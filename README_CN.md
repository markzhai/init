# Init [![Maven Central](https://maven-badges.herokuapp.com/maven-central/cn.zhaiyifan/init/badge.svg?style=flat)](https://maven-badges.herokuapp.com/maven-central/cn.zhaiyifan/init)
Init帮助Android应用调度初始化流程，处理类型、优先级、多进程（比如每个进程都会执行application的onCreate），任务依赖，提高应用启动效率。

尽管Init设计的初衷是为了应用(application)初始化，但并不局限于此，它可以于应用在任何复杂的初始化流程。

Init不依赖于任何第三方库，使用Java concurrent并部分依赖于Android SDK(Context, Log)，所以理论上也可以在简单修改后直接用于Java工程。

# How

初始化流程被抽象为flow、wave和task。

![flow](art/flow.png "how it works")

flow是一个粗粒度概念，通常一个应用只有一个flow，但某些情况下我们可能拥有多个flow，像是patch flow，broadcast flow，fake UI flow等等，可以把它们都交给Init处理。

每个wave只有在上一wave的所有阻塞task完成后才能开始，而所有属于该wave的task会一起开始执行（除非被赋予了delay）。

至于task，在本库中属于原子性操作，他们可以被分为2大类型
 1. 阻塞task，即图中的蓝色任务。
 2. 异步task，又可以被分为
- 完全异步或者横跨若干个wave后才需要阻塞，像图中的绿色task。
- 异步链，像图中的红色task。

# 使用

```gradle
dependencies {
    compile 'cn.zhaiyifan:init:1.0.1'
}
```

```java
public class DemoApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        // Init需要应用context来获得进程相关信息
        Init.init(this);
        // 可以使用自定义的log离开输出Init的Log，logProxy需要实现cn.zhaiyifan.appinit.ILog接口
        // Init.init(this, logProxy)
        
        // 默认Task，延迟0，且阻塞下一波task的执行，参数字符串可以用来追踪任务执行状态
        Task task1 = new Task("task1") {

            @Override
            protected void start() {
                doSomeThing();
            }

            // 仅在返回true的时候才会在对应进程执行
            @Override
            public boolean runOnProcess(String processName) {
                return processName.equals("cn.zhaiyifan.demo");
            }
        };
        
        // 创建一个task，非阻塞，且延时300毫秒执行
        Task task2 = new Task("task2", false, 300) {

            @Override
            protected void start() {
                doSomeThing();
            }
        };

        // 类似地，创建更多task，如task3、task4等等
        
        // 创建一个有名flow
        Flow flow = new Flow("flow");
        // 往flow添加刚才创建的task, 第一个参数是wave序号，会从小到大执行每个wave的task
        flow.addTask(1, task1).addTask(1, task2).addTask(2, task3).addTask(2, task4);
        // 启动flow，开始初始化
        Init.start(flow);
    }
```

看一下log，可以发现原来一个串行执行需要2700毫秒的任务，在我们的安排下，现在只需要1307毫秒就可以结束。
```log
10-04 18:53:54.789 646-666/cn.zhaiyifan.init I/Task: task2 runs 500
10-04 18:53:55.289 646-665/cn.zhaiyifan.init I/Task: task1 runs 1000
10-04 18:53:55.591 646-741/cn.zhaiyifan.init I/Task: task3 runs 300
10-04 18:53:55.592 646-646/cn.zhaiyifan.init I/Flow: flow runs 1307
10-04 18:53:55.990 646-740/cn.zhaiyifan.init I/Task: task4 runs 700
10-04 18:53:56.191 646-783/cn.zhaiyifan.init I/Task: task5 runs 200
```

Useful api: 
```java
// 设置线程池大小
Init.setThreadPoolSize(...)

// 取消一个已经开始的flow
Init.cancel(...)

// 获得flow状态
Init.getFlowStatus(...)

// 获得特定的task状态
flow.getTaskStatus(taskName)

// 设置超时限制
flow.setTimeout(5000)

等等
```

更多详情请见demo工程。

# 为什么需要Init
想象一下我们是怎么去初始化一个大型应用像是支付宝、QQ、微信、空间等的，我们会面对像是下面这种代码：

```java
public class XXXApplication {

    // for multi-dex apps
    @Override
    protected void attachBaseContext(Context base) {
        // log init
        ...
        // eventbus init...
        ...
        // global variables init
        ...
        // process related
        String processName = ...
        boolean isMainProcess = ...
        ProcessInit.attachBaseContext(this, processName, isMainProcess);
    }

    @Override
    protected void onCreate() {
        // process related
        String processName = ...
        boolean isMainProcess = ...

        // CrashHandler, SafeMode, plugin, image manager, database, download, update, etc init

        ProcessInit.onCreate(this, processName, isMainProcess);
    }

}

public class ProcessInit {
    ...
    public static void onCreate(Application application, boolean isMainProcess, String processName) {
        if (isMainProcess) {

        }
    } else if (processName.contains(PUSH_PROCESS_SUFFIX)) {
        ...
    } else if (processName.contains(WEB_PROCESS_SUFFIX)) {
        ...
    } else if (processName.contains(MUSIC_PROCESS_SUFFIX)) {
        ...
    } else {
        ...
    }
    ...
}
```

你看到了当一个应用越来越大以后初始化能是一件多么复杂的事情，有些操作必须在另一个之后，而又有一些可以并行执行，又有的操作又需要在一个异步操作完成后才能执行......于是我们就得把每个独立的操作进行修改，有的改成异步，有的则阻塞在另一个操作后，使得代码杂乱且难以维护。

怎么可以使它变得简单呢？Init就是来帮助你做这个事的。

# 路线图
- 1.0 *10月 - 一个实现上述概念的可运行库* 已完成
- 1.1 **2015年内 - 支持更复杂的初始化flow** 进行中
- 2.0 或许明年 - 从使用本库的代码可以直接逆向出初始化flow的图

# Contribute
任何贡献都是受欢迎的，你可以创建一个issue或者直接发一个pull请求。