# ip_parse(hive)
## 使用教程
### 函数名
> getlocation_v2('ip') 

### 含义
> 默认输出中文ip地理信息，如果需要英文格式可以使用getlocation_v2('ip','en');

### 函数优点

> - 该函数支持不仅支持ipv4 还支持ipv6格式
> - 海外ip地址解析更精确

### hive客户端使用样例

> 普通格式

```hql
hive (default)> select getlocation_v2('61.174.15.215');
OK
亚洲|CN|1814991|中国|浙江省|null|null|120.1614|30.2936|null
Time taken: 0.115 seconds, Fetched: 1 row(s)
```

> 英文字符

```hql
hive (default)> select getlocation_v2('61.174.15.215','en');
OK
Asia|CN|1814991|China|Zhejiang|null|null|120.1614|30.2936|null
```

> ip v6 格式

```hql
hive (default)> select getlocation_v2('2404:6800:8005::68','en')  ;
OK
Oceania|AU|2077456|Australia|null|null|null|133.0|-27.0|null
Time taken: 0.128 seconds, Fetched: 1 row(s)

```

> 结果解析

```hql                       
 
--------------------------------------------------------------
 大洋洲|AU|2077456|澳大利亚|null|null|null|133.0|-27.0|null 
 --------------------------------------------------------------
 洲|简称|国家编号|国家名称|省|市|区县|经度|纬度|运营商

```

### 开发过程

- 需求：支持ipv4 v6解析
- 库格式：文件格式为geoip.mmdb

#### 创建自定义函数的步骤

 1.创建java类 extends org.apache.hadoop.hive.sql.exec.UDF

 2.需要实现evalute函数，evalute函数支持重载

 3.把程序打包放在机器上

 4.进入hive客户端，上传jar包到hdfs

 5.创建duf函数

注意事项：

java程序打成jar包后，经常碰到一些资源文件找不到等问题

可以如下方式解决
```hql

InputStream is= IPParse.class.getResourceAsStream("/"+Constants.geoIPFile);
            reader = new DatabaseReader.Builder(is).build();    
            
```

#### 创建sql
```hql
hive (default)> add jar /home/datacenter/ip_parse-1.0-jar-with-dependencies.jar;
hive (default)> create temporary function get_location_ip as 'com.yoozoo.dc.IPParse';
```

