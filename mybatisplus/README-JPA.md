# 2020-11-4

# Spring Data JPA

https://spring.io/projects/spring-data-jpa

根Module配置插件:

```
plugins {
    val kotlinVersion = "1.4.10"
    ...
    kotlin("plugin.jpa") version kotlinVersion
    ...
}
```

子Module使用插件:

```
apply(plugin = "org.jetbrains.kotlin.plugin.jpa")
```

# 配置数据源

https://docs.spring.io/spring-data/jpa/docs/2.3.5.RELEASE/reference/html/#jpa.java-config