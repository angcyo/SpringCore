# 2020-11-7

# Swagger

https://swagger.io/

https://swagger.io/tools/swagger-ui/

https://github.com/swagger-api

https://github.com/swagger-api/swagger-ui

https://swagger.io/specification/

http://hackage.haskell.org/package/swagger2

https://jcenter.bintray.com/io/springfox

# 第三方

https://github.com/springforall/spring-boot-starter-swagger

# 文档地址

## Swagger2

http://localhost:888/swagger-ui.html

## Swagger3

http://localhost:888/swagger-ui/
http://localhost:888/swagger-ui/index.html

## 第三方ui

http://localhost:888/doc.html

# 注解

http://c.biancheng.net/view/5533.html

## @Api

标记一个 Controller 类

```kotlin
@Api(tags = { "用户接口" })
@RestController
class UserController
```

## @ApiModel

用于实体类中的参数接收说明

```kotlin
@ApiModel("新增用户参数")
class AddUserParam
```

## @ApiModelProperty()

用于字段，表示对 model 属性的说明

```kotlin
@ApiModel("新增用户参数")
class AddUserParam {
    @ApiModelProperty("年龄")
    var age: Int
}
```

## @ApiParam

用于 Controller 中方法的参数说明

```kotlin
@PostMapping("/user")
fun addUser(@ApiParam(value = "新增用户参数", required = true) @RequestBody param: AddUserParam): UserDto {
    System.err.println(param.getName())
    return UserDto()
}
```

## @ApiOperation

用在 Controller 里的方法上，说明方法的作用，每一个接口的定义

```kotlin
@ApiOperation(value = "新增用户", notes = "详细描述")
fun addUser(@ApiParam(value = "新增用户参数", required = true) @RequestBody param: AddUserParam): UserDto {

}
```

## @ApiImplicitParam 和 @ApiImplicitParams

用于方法上，为单独的请求参数进行说明

```kotlin
@ApiImplicitParams({
    @ApiImplicitParam(
        name = "id",
        value = "用户ID",
        dataType = "string",
        paramType = "query",
        required = true,
        defaultValue = "1"
    )
})
@ApiResponses({
    @ApiResponse(
        code = 200,
        message = "OK",
        response = UserDto.class)
})
@GetMapping("/user")
fun getUser(@RequestParam("id") id: String): UserDto {
    return UserDto()
}
```



