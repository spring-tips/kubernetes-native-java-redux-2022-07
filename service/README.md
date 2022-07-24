# service

Test reactive with native builder

> http://localhost:8080/customers \
> http://localhost:8080/actuator \
> http://localhost:8080/down \
> http://localhost:8080/slow?time=15_000

```bash
export GRAALVM_HOME=/usr/lib/jvm/graalvm-ce-java17-22.1.0

sudo gu remove native-image
sudo gu install native-image
gu info native-image
gu available 
mvn -DskipTests -Pnative clean package

./target/service
```

> https://github.com/paketo-buildpacks/native-image/blob/main/README.md \
> https://github.com/paketo-buildpacks/java-native-image

```xml
<!-- gcr.io/paketo-buildpacks/java-native-image:7.24.0 -->

<image>
  <buildpacks>
    <buildpack>gcr.io/paketo-buildpacks/java-native-image</buildpack>
  </buildpacks>
  <!--<builder>dashaun/java-native-builder-amd64:7.19.0</builder>-->
  <!--<dbuilder>paketobuildpacks/builder:tiny</builder>-->
  <env>
    <BP_NATIVE_IMAGE>true</BP_NATIVE_IMAGE>
  </env>
</image>
```

```bash
mvn -DskipTests clean spring-boot:build-image

docker images
docker run --rm -it \
 --network host \
 --name native-service \
 docker.io/library/service:1.0.0
```
