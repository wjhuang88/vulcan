# 对象转换工具
## 工具类位置
工具类定义在：

```java
io.vulcan.bean.Bean
```

**重要：根据JavaBean的一般约定，请确保需要做转换的类型定义中，需要传递的属性都有相对应的getter和setter方法。**

## map对象转到普通对象
示例如下：

```java
import io.vulcan.bean.Bean;
// ...

Bean beanManager = Bean.getInstance();

Map<String, Object> map = new HashMap<>();
// 填充数据
// ...
// ...
// 填充完毕

// 新建对象并存入数据，这种方式要求DemoClazz类有无参构造方法
DemoClazz newObject = beanManager.mapToBean(map, DemoClazz.class);

// 自己创建类，并填充数据
DemoClazz newObject2 = new DemoClazz();
beanManager.mapToBean(map, newObject2); // 此时newObject2会被填充map中的数据
```

## 不同对象间转换
示例如下：

```java
import io.vulcan.bean.Bean;
// ...

Bean beanManager = Bean.getInstance();

SourceClazz src = new SourceClazz();
// 填充数据
// ...
// ...
// 填充完毕

// 新建对象并存入数据，这种方式要求DistClazz类有无参构造方法
DistClazz dist = beanManager.beanToBean(src, DistClazz.class);

// 自己创建类，并填充数据
DistClazz dist2 = new DistClazz();
beanManager.beanToBean(src, dist2); // 此时dist2的属性会被填充src中相同属性的数据
```

## 普通对象到map的转换
示例如下：

```java
import io.vulcan.bean.Bean;
// ...

Bean beanManager = Bean.getInstance();

SourceClazz src = new SourceClazz();
// 填充数据
// ...
// ...
// 填充完毕

Map<String, Object> map = beanManager.beanToMap(src); // map中包含src对象中的属性名(key)和值(value)
```

## 列表批量转换
上述方法有相对应的列表转换方法，可以实现列表批量对象转换：

```java
import io.vulcan.bean.Bean;
// ...

Bean beanManager = Bean.getInstance();

List<Map<String, Object>> srcMapList = new ArrayList<>();
// 填充数据
// ...
// ...
// 填充完毕

List<MapDistClazz> mapDIst = beanManager.mapToBeanInList(srcMapList, MapDistClazz.class);

List<SourceClazz> srcList = new ArrayList<>();
// 填充数据
// ...
// ...
// 填充完毕

List<DistClazz> distList = beanManager.beanToBeanInList(srcList, DistClazz.class);

List<BeanClazz> beanList = new ArrayList<>();
// 填充数据
// ...
// ...
// 填充完毕

List<Map<String, Object>> mapList = beanManager.beanToMapInList(beanList)
```

## 自定义转换逻辑
类属性转换填充时，常见的可转换类型已经做了内置处理：
- 日期类型(java.util.Date, java.sql包中的日期类型, java8 time包类型等的互相转换)
- 字符串自动toString
- BigDecimal和BigInteger与普通Number类型的转换

如果想对特定的类做自定义的转换逻辑，下文提供两种实现方式。

### 实现转换接口
此方式有代码侵入性，但使用方式比较直观，自定义对象实现框架中的特定接口即可在调用工具类时自动调用接口实现的转换方法，接口说明如下：

```java
// 用于实现map对象到本对象的转换，配合Bean.mapToBean方法使用
io.vulcan.api.convertible.FromMap

// 用于实现其他对象(T类型)到本对象的转换，配合Bean.beanToBean方法中的目标对象使用
io.vulcan.api.convertible.From<T>

// 用于实现本对象到其他对象(T类型)的转换，配合Bean.beanToBean方法中的源对象使用
io.vulcan.api.convertible.Into<T>

// 注意：同时使用时，From的优先级高于Into

// 如果被转换的类中属性的类型实现了该接口，则会调用copy方法来复制一个新对象存入转换目标，否则使用默认的浅拷贝
io.vulcan.api.convertible.Copiable<T>
```

### 实现自定义转换器
可以通过实现自定义转换器并注册到工具类中，实现不侵入原始代码的情况下改变特定类转换的实现逻辑，框架中提供了两种转换器接口分别对应mapToBean和beanToBean的情况：

```java
// 实现T类型对象的mapToBean的转换逻辑
io.vulcan.api.helper.map2bean.MapConverter<T>
// 假设对DistClazz类的转换器实现类对象为mapConverter，注册转换器如下：
BeanUtils.register(DistClazz.class, mapConverter);

// 实现T类型对象的beanToMap的转换逻辑
io.vulcan.api.helper.map2bean.MapReverter<T>
// 假设对DistClazz类的转换器实现类对象为mapReverter，注册转换器如下：
U.bean.register(DistClazz.class, mapReverter);

// 实现S类型对象到D类型对象的转换逻辑
io.vulcan.api.helper.bean2bean.BeanConverter<S,D>
// 假设对SrcClazz到DistClazz的转换器实现类对象为beanConverter，注册转换器如下：
BeanUtils.register(SrcClazz.class, DistClazz.class, beanConverter);
```

## 性能优化
框架通过运行时动态生成转换器对象并缓存来实现高性能的对象转换，因此每个类型的对象在系统运行的第一次转换会有额外的性能消耗(即使如此在压测中的表现也比一般的beanutils更好)，而系统预热完成后的转换性能理论上可以达到和手写setter方法同样的性能，而如果你已经知道系统运行过程中会有多少类型会发生类型转换的操作，框架工具类也提供了预热方法，可以在系统启动过程中提前完成预热动作，压榨最后的性能空间：

```java
import io.vulcan.bean.Bean;
// ...

Bean beanManager = Bean.getInstance();

// SrcClazz类型对象到map对象的mapToBean和beanToMap方法预热
beanManager.speedup(SrcClazz.class);

// SrcClazz类型对象到DistClazz类型对象的beanToBean方法预热
beanManager.speedup(SrcClazz.class, DistClazz.class);
```

**注意：预热方法在系统启动时执行一次就可以了，不需要每次使用都执行一次**
