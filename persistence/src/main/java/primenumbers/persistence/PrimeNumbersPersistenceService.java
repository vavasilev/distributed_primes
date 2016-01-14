package primenumbers.persistence;

import java.util.List;

public interface PrimeNumbersPersistenceService {
	
	public void saveLastNumber(long number) throws PersistenceException;
    public void savePrimeNumber(long number) throws PersistenceException;
    public long getLastNumber() throws PersistenceException;
    public List<Long> getPrimeNumbers() throws PersistenceException;
}
