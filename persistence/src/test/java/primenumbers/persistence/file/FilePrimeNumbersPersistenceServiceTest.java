package primenumbers.persistence.file;

import static org.junit.Assert.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.Test;

import primenumbers.persistence.PersistenceException;

public class FilePrimeNumbersPersistenceServiceTest {

	@Test
	public void test() throws IOException, PersistenceException {
		Path primeNumbersFile = Files.createTempFile(null, ".fpnpst");
		Path lastNumberFile = Files.createTempFile(null, ".fpnpst");
		
		FilePrimeNumbersPersistenceService service = new FilePrimeNumbersPersistenceService(5);
		service.setLastNumberFile(lastNumberFile);
		service.setPrimeNumbersFile(primeNumbersFile);
		
		service.saveLastNumber(6);
		assertEquals(6, service.getLastNumber());
		
		assertTrue(service.getPrimeNumbers().isEmpty());
		
		service.savePrimeNumber(1);
		service.savePrimeNumber(2);
		service.savePrimeNumber(3);
		service.savePrimeNumber(5);
		
		List<Long> primeNumbers = service.getPrimeNumbers();
		assertEquals(4, primeNumbers.size());
		assertEquals(1, primeNumbers.get(0).longValue());
		assertEquals(2, primeNumbers.get(1).longValue());
		assertEquals(3, primeNumbers.get(2).longValue());
		assertEquals(5, primeNumbers.get(3).longValue());
		
		service = new FilePrimeNumbersPersistenceService(5);
		service.setLastNumberFile(lastNumberFile);
		service.setPrimeNumbersFile(primeNumbersFile);
		
		assertEquals(6, service.getLastNumber());
		
		primeNumbers = service.getPrimeNumbers();
		assertEquals(4, primeNumbers.size());
		assertEquals(1, primeNumbers.get(0).longValue());
		assertEquals(2, primeNumbers.get(1).longValue());
		assertEquals(3, primeNumbers.get(2).longValue());
		assertEquals(5, primeNumbers.get(3).longValue());
		
		service.savePrimeNumber(7);
		service.savePrimeNumber(11);
		service.savePrimeNumber(13);
		
		primeNumbers = service.getPrimeNumbers();
		assertEquals(5, primeNumbers.size());
		assertEquals(3, primeNumbers.get(0).longValue());
		assertEquals(5, primeNumbers.get(1).longValue());
		assertEquals(7, primeNumbers.get(2).longValue());
		assertEquals(11, primeNumbers.get(3).longValue());
		assertEquals(13, primeNumbers.get(4).longValue());
		
		service = new FilePrimeNumbersPersistenceService(5);
		service.setLastNumberFile(lastNumberFile);
		service.setPrimeNumbersFile(primeNumbersFile);
		
		primeNumbers = service.getPrimeNumbers();
		assertEquals(5, primeNumbers.size());
		assertEquals(3, primeNumbers.get(0).longValue());
		assertEquals(5, primeNumbers.get(1).longValue());
		assertEquals(7, primeNumbers.get(2).longValue());
		assertEquals(11, primeNumbers.get(3).longValue());
		assertEquals(13, primeNumbers.get(4).longValue());
	}

}
