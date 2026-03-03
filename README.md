# GLC

Gestion locale des comptes.

- [GLC](#glc)
  - [Prérequis 🚨](#prérequis-)
  - [Setup 🧰](#setup-)
    - [Initialisation 🛠️](#initialisation-️)
    - [License 📔](#license-)
    - [Changelog 🆕](#changelog-)
  - [Run ▶️](#run-️)
  - [Tests 🧪](#tests-)
  - [Build 📦](#build-)

[![Coverage](https://raw.githubusercontent.com/GIP-RECIA/GLC/badges/jacoco.svg)](https://github.com/GIP-RECIA/GLC/actions/workflows/project.yml)
[![Branches](https://raw.githubusercontent.com/GIP-RECIA/GLC/badges/branches.svg)](https://github.com/GIP-RECIA/GLC/actions/workflows/project.yml)

## Prérequis 🚨

- [nvm](https://github.com/nvm-sh/nvm)
- [sdkman](https://sdkman.io)
- [docker](https://www.docker.com)
- mysql-client-core-8.0

## Setup 🧰

### Initialisation 🛠️

```sh
make init
```

Personnalisez les fichiers :

- `.env.local`
- `src/main/resources/config/application-dev.yml`

### License 📔

> ⚠️ docker doit être lancé

```sh
make license-check
make license-generate
```

### Changelog 🆕

```sh
yarn changelog:generate
```

## Run ▶️

**frontend** :

```sh
make frontend
```

**backend** :

```sh
make backend
```

## Tests 🧪

**frontend** :

```sh
yarn test:unit
yarn test:e2e
```

**backend** :

> ⚠️ docker doit être lancé et `mysql-client-core-8.0` installé

```sh
make backend-tests
```

## Build 📦

```sh
make snapshot
make release
```
