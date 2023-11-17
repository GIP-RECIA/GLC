# GLC

Gestion locale des comptes.

## Requirements

- [nvm](https://github.com/nvm-sh/nvm#installing-and-updating) or node 20
- [sdkman](https://sdkman.io/install) or java 11 temurin

## Prepare environment

```bash
./init.sh
```

Complete the `.env.local` (located at root) file and the `application-dev.yml` file (located in `src/main/resources/config`).

## Dev

**Frontend**

```bash
yarn dev
```

**Backend**

```bash
./mvnw clean spring-boot:run -P dev
```

## Build

```bash
./build.sh
```
