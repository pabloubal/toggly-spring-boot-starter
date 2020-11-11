[![Maven](https://img.shields.io/maven-central/v/io.github.pabloubal/toggly-spring-boot-starter.svg)](https://search.maven.org/search?q=io.github.pabloubal.toggly-spring-boot-starter)

# Toggly's spring boot starter

Intended to be used with [Toggly Controller](https://github.com/pabloubal/toggly-controller)

**Toggly** is a feature toggling platform intended to simplify the feature toggling management. There are [Docker Images Available](https://hub.docker.com/search?q=pabloubal%2Ftoggly) for both [toggly-controller](https://github.com/pabloubal/toggly-controller) and [toggly-ui](https://github.com/pabloubal/toggly-ui), and it's also prepared to be deployed to any Kubernetes cluster through it's [Helm manifest](https://github.com/pabloubal/toggly-helm).

Every feature toggle creates technical debt, that's why it's pretty straight forward to work with Toggly and refactor your code once the feature is stable in production.

## Features!
  - Annotation-driven feature toggling
  - SPI for performing your own customizations

## Importing Dependencies
All artifacts are published to Maven Central.

**Latest Version:** [![Maven](https://img.shields.io/maven-central/v/io.github.pabloubal/toggly-spring-boot-starter.svg)](https://search.maven.org/search?q=io.github.pabloubal.toggly-spring-boot-starter)
### Maven
```
<dependency>
  <groupId>io.github.pabloubal</groupId>
  <artifactId>toggly-spring-boot-starter</artifactId>
  <version>0.1.0</version>
</dependency>
```
### Gradle
```
compile group: 'io.github.pabloubal', name: 'toggly-spring-boot-starter', version: '0.1.0'
```
## Using toggly
The toggly-spring-boot-starter will try to discover a Toggly Enumerator class within the classpath, annotated with the @TogglyEnum annotation, to determine which features the client wants to keep track of.

So, you need to create a Toggly Enumerator class containing your features like the following:
```
@TogglyEnum
public abstract class ToggleEnum{
    public static final String TestFeature="TestFeature";
}
```

Then, encapsulate inside a method the business logic you'd like to surround with a feature toggle. i.e.:
```
@Toggly(ToggleEnum.TestFeature)
public String test() {
  return "Toggle enabled";
}
```

## Fallback
In case a feature is disabled, we might want the application to take a differen path. For this purpose, toggly implements a fallback strategy, similar to the one OpenFeign implements.

The same @Toggly annotation accepts the fallbackClass argument, which is used to determine the class that implements the fallback method. By default, Toggly expects to find a method with the same signature in the Fallback class.

For instance:
```
@Service
public class TestService implements ITestService{
  @Override
  @Toggly(value=ToggleEnum.TestFeature, fallbackClass = TestServiceFallback.class)
  public String test() {
    return "Toggle enabled";
  }
}

@Component
public class TestServiceFallback implements ITestService {
  @Override
  public String test() {
    return "Toggle disabled";
  }
}
```
>**NOTE: There's no need to implement the same interface on both classes, but they DO need to implement the same method signature.**

When Toggly detects an `@Toggly` annotated method which feature is disabled, it will check if the fallbackClass was specified. If it's present, then it will look for a Bean corresponding to the Fallback class and redirect the method call to that Bean.
If it's NOT present, it will just return **NULL**.

## Alternative usage
Instead of using @Toggly annotation, you can inject the TogglyManager bean and check for the feature status yourself. For instance:

```
@Service
public class TestService implements ITestService{
  @Autowired
  TogglyManager togglyManager;
  
  @Override
  public String test() {
    if (togglyManager.isActive(ToggleEnum.TestFeature))
      return "Toggle enabled";
    else
      return "Toggle disabled";
  }
}
```

## Technical Debt
When you're done testing your feature and you know it's ready for production, all you need to do is:
* Remove corresponding `@Toggly` annotation
* Delete the fallback class/method if no other Feature is using it

So, your application should finally look like:
```
public class TestService implements ITestService{
    @Override
    public String test() {
        return "TestService";
    }
}
```

## Custom configuration
The toggly spring-boot starter behavior can be customized through application properties.

- **toggly.global.enabledByDefault** (boolean): determines whether the features are enabled by default or not, in case any of them is not defined in the toggly controller. ***Default value: true***
- **toggly.global.missingTogglesBackToDefault** (boolean): whether missing toggles should fallback to the ***enabledByDefault*** value. ***Default value: true***
- **toggly.scrapper.rate** (integer): frequency, expressed in ms, between two subsequent calls to toggly controller to retrieve the status of the features. ***Default value: 60000***
- **toggly.controller.baseUrl** (string): base url of the toggly controller. ***Default value: http://localhost:4000***

Sample configuration:
```
toggly:
  global:
    enabledByDefault: false
    missingTogglesBackToDefault: true
  scrapper:
    rate: 10000
  controller:
    baseUrl: http://toggly.test/controller
```

## TO-DO

### Activation-Strategies 
Implement activation strategies, such as Oauth roles, list of users, etc
### Analytics
Send information to toggly controller every time a toggle is checked, in order to create reports.
### Enable status override through headers
Add support for overriding feature status through a custom http header. For instance:
```
curl http://myapp.com \
 -H 'toggly-features-enabled: feature1, feature2' \
 -H 'toggly-features-disabled: feature3, feature4' 
```