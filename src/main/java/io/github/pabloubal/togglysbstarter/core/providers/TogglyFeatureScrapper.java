package io.github.pabloubal.togglysbstarter.core.providers;

import static java.util.Collections.emptyList;

import io.github.pabloubal.togglysbstarter.core.clients.TogglyControllerClient;
import io.github.pabloubal.togglysbstarter.core.configuration.TogglyConfiguration;
import io.github.pabloubal.togglysbstarter.core.entities.Feature;
import io.github.pabloubal.togglysbstarter.core.errors.CantRetrieveFeaturesException;
import io.github.pabloubal.togglysbstarter.core.repositories.TogglyRepository;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * @author Pablo Ubal - pablo.ubal@gmail.com
 */
@Component
public class TogglyFeatureScrapper {
  private Log logger = LogFactory.getLog(getClass());

  private final TogglyRepository featuresRepository;
  private final TogglyConfiguration togglyConfiguration;
  private final TogglyControllerClient togglyControllerClient;

  public TogglyFeatureScrapper(TogglyRepository featuresRepository,
    TogglyConfiguration togglyConfiguration, TogglyControllerClient togglyControllerClient) {
    this.featuresRepository = featuresRepository;
    this.togglyConfiguration = togglyConfiguration;
    this.togglyControllerClient = togglyControllerClient;

  }

  @Scheduled(fixedRateString = "#{togglyConfiguration.getScrapper().getRate()}")
  private void scrape() {
    try {
      Set<String> featNames = featuresRepository.getAllFeaturesNames();
      List<Feature> newFeatures = Optional.of(togglyControllerClient.getAllFeatures()).orElse(emptyList());

      //Add or Update features received from the controller
      newFeatures.stream().filter(f -> featNames.contains(f.getName())).forEach(featuresRepository::addOrUpdateFeature);

      //Move to the default status those features declared in the repository but not returned by the controller
      if(togglyConfiguration.getGlobal().isMissingTogglesBackToDefault()) {
        featNames.stream()
          .filter(featName -> newFeatures.stream().noneMatch(n -> n.getName().equals(featName)))
          .forEach(featName -> featuresRepository.addOrUpdateFeature(
            Feature.builder().name(featName).enabled(togglyConfiguration.getGlobal().isEnabledByDefault()).build()
            )
          );
      }
    } catch (CantRetrieveFeaturesException ex) {
      logger.error(ex.getMessage());
    }
  }
}
