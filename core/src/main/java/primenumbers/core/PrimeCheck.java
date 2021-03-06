package primenumbers.core;

import java.util.concurrent.Callable;

/**
 * Task that delegates to {@link PrimeNumberChecker} and returns the result of the check
 * @author vasilev
 *
 */
public class PrimeCheck implements Callable<NumberResult> {

    private long number;
    private PrimeNumberChecker checker;
    
    public PrimeCheck(long number, PrimeNumberChecker checker) {
        this.number = number;
        this.checker = checker;
    }

    @Override
    public NumberResult call() throws Exception {
        boolean isPrime = checker.isPrime(number);
        return new NumberResult(number, isPrime);
    }

}
