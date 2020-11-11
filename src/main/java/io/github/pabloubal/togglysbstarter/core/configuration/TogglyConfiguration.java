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
  GlobalConfig global = new GlobalConfig();
  ScrapperConfig scrapper = new ScrapperConfig();
  Controller controller = new Controller();

  @Data
  @NoArgsConstructor
  public static class GlobalConfig {
    boolean enabledByDefault = TogglyDefaultConfigurations.GLOBALCONFIG_ENABLEDBYDEFAULT;
    boolean missingTogglesBackToDefault = TogglyDefaultConfigurations.GLOBALCONFIG_MISSINGTOGGLESBACKTODEFAULT;
  }

  @Data
  @NoArgsConstructor
  public static class ScrapperConfig {
    Integer rate = TogglyDefaultConfigurations.SCRAPPERCONFIG_RATE;

  }

  @Data
  @NoArgsConstructor
  public static class Controller {
    String baseUrl = TogglyDefaultConfigurations.CONTROLLER_BASEURL;
  }

}
