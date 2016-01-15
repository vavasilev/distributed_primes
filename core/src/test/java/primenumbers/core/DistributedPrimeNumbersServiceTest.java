package primenumbers.core;

import static org.junit.Assert.*;

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.junit.Before;
import org.junit.Test;

public class DistributedPrimeNumbersServiceTest {

	InMemoryPrimeNumbersPersistenceService persistenceService;
	DistributedPrimeNumbersService dpns;
	TestPrimeNumberChecker testPrimeNumberChecker = new TestPrimeNumberChecker();

	@Before
	public void setup() {
		persistenceService = new InMemoryPrimeNumbersPersistenceService();
		dpns = new DistributedPrimeNumbersService();

		dpns.setPrimeNumberCheckerFactory(() -> testPrimeNumberChecker);
		dpns.setCheckerSelectionStrategy(new RandomCheckerSelectionStrategy());
		dpns.addNumberResultListener(numberResult -> {
			persistenceService.saveLastNumber(numberResult.getNumber());
			if (numberResult.isPrime()) {
				persistenceService.savePrimeNumber(numberResult.getNumber());
			}
		});
	}

	@Test
	public void test() throws InterruptedException, PrimeNumberCheckerCreationException {
		dpns.start(0);
		long [] limit = {10};
		dpns.addNumberResultListener(numberResult -> {
			if (numberResult.getNumber() > limit[0]) {
				dpns.stop();
			}
		});
		dpns.awaitTermination(10000, TimeUnit.SECONDS);
		
		assertFisrtNumbers();
		
		testPrimeNumberChecker.setSleepTime(10);
		limit[0] = 100;
		dpns.setGenerationDelay(10);
		dpns.start(persistenceService.getLastNumber());
		dpns.awaitTermination(10000, TimeUnit.SECONDS);
		
		testPrimeNumberChecker.setSleepTime(0);
		limit[0] = 1000;
		dpns.setGenerationDelay(0);
		dpns.start(persistenceService.getLastNumber());
		dpns.awaitTermination(10000, TimeUnit.SECONDS);
		
		limit[0] = 2000;
		dpns.start(persistenceService.getLastNumber());
		dpns.awaitTermination(10000, TimeUnit.SECONDS);
		
		assertPrimeAndStrictMonothomicalGrowth();
	}

	private void assertFisrtNumbers() {
		List<Long> primeNumbers = persistenceService.getPrimeNumbers();
		assertEquals(1, primeNumbers.get(0).longValue());
		assertEquals(2, primeNumbers.get(1).longValue());
		assertEquals(3, primeNumbers.get(2).longValue());
		assertEquals(5, primeNumbers.get(3).longValue());
		assertEquals(7, primeNumbers.get(4).longValue());
	}
	
	private void assertPrimeAndStrictMonothomicalGrowth() {
		List<Long> primeNumbers = persistenceService.getPrimeNumbers();
		long[] lastPrimeNumber = {-1};
		primeNumbers.stream().forEach(primeNumber -> {
			try {
				assertTrue(testPrimeNumberChecker.isPrime(primeNumber));
			} catch (Exception e) {
				fail(e.getMessage());
			}
			if(primeNumber <= lastPrimeNumber[0]) {
				fail("Found out of order number");
			}
			lastPrimeNumber[0] = primeNumber;
		});
	}
}
