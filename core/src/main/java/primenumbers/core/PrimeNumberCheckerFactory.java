package primenumbers.core;

/**
 * Abstract factory for {@link PrimeNumberChecker} instances
 * @author vasilev
 *
 */
public interface PrimeNumberCheckerFactory {
	/**
	 * Creates a {@link PrimeNumberChecker} instances
	 * @return The instance created
	 * @throws PrimeNumberCheckerCreationException
	 */
	public PrimeNumberChecker createPrimeNumberChecker() throws PrimeNumberCheckerCreationException;
}
