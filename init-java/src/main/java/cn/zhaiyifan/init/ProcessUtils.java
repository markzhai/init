package cn.zhaiyifan.init;

import java.lang.management.ManagementFactory;

class ProcessUtils {
    public static String myProcessName() {
        return ManagementFactory.getRuntimeMXBean().getName();
    }
}