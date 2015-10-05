package cn.zhaiyifan.init;

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
    public void info(String tag, String msg) {
        if (mLogProxy != null) {
            mLogProxy.info(tag, msg);
        } else {
            System.out.println(tag + ": " + msg);
        }
    }

    @Override
    public void warn(String tag, String msg) {
        if (mLogProxy != null) {
            mLogProxy.warn(tag, msg);
        } else {
            System.out.println(tag + ": " + msg);
        }
    }
}