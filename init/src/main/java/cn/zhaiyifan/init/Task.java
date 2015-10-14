package cn.zhaiyifan.init;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
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
    private int mStatus = Status.STATUS_PENDING_START;
    private static InternalHandler sHandler;
    private static final int MESSAGE_POST_RUSULT = 0x1;
    // for asynchronous task chain
    private Task mParentTask;
    private Task mChildTask;

    /**
     * Constructor
     *
     * @param name task name
     */
    public Task(String name) {
        mTaskName = name;
    }

    /**
     * Constructor
     *
     * @param name  task name
     * @param delay task delay
     */
    public Task(String name, long delay) {
        mTaskName = name;
        mDelay = delay;
    }

    /**
     * Constructor
     *
     * @param name      task name
     * @param isBlocked if task is blocked
     */
    public Task(String name, boolean isBlocked) {
        mTaskName = name;
        mIsBlocked = isBlocked;
    }

    /**
     * Constructor
     *
     * @param name      task name
     * @param isBlocked if task is blocked
     * @param delay     task delay
     */
    public Task(String name, boolean isBlocked, long delay) {
        mTaskName = name;
        mIsBlocked = isBlocked;
        mDelay = delay;
    }

    /**
     * Normally should not override it
     */
    @Override
    public void run() {
        if (mDelay > 0) {
            try {
                Thread.sleep(mDelay);
            } catch (InterruptedException e) {
                LogImpl.w(TAG, getName() + ": " + e.getMessage());
            }
        }
        if (mParentTask != null) {
            synchronized (this) {
                try {
                    if(mParentTask.getStatus() != Status.STATUS_DONE) {
                        wait();
                    }
                } catch (InterruptedException e) {
                    LogImpl.w(TAG, getName() + ": " + e.getMessage());
                }
            }
        }
        mStatus = Status.STATUS_EXECUTING;

        long startTime = System.currentTimeMillis();

        start();
        getHandler().obtainMessage(MESSAGE_POST_RUSULT, this).sendToTarget();

        long endTime = System.currentTimeMillis();
        LogImpl.i(TAG, getName() + " runs " + (endTime - startTime));

        if (mDoneSignal != null) {
            mDoneSignal.countDown();
        }
        if (mChildTask != null) {
            synchronized (mChildTask) {
                mChildTask.notify();
            }
        }
        mStatus = Status.STATUS_DONE;
    }

    /**
     * This method is invoked by ui thread when the task finish.
     */
    protected void onResult(){};
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
     * Set parent task which blocks this task until it finished.
     *
     * @param task parent task
     */
    public void setParentTask(Task task) {
        mParentTask = task;
        task.setChildTask(this);
    }

    void setChildTask(Task task) {
        mChildTask = task;
    }

    /**
     * Returns delay of the task in milliseconds, by default returns 0.
     */
    public long getDelay() {
        return mDelay;
    }

    /**
     * Get task status
     *
     * @return status
     */
    public int getStatus() {
        return mStatus;
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

    /**
     * Provide a public handler for all the task;
     * @return a handler object
     */
    private static Handler getHandler() {
        synchronized (Task.class) {
            if (sHandler == null) {
                sHandler = new InternalHandler();
            }
            return sHandler;
        }
    }

    private static class InternalHandler extends Handler {
        public InternalHandler() {
            super(Looper.getMainLooper());
        }
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MESSAGE_POST_RUSULT:
                    Task task = (Task) msg.obj;
                    task.onResult();
                    break;
                default:
                    break;
            }
        }
    }
}