package primenumbers.core;

/**
 * Interface for services that can check if a number is prime
 * @author vasilev
 *
 */
public interface PrimeNumberChecker {

	/**
	 * Checks whether a number is prime
	 * @param number The number to check
	 * @return Is number prime
	 * @throws PrimeNumberCheckerException
	 */
    boolean isPrime(long number) throws PrimeNumberCheckerException;
}
