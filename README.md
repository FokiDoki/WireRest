## WireRest - REST API for Wireguard
![Jenkins JaCoCo Building status](https://img.shields.io/jenkins/build?jobUrl=http%3A%2F%2Fs2.fokidoki.su%2Fjob%2Fwg_controller_master&style=flat-square&t=1)
![Jenkins JaCoCo Tests Coverage](https://img.shields.io/jenkins/coverage/apiv4?jobUrl=http%3A%2F%2Fs2.fokidoki.su%2Fjob%2Fwg_controller_master&style=flat-square)
---
WireRest is a powerful, restful stateless API for Wireguard. With built-in caching, it is optimized to work with large configurations. You can use both for your small private server and for public high-load applications. It is written in Java using Spring Boot and Spring WebFlux.

### Features:

--- 

- Get all peers (Sorting available)
- Automatically create and add peer
- Manually create and add peer
- Delete peer
- Update peer
- Get peer by public key
- Get interface configuration
- Get general config (interface) information
- Token authentication

### How to run:

---
**[!!] Java 20 required** ([How to install](#how-to-install-java-20))

Syntax:
```shell
java -jar wirerest-0.5.jar --parameter=value --parameter2=value2 ...
```
Simple run:
```shell
java -jar wirerest-0.5.jar --wg.interface.name=wg0
```
Default port is 8081.

If the launch was successful, you can check the list of available methods and parameters in swagger at http://SERVER-IP:8081/swagger-ui


### Available run parameters:

All parameters that are set as an example are the default values 


* `--wg.interface.name=wg0` - Wireguard interface name
* `--security.token=admin` - Token for access to API (Change it! If you want to disable token auth, set it to empty string). [More info](#token-authentication)
* `--server.port=8081` - WireRest port
* `--wg.cache.enabled=true` - Enable or disable caching (true is recommend). More info about caching [here](#caching)
* `--wg.interface.default.mask=32` - Mask for ip of new peers
* `--wg.interface.default.persistent-keepalive=0` - Default persistent keepalive for new clients
* `--wg.cache.update-interval=60` - Cache update interval (seconds). it is needed to track changes that have occurred bypassing WireRest. A shorter interval can increase CPU usage. Be careful with this parameter
* `--logging.api.max-elements=1000` - The maximum number of logs that will be saved for access to them through the API

### Token authentication
Each request to the API must contain a token. 
The token is set in the `--security.token` parameter. 
If you want to disable token auth, set it to empty.

Token can be passed in two ways:
1. As a query parameter like `/v1/peers?token=TOKEN` (For POST requests, the token must be passed in the body)
2. As a header (Basic access authentication): `Authorization: Basic TOKEN`


### Caching
WireRest has built-in caching. It is enabled by default.
Caching greatly improves performance on large configurations.
The cache is updated every `--wg.cache.update-interval` seconds (default 60 seconds). \
Quick overview: 
* `transferRx`, `transferTx` and `latestHandshake` fields are updated after every sync
* Peer creation, deletion and update operations work instantly
* If you update the wireguard configuration bypassing WireRest, the changes will appear in WireRest during the next sync

It's recommended to use caching, but if you want to disable it, set `--wg.cache.enabled=false`
## Examples:

### Get all peers:
```shell
curl -X 'GET' \
  'http://localhost:8081/v1/peers?limit=1' \
  -H 'accept: application/json'
```
```json
{
  "totalPages": 100,
  "currentPage": 0,
  "content": [
    {
      "publicKey": "ALD3x7qWP0W/4zC26jFozxw28vXJsazA33KnHF+AfHw=",
      "presharedKey": "3hFqZXqzO+YkVL4nX2siavxK1Z3h5lRLkEQz1qf3giI=",
      "endpoint": "123.23.2.3:55412",
      "allowedSubnets": [
        "10.1.142.196/32",
        "2002:0:0:1234::/64"
      ],
      "latestHandshake": 1690200786,
      "transferRx": 12345,
      "transferTx": 54321,
      "persistentKeepalive": 25
    }
  ]
}
```
---
### Create peer automatically:
```shell
curl -X 'POST' \
  'http://localhost:8081/v1/peers' \
  -H 'accept: application/json'
```
```json
{
  "publicKey": "cHViREF4TWc9PUZha2VQdWJLZXkgICAgICAyMDAxMw==",
  "presharedKey": "RmFrZVBza0tleUZha2VQc2tLZXkgICAgICAyMDAxNA==",
  "privateKey": "RmFrZVBydktleUZha2VQcnZLZXkgICAgICAyMDAxMg==",
  "allowedSubnets": [
    "10.0.0.9/32"
  ],
  "persistentKeepalive": 0
}
```
---
### Create peer with custom parameters:
```shell
curl -X 'POST' \
  'http://localhost:8081/v1/peers?publicKey=RmFrZVBydktleUZha2VQcnZLZXkgICAgICAyMDAxNQ%3D%3D&presharedKey=RmFrZVBza0tleUZha2VQc2tLZXkgICAgICAyMDAxNw%3D%3D&privateKey=cHViREF4TlE9PUZha2VQdWJLZXkgICAgICAyMDAxNg%3D%3D&allowedIps=10.0.0.32%2F32&allowedIps=2002%3A0%3A0%3A1234%3A%3A%2F64&persistentKeepalive=25' \
  -H 'accept: application/json'
````
```json
{
  "publicKey": "RmFrZVBydktleUZha2VQcnZLZXkgICAgICAyMDAxNQ==",
  "presharedKey": "RmFrZVBza0tleUZha2VQc2tLZXkgICAgICAyMDAxNw==",
  "privateKey": "cHViREF4TlE9PUZha2VQdWJLZXkgICAgICAyMDAxNg==",
  "allowedSubnets": [
    "2002:0:0:1234::/64",
    "10.0.0.32/32"
  ],
  "persistentKeepalive": 25
}
```
---
#### Delete peer:
```shell
curl -X 'DELETE' \
  'http://localhost:8081/v1/peers?publicKey=AhM4WLR7ETzLYDQ0zEq%2F0pvbYAxsbLwzzlIAdWhR7yg%3D' \
  -H 'accept: application/json'
  ```
```json
{
  "publicKey": "AhM4WLR7ETzLYDQ0zEq/0pvbYAxsbLwzzlIAdWhR7yg=",
  "presharedKey": "3hFqZXqzO+YkVL4nX2siavxK1Z3h5lRLkEQz1qf3giI=",
  "endpoint": "123.23.2.3:55412",
  "allowedSubnets": [
    "10.1.142.196/32",
    "2002:0:0:1234::/64"
  ],
  "latestHandshake": 1690200786,
  "transferRx": 12345,
  "transferTx": 54321,
  "persistentKeepalive": 25
}
```
---
#### Update peer:
```shell
curl -X 'PATCH' \
  'http://localhost:8081/v1/peers?publicKey=cHViREF4T0E9PUZha2VQdWJLZXkgICAgICAyMDAxOQ%3D%3D&presharedKey=%2BEWn9NeR2pVuFHihYMC6LKreccd5VIW4prUkzHLy0nw%3D' \
  -H 'accept: application/json'
  ```
```json
{
  "publicKey": "cHViREF4T0E9PUZha2VQdWJLZXkgICAgICAyMDAxOQ==",
  "presharedKey": "+EWn9NeR2pVuFHihYMC6LKreccd5VIW4prUkzHLy0nw=",
  "endpoint": "123.123.123.123:12345",
  "allowedSubnets": [
    "10.0.0.11/32"
  ],
  "latestHandshake": 1690200786,
  "transferRx": 12345,
  "transferTx": 54321,
  "persistentKeepalive": 25
}
```
---
#### Get peer by public key:
```shell
curl -X 'GET' \
  'http://localhost:8081/v1/peers/find?publicKey=cHViREF4T0E9PUZha2VQdWJLZXkgICAgICAyMDAxOQ%3D%3D' \
  -H 'accept: application/json'
  ```
```json
{
  "publicKey": "cHViREF4T0E9PUZha2VQdWJLZXkgICAgICAyMDAxOQ==",
  "presharedKey": "+EWn9NeR2pVuFHihYMC6LKreccd5VIW4prUkzHLy0nw=",
  "endpoint": "123.123.123.123:12345",
  "allowedSubnets": [
    "10.0.0.11/32"
  ],
  "latestHandshake": 1690200786,
  "transferRx": 12345,
  "transferTx": 54321,
  "persistentKeepalive": 25
}
```
---
```shell
curl -X 'GET' \
  'http://localhost:8081/v1/interface' \
  -H 'accept: application/json'
```
```json
{
  "privateKey": "RmFrZVBydktleUZha2VQcnZLZXkgICAgICAgICAgMA==",
  "publicKey": "cHViQ0FnTVE9PUZha2VQdWJLZXkgICAgICAgICAgMg==",
  "listenPort": 16666,
  "fwMark": 0
}
```


#### Get logs of service:
```shell
curl -X 'GET' \
  'http://localhost:8081/v1/service/logs?from=0&limit=100' \
  -H 'accept: application/json'
  ````
```json
[
  {
    "level": "INFO",
    "message": "Init duration for springdoc-openapi is: 529 ms",
    "timestamp": 1690301255231
  },
  {
    "level": "ERROR",
    "message": "Peer with public key ALD3x7qWP0W/4zC26jFozxw28vXJsazA33KnHF+AfHw= not found",
    "timestamp": 1690301333239
  }
]
```

These are all just examples, more parameters and example responses can be found in your version of swagger ui


### How to build:

--- 

```shell
mvn clean package
```


### How to install Java 20

---

#### For x64 systems:
```shell
sudo apt-get update
wget https://download.oracle.com/java/20/latest/jdk-20_linux-x64_bin.deb
sudo dpkg -i jdk-20_linux-x64_bin.deb
```
\
If you get an error `dpkg: error processing package jdk-20` \
Run this command:
```shell
apt --fix-broken install
```
And then run dpkg again ```sudo dpkg -i jdk-20_linux-x64_bin.deb``` 

#### For arm64 systems:
```shell
wget https://download.oracle.com/java/20/latest/jdk-20_linux-aarch64_bin.rpm
sudo rpm -i jdk-20_linux-aarch64_bin.rpm
```

#### Add JAVA_HOME to your environment variables (for all systems):
If you're not root:
```shell
sudo nano /etc/profile
```
If you're root:
```shell
nano ~/.bashrc
```

Add this lines to the end of the file:
```shell
export JAVA_HOME="/usr/lib/jvm/jdk-20/"
export PATH=$JAVA_HOME/bin:$PATH
```
Save and exit the file, then **relogin**

Then check if java is installed:
```shell
java -version
```


You can find tar archive with java 20 [here](https://www.oracle.com/java/technologies/downloads/)
### TODO:


- Callback API 
