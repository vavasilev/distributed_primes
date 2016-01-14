package primenumbers.core;

import java.util.concurrent.atomic.AtomicLong;

public class TestPrimeNumberChecker implements PrimeNumberChecker {
	
	private AtomicLong sleepTime = new AtomicLong(100);

    public long getSleepTime() {
		return sleepTime.get();
	}

	public void setSleepTime(long sleepTime) {
		this.sleepTime.set(sleepTime);
	}

	@Override
    public boolean isPrime(long number) throws PrimeNumberCheckerException {
        long limit = (long)Math.sqrt(number);
        for(long i=2; i<=limit ; i++) {
            if(number % i == 0) {
                return false;
            }
        }
        if(sleepTime.get() > 0) {
	        try {
	            Thread.sleep(sleepTime.get());
	        } catch (InterruptedException e) {
	            // TODO Auto-generated catch block
	            e.printStackTrace();
	        }
        }
        return true;
    }

}
