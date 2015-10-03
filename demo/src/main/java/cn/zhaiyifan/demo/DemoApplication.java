package cn.zhaiyifan.demo;

import android.app.Application;

import cn.zhaiyifan.appinit.Init;
import cn.zhaiyifan.appinit.Flow;
import cn.zhaiyifan.appinit.Task;

public class DemoApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        // The library needs application context to get process information.
        Init.init(this);

        Task task1 = new Task("task1") {

            @Override
            protected void start() {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public boolean runOnProcess(String processName) {
                return processName.equals("cn.zhaiyifan.init");
            }
        };

        Task task2 = new Task("task2") {

            @Override
            protected void start() {
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };

        Task task3 = new Task("task3") {

            @Override
            protected void start() {
                try {
                    Thread.sleep(300);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };

        Task task4 = new Task("task4", false) {

            @Override
            protected void start() {
                try {
                    Thread.sleep(700);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };

        Flow flow = new Flow("flow");
        flow.addTask(1, task1)
                .addTask(1, task2)
                .addTask(2, task3)
                .addTask(2, task4);

        Init.addFlow(flow);
        Init.start(flow);
    }
}