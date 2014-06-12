Readme for VIF Framework Test
=============================

For that the unit tests can be run, the following preconditions have to be fulfilled:
- Table tblTest (table structure see script table_tblTest.sql) has to exist 
  in an accessible MySQL database.
- The access to the database, i.e. the properties 
  org.hip.vif.db.driver, org.hip.vif.db.url, org.hip.vif.db.userId, org.hip.vif.db.password 
  in ./WEB-INF/conf/vif.properties have to be properly specified.
- The class libraries for xalan (xalan.jar), xerces (xercesImpl.jar) and the MySQL-JDBC (mysql-connector-java-3.0.17-ga-bin.jar) 
  have to be on the classpath.