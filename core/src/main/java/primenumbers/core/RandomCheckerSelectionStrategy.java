package primenumbers.core;

import java.util.List;

public class RandomCheckerSelectionStrategy implements CheckerSelectionStrategy {

    @Override
    public PrimeNumberChecker selectChecker(List<PrimeNumberChecker> checkers) {
        return checkers.get((int)(Math.random()*(double)checkers.size()));
    }

}
