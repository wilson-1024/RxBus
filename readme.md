## RxBus

### RxBus是基于RxJava做的事件发布-订阅总线
支持手动代码注册与使用注解在编译时期生成固定的代码注册
### 常规用法
先声明一个需要监听事件的Listener
```java
   public RxBus.OnEventListener textEventListener = new RxBus.OnEventListener() {
        @Override
        public void onEvent(Object o) {
            ExpEvent expEvent = (ExpEvent) o;
            RevFragment.this.postTV.setText(postTV.getText()+","+expEvent.value);
        }
    };
```
然后在初始化页面时注册
```java
 RxBus.getDefault().register(textEventListener, ExpEvent.class);
```
接着在页面销毁时解注册
```java
 RxBus.getDefault().unregister(textEventListener);
```
### 使用注解用法
声明一个监听的Event方法，参数为接收的参数
```java
    @InjectMethodBind
    public void textEvent(ExpE e) {
        postMethodTV.setText(postMethodTV.getText() + "," + (e.value));
    }
```
在页面初始话时注册
```java
RxBus.getDefault().inject(this);
```
页面销毁时解注册
```java
RxBus.getDefault().unInject(this);
```
编译时期动态生成代码模板
```java
public class RevFragment_BindInject implements Inject<RevFragment> {
    public RxBus.OnEventListener textEvent_bind;

    @Override
    public void inject(final RevFragment host) {
        textEvent_bind = new RxBus.OnEventListener() {
            @Override
            public void onEvent(final Object object) {
                ExpE o = (ExpE)object;
                host.textEvent(o);
            }
        };
        RxBus.getDefault().register(textEvent_bind,0,ExpE.class);
    }

    @Override
    public void unInject(final RevFragment host) {
        RxBus.getDefault().unregister(textEvent_bind);
    }
}
```
### 模块含义
#### rxbus-inject
API android模块
#### rxbus-annotation 
注解相关 java模块
#### rxbus-compile
注解处理器 java模块


