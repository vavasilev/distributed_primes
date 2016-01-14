package primenumbers.core;

import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.atomic.AtomicLong;


public class InMemoryPrimeNumbersPersistenceService {
    
    private AtomicLong lastNumber = new AtomicLong(0);
    private Deque<Long> primeNumbers = new ConcurrentLinkedDeque<Long>();
    
    public void saveLastNumber(long number) {
        lastNumber.set(number);
    }
    
    public void savePrimeNumber(long number) {
        primeNumbers.add(number);
        //System.out.println(number);
    }
    
    public long getLastNumber() {
        return lastNumber.get();
    }
    
    public List<Long> getPrimeNumbers() {
        return new ArrayList(primeNumbers);
    }
}
