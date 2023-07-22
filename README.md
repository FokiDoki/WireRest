## WireRest - REST API for Wireguard 
![Jenkins JaCoCo Building status](https://img.shields.io/jenkins/build?jobUrl=http%3A%2F%2Fs2.fokidoki.su%2Fjob%2Fwg_controller_master&style=flat-square&t=1)
![Jenkins JaCoCo Tests Coverage](https://img.shields.io/jenkins/coverage/apiv4?jobUrl=http%3A%2F%2Fs2.fokidoki.su%2Fjob%2Fwg_controller_master&style=flat-square)
---
This is a REST API for Wireguard. It is written in Java using Spring Boot and Spring MVC. 
Project is under early of development.

Swagger UI is available at http://127.0.0.1:8081/swagger-ui 



### Features:

--- 

- Get interface 
- Get all peers (Sorting available) 
- Get peer by public key
- Automaticly create and add peer
- Manually create and add peer
- Update peer
- Delete peer 

### How to run:

---
Java 20 required
#### Default configuration:

```shell
java -jar wirerest-0.3.jar
```
Server run on port 8081

#### Available run parameters:
```shell
java -jar wire-rest.jar 
    --server.port=8081 # WireRest port
    --wg.interface.name=wg0 # Wireguard interface name
    --wg.interface.new_client_subnet_mask=32 # Mask for ip of new peers
```

### How to build:

--- 

```shell
mvn clean package
```


### TODO:

___
- ~~Migration to Spring WebFlux~~
- Metrics for Prometheus
- ~~Add peer (Custom configuration)~~
- ~~Delete peer~~
- ~~Update peer~~
- Oauth2 authorization
- Authorization key rate limits & scopes 
- ~~Sort peers by key, ip, received, sent, last handshake etc.~~
- Callback API 
- ~~Caching~~
