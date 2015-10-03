package cn.zhaiyifan.appinit;

import java.util.concurrent.CountDownLatch;

/**
 * <h1>Atomic abstraction in Init, usually a single-purpose operation.</h1>
 * Created by mark.zhai on 2015/10/2.
 */
public abstract class Task implements Runnable {

    private static final String TAG = "Task";

    private String mTaskName;
    private CountDownLatch mDoneSignal;
    private boolean mIsBlocked = true;
    private long mDelay = 0;

    public Task(String name) {
        mTaskName = name;
    }

    public Task(String name, long delay) {
        mTaskName = name;
        mDelay = delay;
    }

    public Task(String name, boolean isBlocked) {
        mTaskName = name;
        mIsBlocked = isBlocked;
    }

    public Task(String name, boolean isBlocked, long delay) {
        mTaskName = name;
        mIsBlocked = isBlocked;
        mDelay = delay;
    }

    @Override
    public void run() {
        if (mDelay > 0) {
            try {
                Thread.sleep(mDelay);
            } catch (InterruptedException e) {
                LogImpl.w(TAG, getName() + ": " + e.getMessage());
            }
        }

        long startTime = System.currentTimeMillis();

        start();

        long endTime = System.currentTimeMillis();
        LogImpl.i(TAG, getName() + " runs " + (endTime - startTime));

        if (mDoneSignal != null) {
            mDoneSignal.countDown();
        }
    }

    /**
     * Run task.
     */
    protected abstract void start();

    /**
     * Returns true if the task is blocked, by default returns true.
     */
    public boolean isBlocked() {
        return mIsBlocked;
    }

    /**
     * Set done signal.
     *
     * @param doneSignal CountDownLatch signal
     */
    public void setDoneSignal(CountDownLatch doneSignal) {
        this.mDoneSignal = doneSignal;
    }

    /**
     * Returns delay of the task in milliseconds, by default returns 0.
     */
    public long getDelay() {
        return mDelay;
    }

    /**
     * Determine task's process, by default returns true, which means run on all processes.
     *
     * @param processName process name
     * @return whether given processName should run the task.
     */
    public boolean runOnProcess(String processName) {
        return true;
    }

    /**
     * Get name of task.
     *
     * @return Task's name
     */
    public String getName() {
        return mTaskName;
    }
}