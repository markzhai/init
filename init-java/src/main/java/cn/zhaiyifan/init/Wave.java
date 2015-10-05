package cn.zhaiyifan.init;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * <h1>A wave is made up of several tasks.</h1>
 * Created by mark.zhai on 2015/10/2.
 */
public class Wave {
    private static final String TAG = "Wave";
    private static final long DEFAULT_WAVE_TIMEOUT = 1500;

    private List<Task> mTaskList = new LinkedList<>();

    private String mProcessName;
    private int mSequence;
    private long mTimeout = DEFAULT_WAVE_TIMEOUT;
    private int mStatus = Status.STATUS_UNKNOWN;

    public Wave(int sequence, String processName) {
        mSequence = sequence;
        mProcessName = processName;
        mStatus = Status.STATUS_PENDING_START;
    }

    /**
     * Add task.
     */
    public Wave addTask(Task task) {
        mTaskList.add(task);
        return this;
    }

    /**
     * Get wave status.
     *
     * @return status
     */
    public int getStatus() {
        return mStatus;
    }

    /**
     * Get task status.
     *
     * @param taskName task name
     * @return status
     */
    public int getTaskStatus(String taskName) {
        for (Task task : mTaskList) {
            if (task.getName().equals(taskName)) {
                return task.getStatus();
            }
        }
        return Status.STATUS_UNKNOWN;
    }

    /**
     * Set timeout to this wave.
     *
     * @param timeout timeout in milliseconds
     */
    public Wave setTimeout(long timeout) {
        mTimeout = timeout;
        return this;
    }

    public void start() {
        mStatus = Status.STATUS_EXECUTING;
        List<Task> blockTaskList = new LinkedList<>();
        ExecutorService threadPool = Init.getThreadPool();

        for (Task task : mTaskList) {
            if (task.runOnProcess(mProcessName)) {
                if (task.isBlocked()) {
                    blockTaskList.add(task);
                } else {
                    // just start it
                    threadPool.submit(task);
                }
            }
        }

        if (blockTaskList.size() > 0) {
            CountDownLatch doneSignal = new CountDownLatch(blockTaskList.size());

            for (Task blockTask : blockTaskList) {
                blockTask.setDoneSignal(doneSignal);
                threadPool.submit(blockTask);
            }

            try {
                doneSignal.await(mTimeout, TimeUnit.MILLISECONDS);
            } catch (InterruptedException e) {
                LogImpl.w(TAG, "Wave " + mSequence + "await interrupted. " + e.getMessage());
            }
        }
        mStatus = Status.STATUS_DONE;
    }
}