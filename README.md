# SpringBoot

>  It requires Gradle 6.8, 6.9, or 7.x.

https://docs.spring.io/spring-boot/docs/current/reference/html/build-tool-plugins.html#build-tool-plugins.gradle

https://docs.spring.io/spring-boot/docs/2.5.0/gradle-plugin/reference/htmlsingle/

## 启动程序

```shell
nohup java -Xms512m -Xmx512m -Xmn256m -Dspring.profiles.active=pro -jar xxx.jar > ./nohup-out.log 2>&1 &
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

# 使用

```
git fetch git@gitee.com:angcyo/SpringCore.git
git clone git@gitee.com:angcyo/SpringCore.git
```