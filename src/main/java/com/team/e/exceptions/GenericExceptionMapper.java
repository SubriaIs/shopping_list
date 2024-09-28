package com.team.e.exceptions;

import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceException;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Provider
@Produces(MediaType.APPLICATION_JSON)
public class GenericExceptionMapper implements ExceptionMapper<Throwable> {

    // Manually creating the logger

    @Override
    public Response toResponse(Throwable exception) {

        if (exception instanceof SLServiceException) {
            return buildResponse(Response.Status.fromStatusCode(
                            ((SLServiceException) exception).getHttpRequestCode()),
                    ((SLServiceException) exception).getError(),
                    exception.getMessage()
            );
        } else if (exception instanceof NoResultException) {
            return buildResponse(Response.Status.NOT_FOUND, "Resource Not Found", exception.getMessage());
        } else if (exception instanceof IllegalArgumentException) {
            return buildResponse(Response.Status.BAD_REQUEST, "Invalid Request", exception.getMessage());
        } else if (exception instanceof PersistenceException) {
            return buildResponse(Response.Status.INTERNAL_SERVER_ERROR, "Database Error", exception.getMessage());
        } else {
            return buildResponse(Response.Status.INTERNAL_SERVER_ERROR, "Internal Server Error", exception.getMessage());
        }
    }

    private Response buildResponse(Response.Status status, String error, String message) {
        return Response.status(status)
                .entity(new ErrorResponse(error, message))
                .type(MediaType.APPLICATION_JSON)
                .build();
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ErrorResponse {
        private String error;
        private String message;
    }
}
