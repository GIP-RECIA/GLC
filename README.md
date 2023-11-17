# GLC

## Requirements

- [nvm](https://github.com/nvm-sh/nvm#installing-and-updating) or node 20
- [sdkman](https://sdkman.io/install) or java 11 temurin

## Prepare environment

**Frontend**

```bash
nvm install
npm i -g yarn
yarn
yarn prepare
yarn predev
```

Complete the `.env.local` file.

**Backend**

```bash
sdk install java 11.0.21-tem
sdk use java 11.0.21-tem
cp src/main/resources/config/application-dev.example.yml src/main/resources/config/application-dev.yml
```

Complete the `application-dev.yml` file.

## Dev

**Frontend**

```bash
yarn dev
```

**Backend**

```bash
./mvnw clean spring-boot:run -P dev"
```

## Build

**Frontend**

```bash
yarn build
```

**Backend**

```bash
./mvnw clean package -P prod -Dmaven.test.skip=true -Darguments="-DskipTests -Dmaven.deploy.skip=true"
```
