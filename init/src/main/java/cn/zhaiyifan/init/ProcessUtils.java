package cn.zhaiyifan.init;

import android.app.ActivityManager;
import android.content.Context;

import java.util.List;

class ProcessUtils {
    private static volatile String sProcessName;
    private final static Object sNameLock = new Object();

    private static volatile Boolean sMainProcess;
    private final static Object sMainLock = new Object();

    public static String myProcessName() {
        if (sProcessName != null) {
            return sProcessName;
        }
        synchronized (sNameLock) {
            if (sProcessName != null) {
                return sProcessName;
            }
            return sProcessName = obtainProcessName(Init.getContext());
        }
    }

    public static boolean isMainProcess() {
        if (sMainProcess != null) {
            return sMainProcess;
        }
        synchronized (sMainLock) {
            if (sMainProcess != null) {
                return sMainProcess;
            }
            final String processName = myProcessName();
            if (processName == null) {
                return false;
            }
            Context context = Init.getContext();
            sMainProcess = processName.equals(context.getApplicationInfo().processName);
            return sMainProcess;
        }
    }

    private static String obtainProcessName(Context context) {
        final int pid = android.os.Process.myPid();
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> listTaskInfo = am.getRunningAppProcesses();
        if (listTaskInfo != null && listTaskInfo.size() > 0) {
            for (ActivityManager.RunningAppProcessInfo processInfo : listTaskInfo) {
                if (processInfo != null && processInfo.pid == pid) {
                    return processInfo.processName;
                }
            }
        }
        return null;
    }
}