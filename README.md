# Mikrostoritev za Pošiljanje (microservice-posiljanje)

Ta mikrostoritev je del platforme Nakupify in je odgovorna za upravljanje pošiljk in sledenje dostavi. Povezuje se s ponudniki poštnih storitev (simulirano) in obvešča ostale dele sistema o statusu dostave.

## Tehnološki sklad

- **Ogrodje:** [Quarkus](https://quarkus.io/) (Supersonic Subatomic Java)
- **Podatkovna baza:** PostgreSQL (Hibernate ORM s Panache)
- **Sporočilni sistem:** Apache Kafka (Reactive Messaging)
- **Pakiranje:** Docker, Helm

## Ključne funkcionalnosti

- Ustvarjanje pošiljk za naročila.
- Sledenje statusu pošiljke (npr. V_PRIPRAVI, POSLANO, NA_POTI, DOSTAVLJENO, VRNJENO).
- Pridobivanje podatkov o sledenju preko sledilne številke.
- Obveščanje sistema za naročila o spremembah statusa preko Kafka sporočil.

## API končne točke

### Javni API (`/api/shipments`)

| Metoda | Pot | Opis |
| :--- | :--- | :--- |
| `GET` | `/api/shipments` | Pridobi seznam pošiljk (možnost filtriranja po `orderId`). |
| `GET` | `/api/shipments/{id}` | Pridobi podrobnosti posamezne pošiljke po ID-ju. |
| `GET` | `/api/shipments/{id}/tracking` | Pridobi status sledenja po ID-ju pošiljke. |
| `GET` | `/api/shipments/track/{trackingNumber}` | Pridobi status sledenja po sledilni številki. |

### Interni API (`/internal/shipments`)

| Metoda | Pot | Opis |
| :--- | :--- | :--- |
| `POST` | `/internal/shipments` | Ustvari novo pošiljko za naročilo. |
| `PATCH` | `/internal/shipments/{id}/status` | Posodobi status pošiljke. |

## Razvoj in zagon

### Lokalni zagon v razvojnem načinu

Za zagon aplikacije s podporo za "vroče" ponovno nalaganje kode (live coding) uporabite:

```shell script
./mvnw quarkus:dev
```

Aplikacija bo privzeto dostopna na `http://localhost:8080`. Razvojni vmesnik (Dev UI) je na voljo na `http://localhost:8080/q/dev/`.

### Pakiranje aplikacije

Za pakiranje aplikacije v JAR datoteko:

```shell script
./mvnw package
```

Za izdelavo *über-jar* (vsebuje vse odvisnosti):

```shell script
./mvnw package -Dquarkus.package.jar.type=uber-jar
```

### Izgradnja Docker slike

Aplikacijo lahko zapakirate v Docker sliko z ukazom:

```shell script
docker build -t nakupify/microservice-posiljanje .
```

## Konfiguracija

Konfiguracijski parametri se nahajajo v `src/main/resources/application.properties`. Glavne nastavitve vključujejo:

- `quarkus.datasource.jdbc.url`: Povezava do PostgreSQL baze.
- `mp.messaging.incoming.shipments-in.connector`: Nastavitve za prejemanje Kafka sporočil.

## Avtomatski testi

Za zagon vseh testov uporabite:

```shell script
./mvnw test
```
