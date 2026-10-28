package soapapi.fault;

import java.util.Locale;
import javax.xml.namespace.QName;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBElement;
import soapapi.generated.FaultDetail;
import org.springframework.stereotype.Component;
import org.springframework.ws.context.MessageContext;
import org.springframework.ws.server.endpoint.AbstractEndpointExceptionResolver;
import org.springframework.ws.soap.SoapFault;
import org.springframework.ws.soap.SoapMessage;

@Component
public class SoapFaultResolver extends AbstractEndpointExceptionResolver {
    public static final String NAMESPACE = "http://example.com/library";

    public SoapFaultResolver() {
        setOrder(-1);
    }

    @Override
    protected boolean resolveExceptionInternal(MessageContext context, Object endpoint, Exception exception) {
        SoapMessage response = (SoapMessage) context.getResponse();
        String faultName;
        if (exception instanceof NotFoundException) {
            faultName = "NotFoundFault";
        } else if (exception instanceof ValidationException) {
            faultName = "ValidationFault";
        } else if (exception instanceof ConflictException) {
            faultName = "ConflictFault";
        } else {
            logger.error("Unexpected SOAP error", exception);
            response.getSoapBody().addServerOrReceiverFault("Internal server error", Locale.ENGLISH);
            return true;
        }

        SoapFault fault = response.getSoapBody()
                .addClientOrSenderFault(exception.getMessage(), Locale.ENGLISH);
        writeDetail(fault, faultName, exception.getMessage());
        return true;
    }

    // JAXB writes the fault detail using the same XML type defined in our XSD.
    public static void writeDetail(SoapFault fault, String faultName, String message) {
        try {
            FaultDetail detail = new FaultDetail();
            detail.setMessage(message);
            JAXBElement<FaultDetail> element = new JAXBElement<>(
                    new QName(NAMESPACE, faultName), FaultDetail.class, detail);
            JAXBContext.newInstance(FaultDetail.class).createMarshaller()
                    .marshal(element, fault.addFaultDetail().getResult());
        } catch (Exception exception) {
            throw new IllegalStateException("Could not write SOAP fault", exception);
        }
    }
}
