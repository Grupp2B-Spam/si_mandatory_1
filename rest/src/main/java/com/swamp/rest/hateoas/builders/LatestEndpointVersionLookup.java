package com.swamp.rest.hateoas.builders;

import com.swamp.rest.hateoas.models.HateoasLink;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;
import org.springframework.web.servlet.mvc.condition.PathPatternsRequestCondition;
import org.springframework.web.servlet.mvc.condition.RequestMethodsRequestCondition;
import org.springframework.web.util.pattern.PathPattern;
import org.springframework.http.server.PathContainer;
import org.springframework.web.bind.annotation.RequestMethod;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

/** Resolves the newest declared mapping for links across all MVC controllers. */
@Component
public class LatestEndpointVersionLookup {
    private final ObjectProvider<RequestMappingHandlerMapping> handlerMappingProvider;
    private volatile List<VersionedRoute> routes;

    public LatestEndpointVersionLookup(ObjectProvider<RequestMappingHandlerMapping> handlerMappingProvider) {
        this.handlerMappingProvider = handlerMappingProvider;
    }

    public List<Integer> latestVersions(HateoasLink link) {
        String path = URI.create(link.getHref()).getPath();
        RequestMethod method = methodFor(link.getRel().value());
        List<Integer> matchingVersions = routes().stream()
                .filter(route -> route.methods().contains(method))
                .filter(route -> route.path().matches(PathContainer.parsePath(path)))
                .map(VersionedRoute::version)
                .toList();

        if (matchingVersions.isEmpty()) {
            return routes().stream().map(VersionedRoute::version).max(Integer::compareTo)
                    .map(List::of).orElseGet(List::of);
        }


        return matchingVersions.stream().sorted().toList();
    }

    private List<VersionedRoute> routes() {
        List<VersionedRoute> result = routes;
        if (result == null) {
            synchronized (this) {
                result = routes;
                if (result == null) {
                    routes = result = loadRoutes(handlerMappingProvider.getObject());
                }
            }
        }
        return result;
    }

    private List<VersionedRoute> loadRoutes(RequestMappingHandlerMapping handlerMapping) {
        List<VersionedRoute> found = new ArrayList<>();
        handlerMapping.getHandlerMethods().forEach((mapping, handler) -> {
            String version = mapping.getVersionCondition().getVersion();
            if (version == null || version.isBlank()) {
                return;
            }

            PathPatternsRequestCondition paths = mapping.getPathPatternsCondition();
            RequestMethodsRequestCondition methods = mapping.getMethodsCondition();
            if (paths == null) {
                return;
            }

            int parsedVersion = Integer.parseInt(version);
            Set<RequestMethod> requestMethods = methods.getMethods();
            paths.getPatterns().forEach(path -> found.add(new VersionedRoute(path, requestMethods, parsedVersion)));
        });
        return List.copyOf(found);
    }

    private RequestMethod methodFor(String rel) {
        return switch (rel.toLowerCase()) {
            case "create" -> RequestMethod.POST;
            case "update" -> RequestMethod.PUT;
            case "delete" -> RequestMethod.DELETE;
            default -> RequestMethod.GET;
        };
    }

    private record VersionedRoute(PathPattern path, Set<RequestMethod> methods, int version) {
    }
}
