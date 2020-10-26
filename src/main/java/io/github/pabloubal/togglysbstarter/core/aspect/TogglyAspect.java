package io.github.pabloubal.togglysbstarter.core.aspect;

import io.github.pabloubal.togglysbstarter.core.TogglyManager;
import io.github.pabloubal.togglysbstarter.core.errors.FeatureToggleCantLocateAOPBeanException;
import io.github.pabloubal.togglysbstarter.core.interfaces.Toggly;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.Optional;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

/**
 * Aspect for processing @Toggly annotated methods as well as @TogglyFallback annotated classes.
 * <p>
 * Checks whether it should proceed with the current method or fallback to the alternative one (if provided)
 * <p>
 * In order to fallback, class should be annotated with @TogglyFallback(FallbackClass.class)
 *
 * @author Pablo Ubal - pablo.ubal@gmail.com
 */
@Aspect
@Component
public class TogglyAspect {

  private static final String ERROR_MESSAGE = "Fallback method contains annotations which are proxied through Spring AOP, but couldn't find any bean on ApplicationContext";
  //NEED TO BE CONTEXT AWARE IN ORDER TO BE ABLE TO PROCESS SPRING AOP ANNOTATIONS THROUGH PROXY BEAN
  private final ApplicationContext context;

  @Autowired
  public TogglyAspect(ApplicationContext context) {
    this.context = context;
  }

  @Around("@annotation(feature)")
  public Object enterIfFeatureIsActive(ProceedingJoinPoint joinPoint, Toggly feature) throws Throwable {
    if (TogglyManager.getInstance().isActive(feature.value())) {
      return joinPoint.proceed();
    } else {
      Class<?> fallbackClass = feature.fallbackClass().length > 0 ? feature.fallbackClass()[0] : null;

      if (fallbackClass == null) {
        return null;
      }
      Method mainMethod = ((MethodSignature) joinPoint.getSignature()).getMethod();
      Method fallbackMethod = fallbackClass.getMethod(mainMethod.getName(), mainMethod.getParameterTypes());
      Object targetBean = getTargetBeanWithoutSpringProxy(fallbackClass);
      return fallbackMethod.invoke(targetBean, joinPoint.getArgs());
    }
  }

  private Object getTargetBeanWithoutSpringProxy(Class<?> fallbackClass) throws FeatureToggleCantLocateAOPBeanException {
    return Optional.of(context.getBeansOfType(fallbackClass))
      .orElse(Collections.emptyMap())
      .values().stream()
      .findFirst()
      .orElseThrow(() -> new FeatureToggleCantLocateAOPBeanException(ERROR_MESSAGE));
  }
}
