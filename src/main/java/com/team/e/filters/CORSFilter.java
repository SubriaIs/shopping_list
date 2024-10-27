package com.team.e.filters;

import com.team.e.exceptions.SLServiceException;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerResponseContext;
import jakarta.ws.rs.container.ContainerResponseFilter;
import jakarta.ws.rs.core.Response;
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

        // Set allowed methods in response headers
        responseContext.getHeaders().add("Access-Control-Allow-Methods", String.join(", ", ALLOWED_METHODS));

        // Set allowed headers
        responseContext.getHeaders().add("Access-Control-Allow-Headers", "Content-Type, xToken");

        // Set allowed credentials (optional, if needed)
        responseContext.getHeaders().add("Access-Control-Allow-Credentials", "true");

        // Handle OPTIONS preflight request
        if ("OPTIONS".equalsIgnoreCase(requestContext.getMethod())) {
            responseContext.setStatus(Response.Status.NO_CONTENT.getStatusCode());
            return;
        }

        // Check if the request method is allowed, if not, throw an exception
        String requestMethod = requestContext.getMethod();
        if (!ALLOWED_METHODS.contains(requestMethod)) {
            throw new SLServiceException("Method Not Allowed", 405, "HTTP method " + requestMethod + " is not allowed.");
        }
    }
}
