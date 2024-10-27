package com.team.e.filters;

import com.team.e.exceptions.SLServiceException;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerResponseContext;
import jakarta.ws.rs.container.ContainerResponseFilter;
import jakarta.ws.rs.ext.Provider;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Provider
public class CORSFilter implements ContainerResponseFilter {

    // Define allowed HTTP methods
    private static final List<String> ALLOWED_METHODS = Arrays.asList("GET", "POST", "PUT", "DELETE", "PATCH");

    @Override
    public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext) throws IOException {
        // Allow all origins
        responseContext.getHeaders().add("Access-Control-Allow-Origin", "*");

        // Include allowed methods in response
        responseContext.getHeaders().add("Access-Control-Allow-Methods", String.join(", ", ALLOWED_METHODS));

        // Allow specific headers
        responseContext.getHeaders().add("Access-Control-Allow-Headers", "Content-Type, xToken");

        // Check if the request method is allowed
        String requestMethod = requestContext.getMethod();
        if (!ALLOWED_METHODS.contains(requestMethod)) {
            throw new SLServiceException("Method Not Allowed", 405, "HTTP method " + requestMethod + " is not allowed.");
        }
    }
}
