package primenumbers.core;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Base class for {@link NumbersGenerator} and {@link NumbersConsumer}
 * @author vasilev
 *
 */
public abstract class NumbersProducerConsumer implements Runnable {
	protected PriorityBlockingQueue<FutureAndNumber> tasksQueue;
	protected AtomicBoolean isRunning = new AtomicBoolean();
	protected ExecutorService primeCheckExecutor;
	protected PrimeNumberCheckersPool primeNumberCheckersPool;
	protected NotificationChannel notificationChannel;
	private CountDownLatch terminationLatch;
	private Thread executorThread;
	
	protected long delayAfterRejection = 1000;
    
    public NumbersProducerConsumer(PriorityBlockingQueue<FutureAndNumber> tasksQueue,
    		ExecutorService primeCheckExecutor,
            PrimeNumberCheckersPool primeNumberCheckersPool,
            NotificationChannel notificationChannel) {
        this.tasksQueue = tasksQueue;
        this.primeCheckExecutor = primeCheckExecutor;
        this.primeNumberCheckersPool = primeNumberCheckersPool;
        this.notificationChannel = notificationChannel;
    }
    
    public void start(CountDownLatch terminationLatch) {
		this.terminationLatch = terminationLatch;
        isRunning.set(true);
        executorThread = new Thread(this);
        executorThread.start();
    }
    
    public void stop() {
        isRunning.set(false);
        executorThread.interrupt();
    }

	public long getDelayAfterRejection() {
		return delayAfterRejection;
	}

	public void setDelayAfterRejection(long delayAfterRejection) {
		this.delayAfterRejection = delayAfterRejection;
	}

	@Override
	public void run() {
		try {
			doRun();
		} finally {
			terminationLatch.countDown();
		}
	}
	
	protected abstract void doRun();
}
