package io.github.pabloubal.togglysbstarter.core.repositories;

import io.github.pabloubal.togglysbstarter.core.entities.Feature;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Component;

@Component
public class TogglyRepository {
  private Map<String, Feature> featuresMap;

  public TogglyRepository() {
    featuresMap = new ConcurrentHashMap<>();
  }

  public Feature getFeature(String name) {
    return featuresMap.get(name);
  }

  public Set<String> getAllFeaturesNames() {
    return featuresMap.keySet();
  }

  public void addOrUpdateFeature(Feature feature) {
    Optional.of(feature)
      .ifPresent(f -> featuresMap.put(f.getName(), f));
  }

  public void addOrUpdateFeatures(Map<String, Feature> features) {
    Optional.of(features)
      .ifPresent(f -> featuresMap.putAll(f));
  }

}
