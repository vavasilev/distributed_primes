package primenumbers.core;

import java.util.List;

public interface CheckerSelectionStrategy {

    PrimeNumberChecker selectChecker(List<PrimeNumberChecker> checkers);
}
