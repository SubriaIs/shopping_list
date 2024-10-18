package com.team.e;

import com.team.e.exceptions.GenericExceptionMapper;
import com.team.e.filters.TokenValidationFilter;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.server.ResourceConfig;


import java.io.IOException;
import java.net.URI;

public class Main {
    public static void main(String[] args) throws IOException {

        final String BASE_URI = "http://0.0.0.0:8082/";
        // Create ResourceConfig and register packages and exception mappers
        final ResourceConfig rc = new ResourceConfig()
                .packages("com.team.e") // Register your resources
                .register(JacksonFeature.class)
                .register(TokenValidationFilter.class)
                .register(GenericExceptionMapper.class);


        final HttpServer server = GrizzlyHttpServerFactory.createHttpServer(URI.create(BASE_URI), rc);
        server.start();
        System.out.println("Jersey app started with endpoints available at " + BASE_URI);
    }
}