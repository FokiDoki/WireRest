

This is a dashboard that will allow you to view:
- Number of peers
- How many peers are created in a period of time
- How many peers are removed per time interval
- How many free / total IP addresses (v4) in the interface that wireguard uses
- How much traffic wireguard clients spend
- WireRest API statistics

How to install:

1. Install WireRest on your server (the same one running Wireguard)  [GUIDE](https://github.com/FokiDoki/WireRest#how-to-run)

2. Connect Prometheus to your WireRest

Example
```yaml
- job_name: 'wirerest-demo'
  static_configs:
    - metrics_path: '/actuator/prometheus'
    - authorization:
      credentials_file: '/etc/default/wirerest-demo-token'
    - targets: ['10.0.0.3:8081']
```
\
Do not forget create file with access token or disable it. Default token is "admin" [About token](https://github.com/FokiDoki/WireRest#token-authentication) \
You can check the availability of WireRest as follows:
`curl http://127.0.0.1:8081/actuator/prometheus?token=<YOUR_TOKEN>`

3. Install dashboard ([Download dashboard json](https://raw.githubusercontent.com/FokiDoki/WireRest/master/grafana/grafana_dashboard.json))

Done!

if you find bugs or have a suggestion for improvement, please write about it in [Github issue](https://github.com/FokiDoki/WireRest)
