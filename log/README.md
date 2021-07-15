# 2020-11-8

# Logback

http://logback.qos.ch/manual/configuration.html

http://logback.qos.ch/apidocs/index.html

https://juejin.im/entry/6844903705641402376

# Db日志

http://logback.qos.ch/manual/appenders.html#DBAppender


需要创建对应的数据库表结构:
```
logback-classic/src/main/java/ch/qos/logback/classic/db/script

//找到对应的数据库脚本文本运行:
logback-classic-1.2.3.jar!\ch\qos\logback\classic\db\script\mysql.sql
```

