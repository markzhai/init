package cn.zhaiyifan.appinit;

/**
 * <h1>Log interface for application to implement so that the library can use application's custom
 * Log, like file or report</h1>
 * Created by mark.zhai on 15/10/3.
 */
public interface ILog {
    void info(String tag, String message);
    void warn(String tag, String message);
}