# 介绍
提供 API 接口供开发者调用的平台，基于 Spring Boot 后端 + React 前端的全栈微服务项目。管理员可以接入并发布接口、统计分析各接口调用情况；用户可以注册登绿并开通接口调用权限、浏览接口、在线调试，还能使用客户端 SDK 轻松在代码中调用接口。

# SpringBoot 项目初始模板

基于 SpringBoot 项目初始模板：https://github.com/weedsx/springboot-init

# 技术栈

- Spring Boot
- MySQL
- MyBatis Plus
- MyBatisX
- Redis Redisson
- Spring Scheduler
- Swagger
- Knife4j
- Gson

# 技术难点

## API 签名认证

如果有调用第三方接口服务的开发经历就知道，第三方服务往往会事先给出一个`key`和`secret`，然后开发者再根据第三方服务的 SDK 加上`key`和`secret`进行服务的调用，其实这个`key`和`secret`就是 API 签名认证。

API签名认证的目的是防止未经授权的访问和数据篡改。当第三方应用程序向主机应用程序发送请求时，它必须使用一种加密算法将请求参数和其他必要信息组合在一起，并生成一个唯一的签名。主机应用程序收到请求后，会对签名进行验证，以确保请求没有被篡改，并且请求来自经过授权的第三方应用程序。

通常，API签名认证涉及以下步骤：

1. 获得访问凭证：第三方应用程序需要从主机应用程序获取访问凭证，通常是一个API密钥或令牌。这些凭证用于标识第三方应用程序的身份。
2. 创建签名字符串：第三方应用程序使用请求参数和访问凭证等信息创建一个单向加密的签名字符串。
3. 加密签名字符串：使用事先约定好的加密算法（例如HMAC-SHA1、HMAC-SHA256等），将签名字符串与私有密钥一起加密，生成最终的签名值。
4. 发送请求：第三方应用程序将请求发送到主机应用程序，并在请求中包含生成的签名。
5. 验证签名：主机应用程序收到请求后，会提取请求中的签名和其他相关信息，并使用相同的加密算法和事先约定的私有密钥，重新生成签名。然后，主机应用程序将新生成的签名与请求中的签名进行比较，以验证请求的完整性和真实性。



**本项目的具体步骤如下：**

1. 客户端（也就是本项目的 SDK）拿到自己调用 API 的 AccessKey 和 SecretKey，将客户端调用 API 要发送的数据 body + SecretKey 单向加密生成摘要，也就是签名 sign
2. 将 AccessKey、sign、body 等数据或表示加入要调用 API 的请求头中（**一定不能带SecretKey**）
3. API 服务端这里首先校验该用户的 AccessKey，再查出 SecretKey 并使用 SecretKey + body 生成签名 sign，与请求头的 sign 进行比对。



当然，以上只是最简单的版本，还可以在使用非对称加密对 sign 进行加密，如下：

<details class="lake-collapse"><summary id="u9221762b"><span class="ne-text">加密版本</span></summary><ol class="ne-ol" style="margin: 0; padding-left: 23px"><li id="u9bc0e9cf" data-lake-index-type="0"><span class="ne-text">发送方生成密钥对：公钥A和私钥A。</span></li><li id="u497ab54a" data-lake-index-type="0"><span class="ne-text">发送方使用私钥A对要发送的数据进行签名：</span></li></ol><ul class="ne-list-wrap" style="margin: 0; padding-left: 23px; list-style: none"><ul ne-level="1" class="ne-ul" style="margin: 0; padding-left: 23px; list-style: circle"><li id="ud28e8f08" data-lake-index-type="0"><span class="ne-text">首先，发送方对数据进行哈希，得到数据的哈希值H(data)。</span></li><li id="uec8cf6f4" data-lake-index-type="0"><span class="ne-text">然后，使用私钥A对哈希值H(data)进行加密，得到数字签名S。</span></li><li id="u53f19ffa" data-lake-index-type="0"><span class="ne-text">发送方将数据和数字签名一起发送给接收方。</span></li></ul></ul><ol start="3" class="ne-ol" style="margin: 0; padding-left: 23px"><li id="u0f113aeb" data-lake-index-type="0"><span class="ne-text">接收方接收到数据和数字签名。</span></li><li id="ub0cfca21" data-lake-index-type="0"><span class="ne-text">接收方使用公钥A对数字签名进行解密：</span></li></ol><ul class="ne-list-wrap" style="margin: 0; padding-left: 23px; list-style: none"><ul ne-level="1" class="ne-ul" style="margin: 0; padding-left: 23px; list-style: circle"><li id="u53380a33" data-lake-index-type="0"><span class="ne-text">接收方使用公钥A对数字签名S进行解密，得到解密后的哈希值H'(data)。</span></li></ul></ul><ol start="5" class="ne-ol" style="margin: 0; padding-left: 23px"><li id="u666f41a0" data-lake-index-type="0"><span class="ne-text">接收方对接收到的数据计算哈希值：</span></li></ol><ul class="ne-list-wrap" style="margin: 0; padding-left: 23px; list-style: none"><ul ne-level="1" class="ne-ul" style="margin: 0; padding-left: 23px; list-style: circle"><li id="u0e531031" data-lake-index-type="0"><span class="ne-text">接收方对接收到的数据进行哈希，得到数据的哈希值H''(data)。</span></li></ul></ul><ol start="6" class="ne-ol" style="margin: 0; padding-left: 23px"><li id="u52dc8477" data-lake-index-type="0"><span class="ne-text">接收方验证签名：</span></li></ol><ul class="ne-list-wrap" style="margin: 0; padding-left: 23px; list-style: none"><ul ne-level="1" class="ne-ul" style="margin: 0; padding-left: 23px; list-style: circle"><li id="uc39d642b" data-lake-index-type="0"><span class="ne-text">接收方将解密后的哈希值H'(data)与自己计算的哈希值H''(data)进行比较。</span></li><li id="u0855a3a2" data-lake-index-type="0"><span class="ne-text">如果两个哈希值相等，说明数据未被篡改，并且确实来自发送方，验证通过。</span></li></ul></ul></details>

