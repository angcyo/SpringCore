# SpringBoot

## 启动程序

```shell
nohup java -Xms512m -Xmx512m -Xmn256m -Dspring.profiles.active=pro -jar xxx.jar > ./nohup-out.log &
```

## 实时查看日志

```shell
tail -fn 200 nohup-out.log
```

```shell
cat nohup-out.log
```

## 查询程序

```shell
ps -ef|grep java
```

```shell
netstat -nat | grep 9201
```


# swagger 文档

http://localhost:9201/swagger-ui/index.html