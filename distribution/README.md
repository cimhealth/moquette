## Quick Start

edit %moquette_home%distribution/src/main/resources/moquette_redis.properties

```
cd %moquette_home%
mvn clean install -Dmaven.test.skip=true
cd %moquette_home%distribution
mvn assembly:assembly -Dmaven.test.skip=true
java -jar %moquette_home%/distribution/target/distribution-0.8.1-server-rms.jar
```