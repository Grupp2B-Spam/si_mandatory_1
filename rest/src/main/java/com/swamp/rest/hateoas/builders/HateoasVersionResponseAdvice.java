package com.swamp.rest.hateoas.builders;

import com.swamp.rest.hateoas.models.HateoasLink;
import com.swamp.rest.hateoas.models.HateoasModel;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Set;

/** Adds latest endpoint versions to every HATEOAS model before serialization. */
@ControllerAdvice
public class HateoasVersionResponseAdvice implements ResponseBodyAdvice<Object> {
    private final LatestEndpointVersionLookup versionLookup;

    public HateoasVersionResponseAdvice(LatestEndpointVersionLookup versionLookup) {
        this.versionLookup = versionLookup;
    }

    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        return true;
    }

    @Override
    public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType,
                                  Class<? extends HttpMessageConverter<?>> selectedConverterType,
                                  ServerHttpRequest request, ServerHttpResponse response) {
        decorate(body, Collections.newSetFromMap(new IdentityHashMap<>()));
        return body;
    }

    private void decorate(Object value, Set<Object> visited) {
        if (!(value instanceof HateoasModel model) || !visited.add(value)) {
            return;
        }

        model.getLinks().replaceAll((rel, link) -> link.withSupportedVersions(versionLookup.latestVersions(link)));
        model.nestedModels().forEach(child -> decorate(child, visited));
    }
}
