To run project manually:

mvn clean install

mvn spring-boot:run -pl sea

mvn spring-boot:run -pl air

mvn spring-boot:run -pl ground

mvn spring-boot:run -pl quotations

mvn spring-boot:run -pl ordering

mvn spring-boot:run -pl tracking

mvn exec:java -pl client

---

To run the project using docker:

mvn clean install

docker compose build 

docker compose up

mvn exec:java -pl client
