package io.github.pabloubal.togglysbstarter.core;

import io.github.pabloubal.togglysbstarter.core.configuration.TogglyConfiguration;
import io.github.pabloubal.togglysbstarter.core.entities.Feature;
import io.github.pabloubal.togglysbstarter.core.repositories.TogglyRepository;
import java.util.Optional;
import org.springframework.stereotype.Component;

/**
 * Main Toggly class. Determines whether a feature is active or not.
 *
 * @author Pablo Ubal - pablo.ubal@gmail.com
 */
@Component
public class TogglyManager {

  private static TogglyManager togglyManager;
  private final TogglyRepository featuresRepo;
  private final TogglyConfiguration togglyConfiguration;

  public TogglyManager(TogglyRepository featuresRepo, TogglyConfiguration togglyConfiguration) {
    this.featuresRepo = featuresRepo;
    this.togglyConfiguration = togglyConfiguration;
    togglyManager = this;
  }

  public static TogglyManager getInstance() {
    return togglyManager;
  }

  public boolean isActive(String featureName) {
    return Optional.of(featuresRepo.getFeature(featureName))
      .map(Feature::isEnabled)
      .orElse(togglyConfiguration.getGlobal().isEnabledByDefault());
  }
}
