package cn.zhaiyifan.init;

/**
 * <h1>Log interface for application to implement so that the library can use application's custom
 * Log, like file or report</h1>
 * Created by mark.zhai on 15/10/3.
 */
public interface ILog {

    /**
     * Send an information level log message, used by time-related log.
     *
     * @param tag Used to identify the source of a log message.  It usually identifies
     *            the class or activity where the log call occurs.
     * @param msg The message you would like logged.
     */
    void info(String tag, String msg);


    /**
     * Send an warning level log message, used by exception-related log.
     *
     * @param tag Used to identify the source of a log message.  It usually identifies
     *            the class or activity where the log call occurs.
     * @param msg The message you would like logged.
     */
    void warn(String tag, String msg);
}