package com.f_lab.joyeuse_planete.foods.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.client.ClientConfiguration;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchConfiguration;

@Configuration
public class ElasticsearchConfig extends ElasticsearchConfiguration {

  @Value("${spring.data.elasticsearch.host}")
  private String ELASTICSEARCH_HOST;

  @Value("${spring.data.elasticsearch.user}")
  private String ELASTICSEARCH_USER;

  @Value("${spring.data.elasticsearch.password}")
  private String ELASTICSEARCH_PASSWORD;

  @Override
  public ClientConfiguration clientConfiguration() {
    return ClientConfiguration.builder()
        .connectedTo(ELASTICSEARCH_HOST)
        .withBasicAuth(ELASTICSEARCH_USER, ELASTICSEARCH_PASSWORD)
        .build();
  }
}
