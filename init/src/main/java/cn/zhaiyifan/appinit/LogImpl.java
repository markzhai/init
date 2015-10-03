package cn.zhaiyifan.appinit;

import android.util.Log;

public class LogImpl implements ILog {
    private static ILog mLogProxy = null;
    private static LogImpl mLogger = null;

    public static void i(String tag, String message) {
        if (mLogger == null) {
            mLogger = new LogImpl();
        }
        mLogger.info(tag, message);
    }

    public static void w(String tag, String message) {
        if (mLogger == null) {
            mLogger = new LogImpl();
        }
        mLogger.warn(tag, message);
    }

    public static void setLogProxy(ILog proxy) {
        mLogProxy = proxy;
    }

    @Override
    public void info(String tag, String message) {
        if (mLogProxy != null) {
            mLogProxy.info(tag, message);
        } else {
            Log.i(tag, message);
        }
    }

    @Override
    public void warn(String tag, String message) {
        if (mLogProxy != null) {
            mLogProxy.warn(tag, message);
        } else {
            Log.w(tag, message);
        }
    }
}