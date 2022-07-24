# configuration

Explore ways to set configurations (Envs, params, k8s configmaps)

```bash
k apply -f message_config.yml
k get configmaps/edge -o json

mvn -DskipTests clean spring-boot:run
```
