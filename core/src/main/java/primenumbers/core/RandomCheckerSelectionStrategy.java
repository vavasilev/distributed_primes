package primenumbers.core;

import java.util.List;

/**
 * Implementation of {@link CheckerSelectionStrategy} that selects a random checker
 * @author vasilev
 *
 */
public class RandomCheckerSelectionStrategy implements CheckerSelectionStrategy {

    @Override
    public PrimeNumberChecker selectChecker(List<PrimeNumberChecker> checkers) {
        return checkers.get((int)(Math.random()*(double)checkers.size()));
    }

}
