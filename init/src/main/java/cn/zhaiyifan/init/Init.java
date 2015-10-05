package cn.zhaiyifan.init;

import android.content.Context;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * <p>Entry to add, start and manage init flow.</p>
 * Created by mark.zhai on 2015/10/2.
 */
public class Init {
    private static final int DEFAULT_THREAD_POOL_SIZE = 8;

    private static Map<String, Flow> sFlowMap = new HashMap<>();
    private static Context sContext;
    private static int mThreadPoolSize = DEFAULT_THREAD_POOL_SIZE;

    /**
     * Init with context.
     *
     * @param context application context
     */
    public static void init(Context context) {
        sContext = context;
    }

    /**
     * Init with context and log class.
     *
     * @param context  application context
     * @param logProxy log class implements {@link ILog}
     */
    public static void init(Context context, ILog logProxy) {
        sContext = context;
        LogImpl.setLogProxy(logProxy);
    }

    public static void addFlow(Flow flow) {
        sFlowMap.put(flow.getName(), flow);
    }

    public static void addFlow(Map<String, Flow> flowMap) {
        sFlowMap.putAll(flowMap);
    }

    /**
     * Get application context for process information, package usage.
     *
     * @return application context
     */
    public static Context getContext() {
        return sContext;
    }

    /**
     * Set thread pool size used by tasks.
     *
     * @param size thread pool size, value less or equal than 0 will produce a cached thread pool.
     */
    public static void setThreadPoolSize(int size) {
        mThreadPoolSize = size;
    }

    public static Flow getFlow(String flowName) {
        Flow flow = sFlowMap.get(flowName);
        return flow != null ? flow : new Flow(flowName);
    }

    /**
     * start flow.
     *
     * @param flowName flow key, should be unique for each flow.
     */
    public static void start(String flowName) {
        Flow flow = sFlowMap.get(flowName);
        if (flow != null) {
            flow.start();
        }
    }

    /**
     * start flow.
     */
    public static void start(Flow flow) {
        flow.start();
    }

    /**
     * Cancel the flow.
     *
     * @param flowName flow key, should be unique for each flow.
     */
    public static void cancel(String flowName) {
        Flow flow = sFlowMap.get(flowName);
        if (flow != null) {
            flow.cancel();
        }
    }

    /**
     * Get status of flow specified by given name, see {@link Status}.
     *
     * @param flowName flow key, should be unique for each flow.
     * @return flow status in {@code STATUS_UNKNOWN}, {@code STATUS_PENDING_START},
     * {@code STATUS_EXECUTING} and {@code STATUS_DONE}.
     */
    public static int getFlowStatus(String flowName) {
        Flow flow = sFlowMap.get(flowName);
        return flow != null ? flow.getFlowStatus() : Status.STATUS_UNKNOWN;
    }

    /**
     * Get thread pool used internally by Init library.
     *
     * @return thread pool
     */
    public static ExecutorService getThreadPool() {
        if (mThreadPoolSize <= 0) {
            return Executors.newCachedThreadPool();
        } else {
            return Executors.newFixedThreadPool(mThreadPoolSize);
        }
    }
}