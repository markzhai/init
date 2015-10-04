package cn.zhaiyifan.appinit;

import android.content.Context;

import java.util.HashMap;
import java.util.Map;

/**
 * <p>Entry to add, start and manage init flow.</p>
 * Created by mark.zhai on 2015/10/2.
 */
public class Init {

    private static Map<String, Flow> sFlowMap = new HashMap<>();
    private static Context sContext;

    public static void init(Context context) {
        sContext = context;
    }

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
     * @return application context
     */
    public static Context getContext() {
        return sContext;
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
}