package primenumbers.core;

/**
 * This interface is used as a way of errors communication between 
 * the number generator, number consumer and the central service
 * @author vasilev
 *
 */
public interface NotificationChannel {
	
	/**
	 * Notifies that error occurred
	 * @param exception The exception that was thrown
	 * @param exit Whether the central service should exit
	 */
	void notifyError(Exception exception, boolean exit);
}
