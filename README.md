# <img src="assets/konsign_icon.png" alt="icon" width="30"/> Konsign Backend

_Checkout frontend project: [here](https://github.com/Razdeep/konsign-ui)_



## Development guide

### Dependencies
 - docker
 - docker-compose
 - java 17 / sdkman
 - gradle

### Setup

```bash
docker-compose up
./gradlew run
```

### Environment variables

| Name              | Value     |
|-------------------|-----------|
| REDIS_HOST        | localhost |
| REDIS_PORT        | 6379      |
| REDIS_USERNAME    | redis     |
| REDIS_PASSWORD    | konsign   |
| POSTGRES_HOST     | localhost |
| POSTGRES_PORT     | 5432      |
| POSTGRES_DBNAME   | konsign   |
| POSTGRES_USERNAME | konsign   |
| POSTGRES_PASSWORD | konsign   |

