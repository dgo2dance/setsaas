# 多租户，动态数据源

1. 功能简述<p>
   1) 租户获取，支持字段、schema、datasource模式
   2) 支持动态数据源和单数据源
   3) 支持自定义SQL元数据处理，如create_time update_time的设值

2. 设计简述<p>
   1) 通过请求头获取租户信息[tenant]，设置到线程上下文，同时通过``ConnectionService``接口获取对应租户的数据源
   2) 使用``mybatis-plus(3.4.0)``支持多租户的SQL解析以及表部分字段处理
   3) 使用``AbstractRoutingDataSource``支持动态数据源
   
3. 使用说明<p>
   1) pom文件引入
   ```xml
         <dependency>
           <groupId>com.xiaogj.x3</groupId>
           <artifactId>x3-tenant-datasource</artifactId>
           <version>${project.version}</version>
         </dependency>
   ```
   2) 租户配置使用
   ```yaml
    xiaogj:
      x3:
        # 是否启用动态数据源
        dynamic:
          enabled: true
        tenant:
          # 多租户开启 springmvc模式，需要从header中获取到租户信息，gateway的weblux模式可以关闭
          webmvc: true
          # 是否启用多租户模式
          enabled: true
          # 多租户类型，支持 none,column,schame
          type: schame
   ```
   3) 数据源配置(暂支持Druid)
   ```yaml
     spring:
       datasource:
         driver-class-name: com.mysql.cj.jdbc.Driver
         url: xxx
         username: xxx
         password: xxx
         druid:
           initial-size: 10
           min-idle: 10
           #最大连接数
           max-active: 200
           max-wait: 20000
           test-on-borrow: false
           test-on-return: false
           test-while-idle: true
           validation-query: SELECT 1
           validation-query-timeout: 30000
           pool-prepared-statements: true
           max-pool-prepared-statement-per-connection-size: 10
           max-open-prepared-statements: 10
           filters: stat,wall,slf4j
           connection-properties: druid.stat.mergeSql=true;druid.stat.slowSqlMillis=5000
           timeBetweenEvictionRunsMillis: 60000
           minEvictableIdleTimeMillis: 30000
   ```
   3) 实现多数据源获取接口``ConnectionService``
4. 使用案例参考：demo/datasource-demo