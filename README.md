### sso

> 单点登录, 不支持单点注销

本地调试需要添加 `hosts`
```
127.0.0.1       sso.com
127.0.0.1       adm.com
127.0.0.1       user.com
```

项目域名对应关系
```
sso-server  对应  sso.com
sso-client  对应  adm.com
sso-client2 对应  user.com
```