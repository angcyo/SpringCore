# 2020-11-6

## Spring Security做JWT认证和授权

https://www.jianshu.com/p/d5ce890c67f7

### spring-security中核心概念

#### AuthenticationManager

用户认证的管理类，所有的认证请求（比如login）都会通过提交一个token给AuthenticationManager的authenticate()方法来实现。当然事情肯定不是它来做，具体校验动作会由AuthenticationManager将请求转发给具体的实现类来做。根据实现反馈的结果再调用具体的Handler来给用户以反馈。这个类基本等同于shiro的SecurityManager。

#### AuthenticationProvider

认证的具体实现类，一个provider是一种认证方式的实现，比如提交的用户名密码我是通过和DB中查出的user记录做比对实现的，那就有一个DaoProvider；如果我是通过CAS请求单点登录系统实现，那就有一个CASProvider。这个是不是和shiro的Realm的定义很像？基本上你可以帮他们当成同一个东西。按照Spring一贯的作风，主流的认证方式它都已经提供了默认实现，比如DAO、LDAP、CAS、OAuth2等。
前面讲了AuthenticationManager只是一个代理接口，真正的认证就是由AuthenticationProvider来做的。一个AuthenticationManager可以包含多个Provider，每个provider通过实现一个support方法来表示自己支持那种Token的认证。AuthenticationManager默认的实现类是ProviderManager。

#### UserDetailService

用户认证通过Provider来做，所以Provider需要拿到系统已经保存的认证信息，获取用户信息的接口spring-security抽象成UserDetailService。虽然叫Service,但是我更愿意把它认为是我们系统里经常有的UserDao。

#### AuthenticationToken

所有提交给AuthenticationManager的认证请求都会被封装成一个Token的实现，比如最容易理解的UsernamePasswordAuthenticationToken。这个就不多讲了，连名字都跟Shiro中一样。

#### SecurityContext

当用户通过认证之后，就会为这个用户生成一个唯一的SecurityContext，里面包含用户的认证信息Authentication。通过SecurityContext我们可以获取到用户的标识Principle和授权信息GrantedAuthrity。在系统的任何地方只要通过SecurityHolder.getSecruityContext()就可以获取到SecurityContext。在Shiro中通过SecurityUtils.getSubject()到达同样的目的。

![](https://upload-images.jianshu.io/upload_images/13282795-62fd0c22e989fa0c.jpg)

# Spring Security

https://spring.io/projects/spring-security

# Demo

https://github.com/spring-projects/spring-security/tree/5.4.1/samples/boot/helloworld

# 使用方法

## 1.注册

```
/auth/register
```

## 2.登录

```
/auth/login
```

## 3.登出

```
/auth/logout
```