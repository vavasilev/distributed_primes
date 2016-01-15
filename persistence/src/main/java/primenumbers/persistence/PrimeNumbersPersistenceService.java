package primenumbers.persistence;

import java.util.List;

/**
 * Service that can persist the current state of prime number generation
 * @author vasilev
 *
 */
public interface PrimeNumbersPersistenceService {
	/**
	 * Saves the last checked number in to the persistent storage
	 * @param number Number to store
	 * @throws PersistenceException In case of error accessing the storage
	 */
	public void saveLastNumber(long number) throws PersistenceException;
	/**
	 * Saves a prime number in to the persistent storage
	 * @param number Number to store
	 * @throws PersistenceException In case of error accessing the storage
	 */
    public void savePrimeNumber(long number) throws PersistenceException;
    /**
     * Gets the last checked number from the persistent storage
     * @return The last number
     * @throws PersistenceException In case of error accessing the storage
     */
    public long getLastNumber() throws PersistenceException;
    /**
     * Gets all saved prime numbers
     * @return List of saved prime numbers
     * @throws PersistenceException In case of error accessing the storage
     */
    public List<Long> getPrimeNumbers() throws PersistenceException;
}
