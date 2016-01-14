package primenumbers.core;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;


public class NumbersConsumer extends NumbersProducerConsumer {
    private List<NumberResultListener> numberResultListeners;
    
    private long timeout=10;
    private TimeUnit timeUnit = TimeUnit.SECONDS;
    private int attempts = 3;
    
    public NumbersConsumer(PriorityBlockingQueue<FutureAndNumber> tasksQueue,
    		List<NumberResultListener> numberResultListeners,
    		ExecutorService primeCheckExecutor,
            PrimeNumberCheckersPool primeNumberCheckersPool,
            NotificationChannel notificationChannel) {
        super(tasksQueue, primeCheckExecutor, primeNumberCheckersPool, notificationChannel);
        this.numberResultListeners = numberResultListeners;
    }

	public long getTimeout() {
		return timeout;
	}

	public void setTimeout(long timeout) {
		this.timeout = timeout;
	}

	public TimeUnit getTimeUnit() {
		return timeUnit;
	}

	public void setTimeUnit(TimeUnit timeUnit) {
		this.timeUnit = timeUnit;
	}

	public int getAttempts() {
		return attempts;
	}

	public void setAttempts(int attempts) {
		this.attempts = attempts;
	}


    @Override
    public void doRun() {
        while(isRunning.get()) {
            if(Thread.currentThread().isInterrupted()) {
                return;
            }
            FutureAndNumber futureAndNumber = null;
			try {
				futureAndNumber = tasksQueue.take();
			} catch (InterruptedException e1) {
				return;
			}
            try {
                NumberResult numberResult = futureAndNumber.getFuture().get(timeout, timeUnit);
                notifyListeners(numberResult);
            } catch (InterruptedException e) {
                return;
            } catch (ExecutionException | TimeoutException e) {
            	if(futureAndNumber.getAttempt() <= attempts) {
            		PrimeNumberChecker checker = primeNumberCheckersPool.getPrimeNumberChecker();
            		Future<NumberResult> result;
                    try {
                        result = primeCheckExecutor.submit(new PrimeCheck(futureAndNumber.getNumber(), checker));
                    } catch (RejectedExecutionException e2) {
                    	if(primeCheckExecutor.isShutdown()) {
                            return;
                        } else {
                        	notificationChannel.notifyError(e, false);
                        	try {
                                Thread.sleep(delayAfterRejection);
                            } catch (InterruptedException e1) {
                                return;
                            }
                        	tasksQueue.put(futureAndNumber);
                        	continue;
                        }
                    }
                    tasksQueue.put(new FutureAndNumber(result, futureAndNumber.getNumber(), futureAndNumber.getAttempt()+1));
            	} else {
            		notificationChannel.notifyError(e, true);
                	return;
            	}
            }
        }
    }
    
    private void notifyListeners(NumberResult numberResult) {
    	for(NumberResultListener listener : numberResultListeners) {
    		listener.numberResultReceived(numberResult);
    	}
    }
}
