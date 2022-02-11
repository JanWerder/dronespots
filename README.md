# dronespots

# Frontend

# Backend

The backend requires variables to be set in the application.yaml.

```
custom:
  db:
    driverClassName: org.postgresql.Driver
    url: jdbc:postgresql://localhost:5432/dronespots
    user: dronespots
    password: [password]
  oauth:
    clientId: [oAuthClientId]
    clientSecret: [oAuthClientSecret]
  mapbox:
    accessToken: [mapboxAccessToken]
  
spring:
  jpa:
    properties:
      hibernate:
        dialect: org.hibernate.dialect.PostgreSQLDialect
        hbm2ddl:
          auto: update
          show_sql: false
        connection:
          autocommit: true
  servlet:
    multipart:
      max-file-size: 50MB
      max-request-size: 50MB
          
logging:
  level:
    root: INFO
    org:
      hibernate: INFO

server:
  port : 5090

```

You can then start the backend with the additional ```--spring.config.location==file:/your.yaml``` parameter to load your configuration.