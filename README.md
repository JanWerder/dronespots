# dronespots

# Stack

- Frontend: Vue (2.x)
- Backend: Spring Boot
- Database: PostgresSQL 

# Frontend

In the build process of the frontend the oAuth Client ID is inserted. You'll need to set the VUE_APP_CLIENT_ID enviroment variable during the build process. For more information see the GitHub Actions section.

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

# Github Actions

The build process of this project is fully automatic from building to deployment for both the frontend and the backend. To avoid any secret being exposed in this repo most of the secrets are inserted via configuration, but a few secret need to set for the Github Action to work:

```
CLIENT_ID - The oAuth Client ID for the frontend
DEPLOYMENT_USER - The user which should be used to deploy frontend & backend to the server
DEPLOY_SSH_KEY - The ssh-key to use for the deployment
SERVER_IP - The IP or domain to which the frontend & backend should be deployed to
```