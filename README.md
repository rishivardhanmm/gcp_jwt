# gap_oauth_jwt

To generate a JAR file and execute the JAR, run the following Maven command in the root folder:

```bash
mvn clean package shade:shade
```

Make sure the config.properties file is placed in the same folder of the jar located

```bash
java -jar target/gauth-0.0.1-SNAPSHOT-shaded.jar
```
