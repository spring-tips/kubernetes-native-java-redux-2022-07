# edge

Test cloud gateway with reactive and graphql

> http://localhost:9999/proxy \
> http://localhost:9999/graphiql?path=/graphql

```bash
mvn -DskipTests clean spring-boot:run
```

```graphql
query {
  customers {
    id, name
  }
}
```

```graphql
query {
  customers {
    profile {
      id
    }
    id, name
  }
}
```
