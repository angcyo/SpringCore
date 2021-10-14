# 2021-10-13

工具类

https://www.hutool.cn/

https://www.hutool.cn/docs/#/

https://apidoc.gitee.com/dromara/hutool/

## 仓库地址

https://repo1.maven.org/maven2/cn/hutool/

## 🛠️包含组件
一个Java基础工具类，对文件、流、加密解密、转码、正则、线程、XML等JDK方法进行封装，组成各种Util工具类，同时提供以下组件：

模块 | 介绍
--- | ---
hutool-aop | JDK动态代理封装，提供非IOC下的切面支持
hutool-bloomFilter | 布隆过滤，提供一些Hash算法的布隆过滤
hutool-cache | 简单缓存实现
hutool-core | 核心，包括Bean操作、日期、各种Util等
hutool-cron | 定时任务模块，提供类Crontab表达式的定时任务
hutool-crypto | 加密解密模块，提供对称、非对称和摘要算法封装
hutool-db | JDBC封装后的数据操作，基于ActiveRecord思想
hutool-dfa | 基于DFA模型的多关键字查找
hutool-extra | 扩展模块，对第三方封装（模板引擎、邮件、Servlet、二维码、Emoji、FTP、分词等）
hutool-http | 基于HttpUrlConnection的Http客户端封装
hutool-log | 自动识别日志实现的日志门面
hutool-script | 脚本执行封装，例如Javascript
hutool-setting | 功能更强大的Setting配置文件和Properties封装
hutool-system | 系统参数调用封装（JVM信息等）
hutool-json | JSON实现
hutool-captcha | 图片验证码实现
hutool-poi | 针对POI中Excel和Word的封装
hutool-socket | 基于Java的NIO和AIO的Socket封装
hutool-jwt | JSON Web Token (JWT)封装实现

可以根据需求对每个模块单独引入，也可以通过引入hutool-all方式引入所有模块。

> poi 还需要引入库, `version >= 4.1.2` 

```
// https://mvnrepository.com/artifact/org.apache.poi/poi-ooxml
implementation("org.apache.poi:poi-ooxml:5.0.0")
```