<details class="lake-collapse"><summary id="uac459b6d"><strong><span class="ne-text" style="color: rgba(55,199,207,1)">加密算法分类</span></strong></summary><div data-type="color1" class="ne-alert" style="border: 1px solid #B5E8F2; background-color: #CEF1F7; margin: 4px 0; padding: 10px; border-radius: 4px"><ol class="ne-ol" style="margin: 0; padding-left: 23px"><li id="u0a235576" data-lake-index-type="0"><strong><span class="ne-text">单向加密算法（摘要算法）</span></strong><span class="ne-text">：也称为单向散列函数或哈希函数，是一类特殊的加密算法，其主要特点是只能进行单向的转换，即可以将输入数据（如文本、文件等）转换为固定长度的输出（通常称为哈希值或摘要），但无法从哈希值恢复出原始的输入数据。如：MD5（Message Digest Algorithm 5）、SHA-1（Secure Hash Algorithm 1）、SHA-256、SHA-384、SHA-512、SHA-3</span></li><li id="u0ba5dab9" data-lake-index-type="0"><strong><span class="ne-text">秘钥加密算法</span></strong><span class="ne-text">：</span></li></ol><ul class="ne-list-wrap" style="margin: 0; padding-left: 23px; list-style: none"><ul ne-level="1" class="ne-ul" style="margin: 0; padding-left: 23px; list-style: circle"><li id="u847228f5" data-lake-index-type="0"><strong><span class="ne-text">对称加密算法</span></strong><span class="ne-text">：使用相同的密钥进行加密和解密，也称为共享密钥加密。常见的对称加密算法有AES（Advanced Encryption Standard）、DES（Data Encryption Standard）和3DES等。</span></li><li id="ua2c2e4f2" data-lake-index-type="0"><strong><span class="ne-text">非对称加密算法</span></strong><span class="ne-text">：使用一对相关联的密钥，一个密钥用于加密数据（通常称公钥），另一个密钥用于解密数据（通常称私钥）。常见的非对称加密算法有RSA（Rivest-Shamir-Adleman）、ECC（Elliptic Curve Cryptography）等。</span></li></ul></ul></div></details>

------

**开发 SDK：**

1. SDK 的 pom.xml 最好将一般项目中自动生成的插件删掉
2. SDK 不需要启动类，取而代之的是要有返回供开发者使用的客户端的配置类：

```java
@Configuration
@ConfigurationProperties("open-api.client")
@ComponentScan
@Data
public class OpenApiClientConfig {
    private String accessKey;

    private String secretKey;

    @Bean
    public OpenApiClient openApiClient() {
        return new OpenApiClient(this.accessKey, this.secretKey);
    }
}
```

开发 SDK 最后最重要的一步就是添加`META-INF/spring.factories`，内容如下：

<img src="README.assets\1689763721986-0d564f91-6ff5-43a7-8ceb-cab4cbbfd102.png" alt="img"  />

```properties
# springboot starter
# org.springframework.boot.autoconfigure.EnableAutoConfiguration=返回bean实例的配置类路径
org.springframework.boot.autoconfigure.EnableAutoConfiguration=com.weeds.client.OpenApiClientConfig
```

然后 maven install，本地仓库即可出现该 SDK

