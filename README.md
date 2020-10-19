# geo-location-java: A project showing the advantages of using Cassandra as a data store for Geo-Locations.

**geo-location-java** This project implements a rest service for location searching from a give latitude and longitude. It returns a set of point of interest around the supplied geo location. The radius and location count are parameters supplied in the request.
This project was implemented using Java 8, Spring-Boot, Spring-Rest, Datastax Cassandra. There is a CQL script defining the table needed to contain a set of locations. There are two classes of CommandLineRunner that take care of loading two datasets from Kaggle.
One for fast food location and another for Starbucks stores. Those load the data upon start. Those loaders use Spring-Batch technology to read and transform the locations into a canonical form that can be used to load in a data store.
The Datastax cassandra drivers are used to load the data into Cassandra. Once loaded the rest service is up and running. The rest service can be access also using a browser.
This interface is REST but it responds with a document that is HTML and the browser can render. Here you can provide a Geo location, and it returns the matching locations that are displayed in a Google map.
## Project Architecture
This project is composed of Source, Sink, and Rest endpoints. Sources are data that provides locations and can come in different formats. Sinks are databases NoSQL or Relational where the data is store. Then the REST services access this type of data and return it to the user.

## Summary
The goal of this project is to compare a NoSql vs a Relational database searching for Geo locations.

