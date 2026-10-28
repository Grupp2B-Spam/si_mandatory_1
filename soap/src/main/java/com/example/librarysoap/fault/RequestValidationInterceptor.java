package com.example.librarysoap.fault;

import java.util.Locale;
import org.springframework.ws.context.MessageContext;
import org.springframework.ws.soap.SoapFault;
import org.springframework.ws.soap.SoapMessage;
import org.springframework.ws.soap.server.endpoint.interceptor.PayloadValidatingInterceptor;
import org.xml.sax.SAXParseException;

public class RequestValidationInterceptor extends PayloadValidatingInterceptor {
    @Override
    protected boolean handleRequestValidationErrors(MessageContext context, SAXParseException[] errors) {
        SoapMessage response = (SoapMessage) context.getResponse();
        String message = errors[0].getMessage();
        SoapFault fault = response.getSoapBody().addClientOrSenderFault(message, Locale.ENGLISH);
        SoapFaultResolver.writeDetail(fault, "ValidationFault", message);
        return false; // Stop here: invalid XML must not reach the service/database.
    }
}