<details class="lake-collapse"><summary id="u627ff63e"><strong><span class="ne-text" style="color: rgba(55,199,207,1)">spring.factories 文件被 SpringBoot 自动识别的原理</span></strong></summary><div data-type="color1" class="ne-alert" style="border: 1px solid #B5E8F2; background-color: #CEF1F7; margin: 4px 0; padding: 10px; border-radius: 4px"><p id="u9ae77616" class="ne-p" style="margin: 0; padding: 0; min-height: 24px"><span class="ne-text">spring.factories 文件的原理涉及到 Spring Boot 的自动配置机制和 Spring Framework 的类路径扫描。</span></p><ol class="ne-ol" style="margin: 0; padding-left: 23px"><li id="uda6e8a70" data-lake-index-type="0"><strong><span class="ne-text">自动配置机制</span></strong><span class="ne-text">：Spring Boot 的自动配置是基于条件化配置（Conditional Configuration）的原理。条件化配置允许根据环境或类路径上存在的类来决定是否自动配置某些功能或组件。自动配置类会根据一系列条件来判断是否启用或禁用特定功能，从而保证在不同场景下有不同的行为。</span></li><li id="u97f2b885" data-lake-index-type="0"><strong><span class="ne-text">类路径扫描</span></strong><span class="ne-text">：Spring Boot 在应用程序启动时会扫描类路径上的所有 JAR 文件，包括项目依赖的 JAR 包。对于每个 JAR 包，它会查找 </span><strong><span class="ne-text">META-INF/spring.factories</span></strong><span class="ne-text"> 文件。如果该文件存在，Spring Boot 就会读取其中的配置。</span></li></ol><p id="u354c6e46" class="ne-p" style="margin: 0; padding: 0; min-height: 24px"><span class="ne-text">当 Spring Boot 启动时，它会按照以下步骤处理 </span><span class="ne-text">spring.factories</span><span class="ne-text"> 文件：</span></p><ul class="ne-ul" style="margin: 0; padding-left: 23px"><li id="u3927854e" data-lake-index-type="0"><span class="ne-text">Spring Boot 类路径扫描：Spring Boot 在启动时会通过类路径扫描找到所有的 </span><span class="ne-text">spring.factories</span><span class="ne-text"> 文件。</span></li><li id="uc59f8ac7" data-lake-index-type="0"><span class="ne-text">加载 </span><span class="ne-text">spring.factories</span><span class="ne-text"> 文件：对于每个找到的 </span><span class="ne-text">spring.factories</span><span class="ne-text"> 文件，Spring Boot 会读取其中的配置信息。</span></li><li id="u44ba5d4d" data-lake-index-type="0"><span class="ne-text">解析自动配置类：Spring Boot 根据 </span><span class="ne-text">spring.factories</span><span class="ne-text"> 文件中指定的键值对（</span><span class="ne-text">org.springframework.boot.autoconfigure.EnableAutoConfiguration</span><span class="ne-text">）找到对应的自动配置类的全限定名。</span></li><li id="uc6c5624e" data-lake-index-type="0"><span class="ne-text">条件化装配：Spring Boot 将找到的自动配置类进行条件化处理，检查是否满足特定条件（例如是否存在某个类、是否存在某个 Bean 等）。如果满足条件，该自动配置类将被启用。</span></li><li id="u27621f5d" data-lake-index-type="0"><span class="ne-text">自动配置生效：满足条件的自动配置类中的配置注解（如 </span><span class="ne-text">@Configuration</span><span class="ne-text">、</span><span class="ne-text">@Component</span><span class="ne-text"> 等）会被 Spring 加载，从而创建相应的 Spring Bean，并将它们纳入 Spring 容器的管理。</span></li><li id="u794a31b3" data-lake-index-type="0"><span class="ne-text">完成自动配置：在所有的 </span><span class="ne-text">spring.factories</span><span class="ne-text"> 文件都处理完毕后，Spring Boot 将所有符合条件的自动配置组合起来，形成最终的应用程序上下文，完成自动配置过程。</span></li></ul><p id="u4a67f5aa" class="ne-p" style="margin: 0; padding: 0; min-height: 24px"><span class="ne-text">通过这种机制，Spring Boot 能够自动识别和装配大量常用的组件和功能，极大地简化了开发者的工作。同时，开发者也可以根据需要自定义自己的自动配置，通过编写 </span><span class="ne-text">spring.factories</span><span class="ne-text"> 文件来实现自己的自动配置类。</span></p></div></details>

## Gateway 网关

由于前面的 API 签名认证，所以我们一定要在每个真实提供接口的方法进行用户验证，当然可以使用 AOP 解决，但随着接口数量的增加，在每个接口上都加 AOP 也麻烦，有没有一劳永逸的解决方案呢，当然也可以使用 spring 的拦截器、servlet 的过滤器，只不过如果项目再变大膨胀模块变多，就需要**网关**

