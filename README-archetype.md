# Prerequises
- Java 8+
- Maven 3.x+

## How to Build
- From your IDE: just build your project
- From command line: ``mvn clean install``

## How to Run
- From IDE: Right click on the class ``Starter`` at the root of your project package and run it.
You can customize it with the following springboot profiles:
  - ``spring.profiles.active``: values are ``dev``, ``ci``, ``test``, ``stress`` or ``prod``
  - See [here](http://docs.spring.io/spring-boot/docs/current/reference/html/howto-properties-and-configuration.html) for more configuration parameters.
- Using Maven: ``mvn spring-boot:run``
