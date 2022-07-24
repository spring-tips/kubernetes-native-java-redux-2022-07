# controllers

Explore k8s api client with native builder

```bash
mvn -DskipTests clean spring-boot:run -Dspring-boot.run.arguments="--namespace=7949-0192-sio-dev-intra --pod=grafana-0"

mvn -DskipTests clean package
java -jar target/controllers-1.0.0.jar --namespace=7949-0192-sio-dev-intra --pod=grafana-0

export GRAALVM_HOME=/usr/lib/jvm/graalvm-ce-java17-22.1.0
mvn -DskipTests -Pnative clean package

./target/controllers --namespace=7949-0192-sio-dev-intra --pod=grafana-0
```
