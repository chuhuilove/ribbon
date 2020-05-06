Ribbon
======

Ribbon是客户端IPC库,该库在云中经过了实战测试。 它提供了下面的特性

* 负载均衡
* 容错能力
* 异步和响应模型中的多种协议(HTTP,TCP,UDP)支持
* 缓存和批处理

获得ribbon二进制,去[maven中心仓库](http://search.maven.org/#search%7Cga%7C1%7Cribbon). Here is an example to add dependency in Maven:

```xml
<dependency>
    <groupId>com.netflix.ribbon</groupId>
    <artifactId>ribbon</artifactId>
    <version>2.2.2</version>
</dependency>
```

## Modules

* ribbon: 在其他功能区模块和[Hystrix](https://github.com/netflix/hystrix)之上集成了负载平衡,容错,缓存/分批处理的API
* ribbon-loadbalancer: 可以独立使用或与其他模块一起使用的负载平衡器API
* ribbon-eureka: 使用[Eureka client](https://github.com/netflix/eureka)的API为云提供动态服务器列表
* ribbon-transport: 使用具有负载平衡功能的[RxNetty](https://github.com/netflix/rxnetty),支持HTTP,TCP和UDP协议的传输客户端
* ribbon-httpclient: REST客户端构建在Apache HttpClient之上，与负载平衡器集成(已弃用,并被ribbon模块取代)
* ribbon-example: 示例
* ribbon-core: 客户端配置API和其他共享API

## 项目状态: 维护中
Ribbon由多个组件组成,其中一些组件用于内部生产,另一些组件随着时间的推移被非oss解决方案所取代.
这是因为Netflix开始转向面向RPC的更组件化的体系结构,并将重点放在单一职责模块上.因此,此时每个Ribbon组件都得到了不同程度的关注.

更具体地说,以下是Ribbon的组成部分以及我们团队对它们的关注程度:
* ribbon-core: **在生产中大规模部署**
* ribbon-eureka: **在生产中大规模部署**
* ribbon-evcache: **未使用**
* ribbon-guice: **未使用**
* ribbon-httpclient: **我们不使用com.netflix.http4.ssl.
                        而是使用云安全团队开发的内部解决方案**
* ribbon-loadbalancer: **在生产中大规模部署**
* ribbon-test: **这只是一个内部集成测试套件**
* ribbon-transport: **未使用**
* ribbon: **未使用**


即使对于已在生产环境中部署的组件,我们也将它们包装在Netflix内部的http客户端中,并且由于它们已经稳定了一段时间,因此我们并未添加新功能.
任何新功能都已经添加到Ribbon上的内部包装器中(例如请求跟踪和度量).我们尚未做出任何努力使Ribbon下的这些组件与Netflix无关.

认识到这些现实和缺陷,我们将Ribbon置于维护模式.
这意味着如果外部用户提交了一个大的特性请求,我们在内部不会对其进行优先级排序.
但是，如果有人要自己完成工作并提交完整的拉请求,我们很乐意进行审查并接受.
我们的团队已经开始在gRPC上构建RPC解决方案.
我们进行这种转换主要有两个原因:多语言支持和通过请求拦截器实现更好的可扩展性/可组合性.
那是我们当前的计划.

我们目前定期向gRPC代码库贡献代码.
为了帮助我们的团队在生产中迁移到基于grpc的解决方案(并进行实战测试),
我们还添加了负载平衡和发现拦截器,以实现与Ribbon和Eureka提供的功能相同的功能.
拦截器目前是netflix内部的.当我们达到那种信心时,我们希望开源这种新方法.
我们预计这种情况不会在2016年第三季度之前发生.

## Release notes

See https://github.com/Netflix/ribbon/releases

## 代码示例

### 使用模板访问HTTP资源 ([full example](https://github.com/Netflix/ribbon/blob/master/ribbon-examples/src/main/java/com/netflix/ribbon/examples/rx/template/RxMovieTemplateExample.java))

```java
HttpResourceGroup httpResourceGroup = Ribbon.createHttpResourceGroup("movieServiceClient",
            ClientOptions.create()
                    .withMaxAutoRetriesNextServer(3)
                    .withConfigurationBasedServerList("localhost:8080,localhost:8088"));
HttpRequestTemplate<ByteBuf> recommendationsByUserIdTemplate = httpResourceGroup.newTemplateBuilder("recommendationsByUserId", ByteBuf.class)
            .withMethod("GET")
            .withUriTemplate("/users/{userId}/recommendations")
            .withFallbackProvider(new RecommendationServiceFallbackHandler())
            .withResponseValidator(new RecommendationServiceResponseValidator())
            .build();
Observable<ByteBuf> result = recommendationsByUserIdTemplate.requestBuilder()
                        .withRequestProperty("userId", "user1")
                        .build()
                        .observe();
```

### 使用注解访问HTTP资源 ([full example](https://github.com/Netflix/ribbon/blob/master/ribbon-examples/src/main/java/com/netflix/ribbon/examples/rx/proxy/RxMovieProxyExample.java))

```java
public interface MovieService {
    @Http(
            method = HttpMethod.GET,
            uri = "/users/{userId}/recommendations",
            )
    RibbonRequest<ByteBuf> recommendationsByUserId(@Var("userId") String userId);
}

MovieService movieService = Ribbon.from(MovieService.class);
Observable<ByteBuf> result = movieService.recommendationsByUserId("user1").observe();
```

### 创建一个启用了[Eureka](https://github.com/Netflix/eureka)动态服务器列表和区域关联的AWS-ready负载均衡器

```java
        IRule rule = new AvailabilityFilteringRule();
        ServerList<DiscoveryEnabledServer> list = new DiscoveryEnabledNIWSServerList("MyVIP:7001");
        ServerListFilter<DiscoveryEnabledServer> filter = new ZoneAffinityServerListFilter<DiscoveryEnabledServer>();
        ZoneAwareLoadBalancer<DiscoveryEnabledServer> lb = LoadBalancerBuilder.<DiscoveryEnabledServer>newBuilder()
                .withDynamicServerList(list)
                .withRule(rule)
                .withServerListFilter(filter)
                .buildDynamicServerListLoadBalancer();   
        DiscoveryEnabledServer server = lb.chooseServer();         
```

### 使用LoadBalancerCommand来负载HttpURLConnection发出的IPC调用 ([full example](https://github.com/Netflix/ribbon/blob/master/ribbon-examples/src/main/java/com/netflix/ribbon/examples/loadbalancer/URLConnectionLoadBalancer.java))

```java
CommandBuilder.<String>newBuilder()
        .withLoadBalancer(LoadBalancerBuilder.newBuilder().buildFixedServerListLoadBalancer(serverList))
        .build(new LoadBalancerExecutable<String>() {
            @Override
            public String run(Server server) throws Exception {
                URL url = new URL("http://" + server.getHost() + ":" + server.getPort() + path);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                return conn.getResponseMessage();
            }
        }).execute();
```

 

 
