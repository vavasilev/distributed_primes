package primenumbers.core;

/**
 * Interface that should be implemented by the parties interested in events 
 * from the {@link DistributedPrimeNumbersService}
 * @author vasilev
 *
 */
public interface NumberResultListener {

	void numberResultReceived(NumberResult numberResult);
}
