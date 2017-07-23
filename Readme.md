This is a Spring Boot REST Service project using the spring,jpa,data and ehcache components.

JPA Entity Classes: Price Objects includes the PricePK as its Composite PrimaryKey

Scheduler Task:A timer task runs once per minute in order to delete values more than 30 days old. 
Since the price service can be accessible concurrently by multiple threads and potentially by multiple simultaneous REST API calls 
and incoming JMS messages, its internal caches and data stores are protected by a Reentrant Read-Write Lock.

Command to start this project: mvn spring-boot:run

Creation of Price Object can be done via
a) REST api call or
b) JMS with a JSON Payload

Class Diagram and the Sequence Diagram included under price-service/diagrams

##Limitations##
a)incoming JSON payload feeds are currently supported, the incoming payload formats can be different for different vendors.
