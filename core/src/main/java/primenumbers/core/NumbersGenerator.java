package primenumbers.core;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.RejectedExecutionException;


public class NumbersGenerator extends NumbersProducerConsumer {
    
    private long startNumber = 1;
    
    private long generationDelay = 100;

    public NumbersGenerator(PriorityBlockingQueue<FutureAndNumber> tasksQueue,
            ExecutorService primeCheckExecutor,
            PrimeNumberCheckersPool primeNumberCheckersPool,
            NotificationChannel notificationChannel) {
    	super(tasksQueue, primeCheckExecutor, primeNumberCheckersPool, notificationChannel);
    }

	public long getGenerationDelay() {
		return generationDelay;
	}

	public void setGenerationDelay(long generationDelay) {
		this.generationDelay = generationDelay;
	}

	public void start(long lastNumber, CountDownLatch terminationLatch) {
    	this.startNumber = lastNumber+1;
    	super.start(terminationLatch);
    }

    @Override
    public void doRun() {
        while(isRunning.get() && !primeCheckExecutor.isShutdown()) {
            if(Thread.currentThread().isInterrupted()) {
                return;
            }
            PrimeNumberChecker checker = primeNumberCheckersPool.getPrimeNumberChecker();
            Future<NumberResult> result;
            try {
                result = primeCheckExecutor.submit(new PrimeCheck(startNumber, checker));
            } catch (RejectedExecutionException e) {
                if(primeCheckExecutor.isShutdown()) {
                    return;
                } else {
                	notificationChannel.notifyError(e, false);
                	try {
                        Thread.sleep(delayAfterRejection);
                    } catch (InterruptedException e1) {
                        return;
                    }
                	continue;
                }
            }
            tasksQueue.put(new FutureAndNumber(result, startNumber));
            if(generationDelay > 0) {
	            try {
	                Thread.sleep(generationDelay);
	            } catch (InterruptedException e) {
	                return;
	            }
            }
            startNumber++;
        }
    }
}
