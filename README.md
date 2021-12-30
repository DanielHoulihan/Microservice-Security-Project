To run quotations manually:

mvn spring-boot:run -pl sea

mvn spring-boot:run -pl air

mvn spring-boot:run -pl ground

mvn spring-boot:run -pl quotations

mvn spring-boot:run -pl ordering

mvn exec:java -pl client



This will run all the microservices, get quotations from each of them and make an order with one of them
