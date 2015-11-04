#Example of portlet with Vaadin + Spring Boot for Liferay

## How to deploy this portlet to Liferay:

###1. Download and run Liferay.

###2. Configure Maven properties:
```
liferay.version
liferay.maven.plugin.version
liferay.auto.deploy.dir
liferay.app.server.deploy.dir
liferay.app.server.lib.global.dir
liferay.app.server.portal.dir
```

###Example for Liferay 6.2 GA4:
```
<liferay.version>6.2.3</liferay.version>
<liferay.maven.plugin.version>6.2.10.13</liferay.maven.plugin.version>
<liferay.auto.deploy.dir>/data/Java/extensions/liferay-portal-6.2-ce-ga4/deploy</liferay.auto.deploy.dir>
<liferay.app.server.deploy.dir>/data/Java/extensions/liferay-portal-6.2-ce-ga4/tomcat-7.0.42/webapps</liferay.app.server.deploy.dir>
<liferay.app.server.lib.global.dir>/data/Java/extensions/liferay-portal-6.2-ce-ga4/tomcat-7.0.42/lib/ext</liferay.app.server.lib.global.dir>
<liferay.app.server.portal.dir>/data/Java/extensions/liferay-portal-6.2-ce-ga4/tomcat-7.0.42/webapps/ROOT</liferay.app.server.portal.dir>
```

A good way to define these is for example to create a liferay profile to your [.m2/settings.xml](https://gist.github.com/mstahv/63cf73964ea053c6961a) file.

### 3. Create package and deploy
```
mvn package liferay:deploy
```

### Optional: use different database

The example uses in memory h2 database by default. To switch into another database, ensure you have proper drivers on your classpath and define the database url in your [applicaiton.properties](https://github.com/mstahv/spring-data-vaadin-portlet/blob/master/src/main/resources/application.properties#L5-L9) file.
