package com.kodewala.sms.config;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;

@Configuration
public class OpenApiConfig {

	@Bean
	public OpenAPI customOpenAPI() {
	    return new OpenAPI()
	            .info(new Info()
	                    .title("Student Management System API")
	                    .version("1.0.0")
	                    .description("REST APIs for Student Management System")
	                    .contact(new Contact()
	                            .name("Gourav Chopra")))
	            .components(
	                    new Components()
	                            .addSecuritySchemes(
	                                    "bearerAuth",
	                                    new SecurityScheme()
	                                            .type(SecurityScheme.Type.HTTP)
	                                            .scheme("bearer")
	                                            .bearerFormat("JWT")
	                                            .description("JWT authentication token")
	                            )
	            )
	            .security(
	                    List.of(
	                            new SecurityRequirement()
	                                    .addList("bearerAuth")
	                    )
	            );
	}
}