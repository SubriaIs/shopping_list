package com.team.e.filters;

import com.team.e.annotations.TokenRequired;
import com.team.e.exceptions.SLServiceException;
import com.team.e.utils.TokenValidationHelper;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.container.ResourceInfo;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.Provider;

import java.io.IOException;
import java.lang.reflect.Method;

@Provider
public class TokenValidationFilter implements ContainerRequestFilter {

    @Context
    private ResourceInfo resourceInfo; // Injecting ResourceInfo

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        // Get the method being invoked
        Method method = resourceInfo.getResourceMethod();

        // Check for the TokenRequired annotation
        if (method != null && method.isAnnotationPresent(TokenRequired.class)) {
            // Get the xToken header
            String xToken = requestContext.getHeaderString("xToken");

            // Validate the xToken
            if (xToken == null || !isValidToken(xToken)) {
                // If the token is missing or invalid, respond with an unauthorized status
                /*requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED)
                        .entity("Unauthorized: Invalid or missing xToken").build());*/
                throw new SLServiceException("Missing or Invalid token",401,"Provided token is missing or invalid. Check header so that it contains xToken.");
            }
        }
    }

    private boolean isValidToken(String xToken) {
        // Add your logic to validate the xToken
        // For example, check if it matches a specific format or value
        // This is just a placeholder, implement your own validation
        return TokenValidationHelper.IsTokenValid(xToken); // Replace with actual validation logic
    }
}
