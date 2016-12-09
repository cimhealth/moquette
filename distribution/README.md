## Quick Start

edit /src/main/resources

cd %moquette_home%

mvn clean install -Dmaven.test.skip=true

cd %moquette_home%/distribution

mvn assembly:assembly -Dmaven.test.skip=true

java -jar %moquette_home%/distribution/target/distribution-0.8.1-server-rms.jar