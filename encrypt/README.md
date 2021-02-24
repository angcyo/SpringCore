# 2021-2-24

RSA非对称加密

https://gitee.com/ishuibo/rsa-encrypt-body-spring-boot

https://www.jianshu.com/p/760b284fc269

---

- **启动类Application中添加@EnableSecurity注解**

```
@SpringBootApplication
@EnableSecurity
public class DemoApplication {
    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }
}
```

- **在application.yml或者application.properties中添加RSA公钥及私钥**

```
rsa:
  encrypt:
    open: true # 是否开启加密 true  or  false
    showLog: true # 是否打印加解密log true  or  false
    publicKey: # RSA公钥
    privateKey: # RSA私钥
```

- **对返回值进行加密**

```
@Encrypt
@GetMapping("/encryption")
public TestBean encryption(){
    TestBean testBean = new TestBean();
    testBean.setName("shuibo.cn");
    testBean.setAge(18);
    return testBean;
}
```

- **对传过来的加密参数解密**

```
@Decrypt
@PostMapping("/decryption")
public String Decryption(@RequestBody TestBean testBean){
    return testBean.toString();
}
```

- **完整DEMO示例**
- https://github.com/ishuibo/SpringAll/tree/master/05.Spring-Boot-RSA

---

# RSA 生成

https://www.jianshu.com/p/bfa57e049a7e

https://www.fzb.me/2015-1-15-openssl-rsa.html

```shell
# 生成私钥 private.pem
$ openssl genrsa -out private.pem 1024
# 把私钥改成pkcs8 格式
openssl pkcs8 -topk8 -inform PEM -in private.pem -outform pem -nocrypt -out private_pkcs.pem
# 生成公钥 public.pem
$ openssl rsa -in private.pem -pubout -out public.pem

# 创建证书 rsacert.csr
$ openssl req -new -key private.pem -out rsacert.csr
# 证书签名, rsacert.crt
$ openssl x509 -req -days 3650 -in rsacert.csr -signkey private.pem -out rsacert.crt
```
---

https://www.cnblogs.com/ifme/p/12182947.html

这里生成了长度为 2048的私钥，长度可选 1024 / 2048 / 3072 / 4096 ...。一般来说长度越长，保密度越高
```shell
# 生成私钥
openssl genrsa -out private.pem 2048
# 生成私钥(把RSA私钥转换成PKCS8格式)
openssl pkcs8 -topk8 -inform PEM -in private.pem -outform pem -nocrypt -out private_pkcs.pem
# 生成公钥
openssl rsa -in private.pem -pubout -out public.pem
```
