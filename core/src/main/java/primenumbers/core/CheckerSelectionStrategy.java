package primenumbers.core;

import java.util.List;

/**
 * A strategy for selecting {@link PrimeNumberChecker}
 * @author vasilev
 *
 */
public interface CheckerSelectionStrategy {

	/**
	 * Selects a {@link PrimeNumberChecker} out of a list of checkers
	 * @param checkers The checkers to select from
	 * @return Selected checker
	 */
    PrimeNumberChecker selectChecker(List<PrimeNumberChecker> checkers);
}
