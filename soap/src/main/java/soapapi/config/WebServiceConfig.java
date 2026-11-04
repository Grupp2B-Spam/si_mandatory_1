package soapapi.config;

import java.util.List;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.ws.config.annotation.EnableWs;
import org.springframework.ws.config.annotation.WsConfigurer;
import org.springframework.ws.server.EndpointInterceptor;
import org.springframework.ws.transport.http.MessageDispatcherServlet;
import org.springframework.ws.wsdl.wsdl11.SimpleWsdl11Definition;
import org.springframework.xml.xsd.SimpleXsdSchema;
import soapapi.fault.RequestValidationInterceptor;

@EnableWs
@Configuration
public class WebServiceConfig implements WsConfigurer {

    @Bean
    public ServletRegistrationBean<MessageDispatcherServlet> soapServlet(ApplicationContext context) {
        MessageDispatcherServlet servlet = new MessageDispatcherServlet();
        servlet.setApplicationContext(context);
        // Keep WSDL addresses correct when using a different host or port.
        servlet.setTransformWsdlLocations(true);
        servlet.setTransformSchemaLocations(true);
        return new ServletRegistrationBean<>(servlet, "/soap/*");
    }

    // Bean names determine the URLs: /soap/library.wsdl and /soap/librarySchema.xsd.
    @Bean(name = "library")
    public SimpleWsdl11Definition libraryWsdl() {
        return new SimpleWsdl11Definition(new ClassPathResource("wsdl/library.wsdl"));
    }

    @Bean(name = "librarySchema")
    public SimpleXsdSchema librarySchema() {
        return new SimpleXsdSchema(new ClassPathResource("wsdl/librarySchema.xsd"));
    }

    @Bean
    public RequestValidationInterceptor requestValidationInterceptor() {
        RequestValidationInterceptor validation = new RequestValidationInterceptor();
        validation.setSchema(new ClassPathResource("wsdl/librarySchema.xsd"));
        validation.setValidateRequest(true);
        // Existing shared database rows can contain empty surnames. We validate new input,but do not impose the stricter create/update rules on existing response data.
        validation.setValidateResponse(false);
        return validation;
    }

    @Override
    public void addInterceptors(List<EndpointInterceptor> interceptors) {
        // A Spring bean receives the initialization callback that loads its schema.
        interceptors.add(requestValidationInterceptor());
    }
}
