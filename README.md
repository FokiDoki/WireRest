## WireRest - REST API for Wireguard 
![Jenkins JaCoCo Tests Coverage](https://img.shields.io/jenkins/build?jobUrl=http%3A%2F%2Fs2.fokidoki.su%2Fjob%2Fwg_controller_master&style=flat-square)
![Jenkins JaCoCo Tests Coverage](https://img.shields.io/jenkins/coverage/apiv4?jobUrl=http%3A%2F%2Fs2.fokidoki.su%2Fjob%2Fwg_controller&style=flat-square)
---
This is a REST API for Wireguard. It is written in Java using Spring Boot and Spring MVC. 
Project is under start of development.

Swagger UI is available at http://127.0.0.1:8081/swagger-ui.html



### Features:

--- 

- Get interface 
- Get all peers 
- Get peer by public key
- Add peer (auto-configured)

### How to run:

---
#### Default configuration:

```shell
java -jar wire-rest.jar
```
Server run on port 8081

#### Available run parameters:
```shell
java -jar wire-rest.jar 
    --server.port=8081 # WireRest port
    --wg.interface.name=wg0 # Wireguard interface name
    --wg.interface.subnet=10.66.66.0/24 # Subnet with which wireguard works
    --wg.interface.ip=10.66.66.1 # Ip of wireguard
    --wg.interface.port=51820 # Port of wireguard
    --wg.interface.new_client_subnet_mask=32 # Mask for ip of new peers
```

### How to build:

--- 

```shell
mvn clean package
```


### TODO:

___
- Migration to Spring WebFlux
- Metrics for Prometheus
- Add peer (Custom configuration)
- Delete peer
- Update peer
- Oauth2 authorization
- Sort peers by key, ip, received, sent, last handshake etc. 