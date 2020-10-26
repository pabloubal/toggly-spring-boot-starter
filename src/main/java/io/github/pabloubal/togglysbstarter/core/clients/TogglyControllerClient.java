package io.github.pabloubal.togglysbstarter.core.clients;

import io.github.pabloubal.togglysbstarter.core.configuration.TogglyConfiguration;
import io.github.pabloubal.togglysbstarter.core.entities.Feature;
import io.github.pabloubal.togglysbstarter.core.errors.CantRetrieveFeaturesException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Component
public class TogglyControllerClient {
  private final Log logger = LogFactory.getLog(getClass());

  private final String baseUrl;
  private static final String FEATURES_LIST = "/api/features";

  public TogglyControllerClient(TogglyConfiguration configuration) {
    this.baseUrl = configuration.getController().getBaseUrl();
  }

  public List<Feature> getAllFeatures() throws CantRetrieveFeaturesException {
    RestTemplate restTemplate = new RestTemplate();
    String url = baseUrl + FEATURES_LIST;
    try {
      ResponseEntity<Feature[]> response = restTemplate.getForEntity(url, Feature[].class);

      if (!response.getStatusCode().equals(HttpStatus.OK)) {
        throw new CantRetrieveFeaturesException(
          "Toggly controller returned code " + response.getStatusCode().toString());
      }

      return Optional.ofNullable(response.getBody())
        .map(Arrays::asList)
        .orElseThrow(() -> new CantRetrieveFeaturesException(""));
    } catch (RestClientException ex) {
      throw new CantRetrieveFeaturesException(
        new StringBuilder("There's been an error when trying to retrieve all features from ")
          .append(url)
          .append("\nNested error: ")
          .append(ex.getMessage())
          .toString()
      );
    }
  }

}
