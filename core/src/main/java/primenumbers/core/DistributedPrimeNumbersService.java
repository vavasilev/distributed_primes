package primenumbers.core;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class DistributedPrimeNumbersService implements NotificationChannel {
	
	private List<NumberResultListener> numberResultListeners = new ArrayList<>();
	
	private NumbersGenerator numbersGenerator;
	private NumbersConsumer numbersConsumer;
	private PriorityBlockingQueue<FutureAndNumber> tasksQueue;
	private ExecutorService primeCheckExecutor;
    private PrimeNumberCheckersPool primeNumberCheckersPool;
    private PrimeNumberCheckerFactory primeNumberCheckerFactory;
    private CheckerSelectionStrategy checkerSelectionStrategy;
    
    private CountDownLatch terminationLatch;
    
    private int poolSize = 10;
    private int queueSize = 50;
    private int checkersSize = 10;
    
    private long consumptionTimeout=10;
    private TimeUnit consumptionTimeUnit = TimeUnit.SECONDS;
    private int consumptionAttempts = 3;
    private long generationDelay = 100;
    private long delayAfterRejection = 1000;

	public int getPoolSize() {
		return poolSize;
	}

	public void setPoolSize(int poolSize) {
		this.poolSize = poolSize;
	}

	public int getQueueSize() {
		return queueSize;
	}

	public void setQueueSize(int queueSize) {
		this.queueSize = queueSize;
	}

	public int getCheckersSize() {
		return checkersSize;
	}

	public void setCheckersSize(int checkersSize) {
		this.checkersSize = checkersSize;
	}

	public long getConsumptionTimeout() {
		return consumptionTimeout;
	}

	public void setConsumptionTimeout(long consumptionTimeout) {
		this.consumptionTimeout = consumptionTimeout;
	}

	public TimeUnit getConsumptionTimeUnit() {
		return consumptionTimeUnit;
	}

	public void setConsumptionTimeUnit(TimeUnit consumptionTimeUnit) {
		this.consumptionTimeUnit = consumptionTimeUnit;
	}

	public int getConsumptionAttempts() {
		return consumptionAttempts;
	}

	public void setConsumptionAttempts(int consumptionAttempts) {
		this.consumptionAttempts = consumptionAttempts;
	}

	public long getGenerationDelay() {
		return generationDelay;
	}

	public void setGenerationDelay(long generationDelay) {
		this.generationDelay = generationDelay;
	}

	public PrimeNumberCheckerFactory getPrimeNumberCheckerFactory() {
		return primeNumberCheckerFactory;
	}

	public void setPrimeNumberCheckerFactory(PrimeNumberCheckerFactory primeNumberCheckerFactory) {
		this.primeNumberCheckerFactory = primeNumberCheckerFactory;
	}

	public long getDelayAfterRejection() {
		return delayAfterRejection;
	}

	public void setDelayAfterRejection(long delayAfterRejection) {
		this.delayAfterRejection = delayAfterRejection;
	}

	public CheckerSelectionStrategy getCheckerSelectionStrategy() {
		return checkerSelectionStrategy;
	}

	public void setCheckerSelectionStrategy(CheckerSelectionStrategy checkerSelectionStrategy) {
		this.checkerSelectionStrategy = checkerSelectionStrategy;
	}

	public void start(long lastNumber) {
		tasksQueue = new PriorityBlockingQueue<FutureAndNumber>();
		primeCheckExecutor = new ThreadPoolExecutor(poolSize, poolSize, 0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<Runnable>(queueSize));
		primeNumberCheckersPool = new PrimeNumberCheckersPool(checkersSize, primeNumberCheckerFactory, checkerSelectionStrategy);
		primeNumberCheckersPool.init();
		numbersConsumer = new NumbersConsumer(tasksQueue, numberResultListeners, primeCheckExecutor, primeNumberCheckersPool, this);
		numbersConsumer.setAttempts(consumptionAttempts);
		numbersConsumer.setTimeout(consumptionTimeout);
		numbersConsumer.setTimeUnit(consumptionTimeUnit);
		numbersConsumer.setDelayAfterRejection(delayAfterRejection);
		numbersGenerator = new NumbersGenerator(tasksQueue, primeCheckExecutor, primeNumberCheckersPool, this);
		numbersGenerator.setGenerationDelay(generationDelay);
		numbersGenerator.setDelayAfterRejection(delayAfterRejection);
		terminationLatch = new CountDownLatch(2);
		numbersConsumer.start(terminationLatch);
		numbersGenerator.start(lastNumber, terminationLatch);
	}
	
	public void stop() {
		numbersGenerator.stop();
		numbersConsumer.stop();
		primeCheckExecutor.shutdown();
	}
	
	public void hardStop() {
		numbersGenerator.stop();
		numbersConsumer.stop();
		primeCheckExecutor.shutdownNow();
	}
	
	public void awaitTermination(long timeout, TimeUnit unit) throws InterruptedException {
		terminationLatch.await(timeout, unit);
		primeCheckExecutor.awaitTermination(timeout, unit);
	}
	
	@Override
	public void notifyError(Exception exception, boolean exit) {
		System.err.println(exception);
		if(exit) {
			hardStop();
		}
	}

	public void addNumberResultListener(NumberResultListener numberResultListener) {
		numberResultListeners.add(numberResultListener);
	}
	
	public void removeNumberResultListener(NumberResultListener numberResultListener) {
		numberResultListeners.remove(numberResultListener);
	}
}
