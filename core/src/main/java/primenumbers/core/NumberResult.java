package primenumbers.core;


public class NumberResult {
    private long number;
    private boolean isPrime;
    
    public NumberResult(long number, boolean isPrime) {
        super();
        this.number = number;
        this.isPrime = isPrime;
    }

    public long getNumber() {
        return number;
    }
    
    public boolean isPrime() {
        return isPrime;
    }
}
