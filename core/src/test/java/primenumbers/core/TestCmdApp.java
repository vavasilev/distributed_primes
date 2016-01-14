package primenumbers.core;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import primenumbers.core.DistributedPrimeNumbersService;
import primenumbers.core.RandomCheckerSelectionStrategy;

public class TestCmdApp {

	public static void main(String[] args) throws InterruptedException, IOException {
		InMemoryPrimeNumbersPersistenceService persistenceService = new InMemoryPrimeNumbersPersistenceService();
      DistributedPrimeNumbersService dpns = new DistributedPrimeNumbersService();
      dpns.setPrimeNumberCheckerFactory(() -> new TestPrimeNumberChecker());
      dpns.setCheckerSelectionStrategy(new RandomCheckerSelectionStrategy());
      dpns.addNumberResultListener(numberResult -> {
    	  persistenceService.saveLastNumber(numberResult.getNumber()); 
    	  if(numberResult.isPrime()) {
    		  persistenceService.savePrimeNumber(numberResult.getNumber());
    	  }
      });
      
      int ch;
      while((ch = System.in.read()) != 'q') {
          if(ch=='s') {
        	  dpns.start(persistenceService.getLastNumber());
          }
          if(ch=='p') {
        	  dpns.stop();
          }
      }
      
      dpns.stop();
      dpns.awaitTermination(10, TimeUnit.SECONDS);
	}

}
