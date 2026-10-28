package com.example.librarysoap.fault;

import java.util.Locale;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import org.springframework.stereotype.Component;
import org.springframework.ws.context.MessageContext;
import org.springframework.ws.server.endpoint.AbstractEndpointExceptionResolver;
import org.springframework.ws.soap.SoapFault;
import org.springframework.ws.soap.SoapMessage;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

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

    // Use XML nodes so characters such as & and < in a message are escaped correctly.
    public static void writeDetail(SoapFault fault, String faultName, String message) {
        try {
            Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
            Element detail = document.createElementNS(NAMESPACE, "lib:" + faultName);
            Element text = document.createElementNS(NAMESPACE, "lib:message");
            text.setTextContent(message);
            detail.appendChild(text);
            document.appendChild(detail);
            TransformerFactory.newInstance().newTransformer()
                    .transform(new DOMSource(document), fault.addFaultDetail().getResult());
        } catch (Exception exception) {
            throw new IllegalStateException("Could not write SOAP fault", exception);
        }
    }
}
