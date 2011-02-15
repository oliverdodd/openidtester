OpenId Tester
=============
A Spring MVC based web app for testing OpenID using openid4java, particularly useful for debugging providers running on localhost or on private networks.


Usage
-----
Run the server locally using maven:

	mvn jetty:run

The default port is set to 8081 in the POM.  When the app starts, go to http://localhost:8081 and proceed to test your OpenId provider.
