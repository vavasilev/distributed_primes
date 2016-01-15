package primenumbers.core;

public interface PrimeNumberCheckerFactory {
	public PrimeNumberChecker createPrimeNumberChecker() throws PrimeNumberCheckerCreationException;
}