这里先用网关的全局过滤器解决所有调用 API 的认证校验和日志。

需要注意的是，Gateway 基于 Reactor 响应式编程库，所以编码时是采用函数式异步编程形式。

不同于以往同步的编码习惯，**⭐⭐过滤器的执行流程为：**

1. 请求来到网关，通过过滤器链，每个过滤器的先后顺序取决于 Order 接口，**具有最高优先级的过滤器是“前”阶段的第一个和“后”阶段的最后一个**，每个实现 GlobalFilter 接口并实现 filter 方法的类就是一个过滤器
2. 请求通过过滤器，进入 filter 方法，整个 filter 方法直到 return 前都是过滤器对该请求还未被调用前的处理（也就是请求前置处理），最后 return 一个支持异步操作的 Mono 对象（默认为`return chain.filter(exchange);`）
3. 每个 filter 方法像这样 return 完后该请求会被真正执行，需要注意的是，以往的 spring 的拦截器或是 servlet 的过滤器都是同步的，也就是会等待这个请求被处理结束才会回过头执行响应前置的处理
4. 而 Gateway 的过滤器异步就体现出来了，在请求执行的时候，此时的过滤器其实是畅通的，不是线程阻塞的，那请求结束的响应前置处理在哪呢？
5. 响应前置处理可以以两种方式实现

1. 1. **形式一：分别编写请求过滤器和响应过滤器**，单独编写一个响应过滤器，只不过该过滤器是单独处理响应的，所以自然而然可以将顺序往后靠一靠，那这个响应前置处理的逻辑写在哪呢？肯定不是 filter 方法里面吧！对也不对，应该写在 return 的 Mono 对象的异步方法`.then()`里，这里其实跟 JS 的 Promise 异步语法一样
      https://blog.csdn.net/small_to_large/article/details/125326498

1. 2. **形式二：编写一个过滤器加上响应装饰器**，所谓装饰器模式就是不改变原有类的基础上增加原有类的额外行为，这里 Gateway 为我们提供了响应的装饰器 `ServerHttpResponseDecorator`
      https://blog.csdn.net/qq_19636353/article/details/126759522

<img src="README.assets\1690958789034-50c441c2-0da7-4cb2-859c-4cc599b307bf.png" alt="img"  />

## Dubbo + Nacos 进行 RPC 远程调用

引入：[https://cn.dubbo.apache.org/zh-cn/overview/mannual/java-sdk/reference-manual/registry/nacos/#21-%E5%A2%9E%E5%8A%A0%E4%BE%9D%E8%B5%96](https://cn.dubbo.apache.org/zh-cn/overview/mannual/java-sdk/reference-manual/registry/nacos/#21-增加依赖)

```xml
<dependency>
  <groupId>org.apache.dubbo</groupId>
  <artifactId>dubbo</artifactId>
  <version>3.0.9</version>
</dependency>
<dependency>
  <groupId>com.alibaba.nacos</groupId>
  <artifactId>nacos-client</artifactId>
  <version>2.1.0</version>
</dependency>
```

添加`@EnableDubbo`注解，提供服务的 Service 实现类上添加`@DubboService`

------

**🐞Dubbo启动时qos-server can not bind localhost:22222错误解决：**

Qos=Quality of Service，qos是Dubbo的在线运维命令，可以对服务进行动态的配置、控制及查询，有时候该端口可能确实被其他软件占用，更多情况是被使用 Nacos 注册的其他服务占用了，改一下（或者直接禁用）就行：

```yaml
dubbo:
  application:
    name: open-api-core
    qosEnable: true
    qosPort: 22223
  protocol:
    name: dubbo
    port: -1
  registry:
    id: open-api-core
    address: nacos://localhost:8848
```

------

🐞**No provider available for the service com.weeds.gateway.provider.DemoService from the url consumer 找不到服务提供者报错！**

如果服务的接口没有抽成一个单独的模块，那**包名一定要相同！！！**

------

🐞**要先启动服务提供模块，再启动服务消费模块**

------

## 网关的路由规则

目前是根据 SDK 事先在请求头中记录的具体 URL，网关根据请求头里 URL 的 IP (域名) + port 进行断言路由，后续也可能改成编程式断言路由

```yaml
spring:
  main:
    web-application-type: reactive
  cloud:
    gateway:
      routes:
        # 通过 SDK 添加的请求头信息进行匹配
        - id: interface-demo-service
          uri: http://localhost:8280
          predicates:
            - Header=api_url, .*localhost:8280.*
```

## 网关过滤器中的 RPC 调用

<img src="README.assets\1691660843965-229895e0-4e8f-4a30-82df-1ef970a49c5f.png" alt="img"  />
