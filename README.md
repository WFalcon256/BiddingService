# BiddingService

Setup Instructions

Required files:::
in java package testproject.biddingservice:
BidController.java
ProjectController.java

in java package testproject.biddingservice.jpa:
Bid.java
BidRepository.java
Project.java
ProjectRepository.java

in META-INF:
persistence.xml

in WEB-INF:
web.xml

in WEB-INF/spring:
applicationContext.xml

Config changes for Tomcat
The following configurations need to be added to Tomcat's server.xml file:

(inside the GlobalNamingResources tag):
<Resource name="jdbc/biddingdbGlobal" auth="Container" type="javax.sql.DataSource"
               maxTotal="100" maxIdle="30" maxWaitMillis="10000"
               username="***username***" password="***password***" driverClassName="com.mysql.jdbc.Driver"
               url="jdbc:mysql://localhost:3306/bidding"/>

(inside the context tag for the biddingService webapp):
<ResourceLink name="jdbc/biddingdb"
                global="jdbc/biddingdbGlobal"
                auth="Container"
                type="javax.sql.DataSource">
		</ResourceLink>

Many jar files (these should be on the build path for the 
project and in the lib directory in Tomcat).

First install Tomcat 8 and MySQL 8 on your machine.  Add the above noted changes to
Tomcat's server.xml file so the web app can have access to the MySQL database as a 
datasource. The jars should go into Tomcat's lib directory. The webapp should live in 
the BiddingService folder under the Tomcat webapps directory. All of the following 
resources should go inside that folder. The compiled java class files should go into 
the WEB-INF/classes folder inside the appropriate packages.  The persistence.xml file 
should go into the META-INF folder. The web.xml file should go in the WEB-INF folder. 
Finally, the applicationContext.xml file should go in the WEB-INF/spring directory.

Here are the SQL commands to set up the database tables in the MySQL database:
CREATE TABLE `project` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `project_name` varchar(45) NOT NULL,
  `description` varchar(300) DEFAULT NULL,
  `contact_email` varchar(100) NOT NULL,
  `requirements_url` varchar(100) DEFAULT NULL,
  `max_budget` decimal(30,2) DEFAULT NULL,
  `close_date` datetime NOT NULL,
  `lowest_bid` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_lowest_bid_idx` (`lowest_bid`),
  CONSTRAINT `fk_lowest_bid` FOREIGN KEY (`lowest_bid`) REFERENCES `bids` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci

CREATE TABLE `bids` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `project_id` int(11) NOT NULL,
  `bid_amount` decimal(30,2) NOT NULL,
  `contact_email` varchar(100) NOT NULL,
  `name` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_project_id_idx` (`project_id`),
  CONSTRAINT `fk_project_id` FOREIGN KEY (`project_id`) REFERENCES `project` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
