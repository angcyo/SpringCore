# 2021-12-24

Spring表达式语言全称为“Spring Expression Language”，缩写为“SpEL”

[https://juejin.cn/post/6921491842865299469](https://juejin.cn/post/6921491842865299469)

[https://www.baeldung.com/spring-expression-language](https://www.baeldung.com/spring-expression-language)

## #this 和 #root变量

变量`#this`始终定义和指向的是当前的执行对象。

变量`#root`指向root context object。

## 函数

有时候你可能需要自定义一些工具函数在表达式中使用，可以先使用StandardEvaluationContext.registerFunction方法注册函数，然后在表达式中调用函数

## bean引用

如果解析上下文已经配置，那么bean解析器能够从表达式使用@符号查找bean类，使用&查到FactoryBean对象

## 声明

```kotlin
@Service
class UserService : BaseAutoMybatisServiceImpl<IUserMapper, UserTable>() {

    fun testFun(): Long {
        return nowTime()
    }

    fun testFun2(a: String?, b: Long?, c: String?, d: String?): String {
        return "test:$a $b $c $d"
    }
}
```

## 使用

```kotlin
@AutoFill(spEL = "@userService.testFun2(name, code, 'test', @userService.testFun())")
var test: Long? = null
```

## 解析

```kotlin
//使用Spring表达式语言（简称SpEL）解析

//创建SpEL表达式的解析器
val parser: ExpressionParser = SpelExpressionParser()
val exp = parser.parseExpression(fill.spEL)
//取出解析结果
val context = StandardEvaluationContext(obj)
context.setBeanResolver(BeanFactoryResolver(app))
val result = exp.getValue(context)

L.i(result)
```

## 其他

### QLExpress 

由阿里的电商业务规则、表达式（布尔组合）、特殊数学公式计算（高精度）、语法分析、脚本二次定制等强需求而设计的一门动态脚本引擎解析工具。 在阿里集团有很强的影响力，同时为了自身不断优化、发扬开源贡献精神，于2012年开源。

QLExpress脚本引擎被广泛应用在阿里的电商业务场景，具有以下的一些特性:

- 1、线程安全，引擎运算过程中的产生的临时变量都是threadlocal类型。
- 2、高效执行，比较耗时的脚本编译过程可以缓存在本地机器，运行时的临时变量创建采用了缓冲池的技术，和groovy性能相当。
- 3、弱类型脚本语言，和groovy，javascript语法类似，虽然比强类型脚本语言要慢一些，但是使业务的灵活度大大增强。
- 4、安全控制,可以通过设置相关运行参数，预防死循环、高危系统api调用等情况。
- 5、代码精简，依赖最小，250k的jar包适合所有java的运行环境，在android系统的低端pos机也得到广泛运用。

[https://github.com/alibaba/QLExpress](https://github.com/alibaba/QLExpress)

### Drool

[https://github.com/kiegroup/drools](https://github.com/kiegroup/drools)
