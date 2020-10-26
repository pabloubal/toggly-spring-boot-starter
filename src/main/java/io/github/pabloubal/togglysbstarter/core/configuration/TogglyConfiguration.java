package io.github.pabloubal.togglysbstarter.core.configuration;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@NoArgsConstructor
@Configuration
@ConfigurationProperties(prefix = "toggly")
public class TogglyConfiguration {
  GlobalConfig global;
  ScrapperConfig scrapper;
  Controller controller;

  @Data
  @NoArgsConstructor
  public static class GlobalConfig {
    boolean enabledByDefault;
    boolean missingTogglesBackToDefault;
  }

  @Data
  @NoArgsConstructor
  public static class ScrapperConfig {
    Integer rate;

  }

  @Data
  @NoArgsConstructor
  public static class Controller {
    String baseUrl;
  }

}
