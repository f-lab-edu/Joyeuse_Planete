package com.f_lab.joyeuse_planete.orders.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

  @Bean
  public OpenAPI openAPI() {
    return new OpenAPI()
        .info(apiInfo());
  }

  private Info apiInfo() {
    return new Info()
        .title("La-Planète API")
        .description("API for La-Planète, a food saving app")
        .version("1.0.0")
        .contact(creatorContact());
  }

  private Contact creatorContact() {
    return new Contact()
        .name("Gunho Ryu")
        .email("paulyu1998@gmail.com");
  }
}
