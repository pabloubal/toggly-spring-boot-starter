package io.github.pabloubal.togglysbstarter.core.configuration;

import io.github.pabloubal.togglysbstarter.core.entities.Feature;
import io.github.pabloubal.togglysbstarter.core.interfaces.TogglyEnum;
import io.github.pabloubal.togglysbstarter.core.repositories.TogglyRepository;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class TogglyEnumConfiguration {

  private Class<?> togglyEnumClass;
  private final TogglyConfiguration togglyConfiguration;
  private final TogglyRepository featuresRepository;

  @Autowired
  public TogglyEnumConfiguration(TogglyConfiguration togglyConfiguration, TogglyRepository featuresRepository) {
    this.togglyConfiguration = togglyConfiguration;
    this.featuresRepository = featuresRepository;

    detectEnum();
  }

  /**
   * Check if a user-defined enum exists
   */
  private void detectEnum() {
    ClassPathScanningCandidateComponentProvider scanner = new ClassPathScanningCandidateComponentProvider(false);
    scanner.addIncludeFilter(new AnnotationTypeFilter(TogglyEnum.class));

    this.togglyEnumClass = scanner.findCandidateComponents("").stream()
      .findFirst()
      .map(BeanDefinition::getBeanClassName)
      .map(className -> {
        try {
          return Class.forName(className);
        } catch (ClassNotFoundException e) {
          return null;
        }
      })
      .orElse(null);

    addFeaturesFromEnum(togglyEnumClass);
  }

  private void addFeaturesFromEnum(Class<?> featureEnum) {
    Map<String, Feature> tmpFeatures = new HashMap<>();

    Optional.ofNullable(featureEnum)
      .ifPresent(clazz ->
        getStaticStringFields(clazz)
          .forEach(f -> {
            try {
              tmpFeatures.put(
                (String)f.get(null),
                Feature.builder().name(f.getName()).enabled(togglyConfiguration.getGlobal().isEnabledByDefault()).build()
              );
            } catch (IllegalAccessException ignore) {
            }
          })
      );

    featuresRepository.addOrUpdateFeatures(tmpFeatures);
  }

  /**
   * Returns all "public static final String" attributes of the class
   *
   * @param featuresEnum
   * @return
   */
  private List<Field> getStaticStringFields(Class<?> featuresEnum) {
    return Arrays.stream(featuresEnum.getFields())
      .filter(f -> java.lang.reflect.Modifier.isPublic(f.getModifiers()) &&
        java.lang.reflect.Modifier.isStatic(f.getModifiers()) &&
        java.lang.reflect.Modifier.isFinal(f.getModifiers()) &&
        f.getType() == String.class)
      .collect(Collectors.toList());
  }

}
