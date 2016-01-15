package primenumbers.httpclient;

import java.util.List;

import primenumbers.core.PrimeNumberChecker;
import primenumbers.core.PrimeNumberCheckerCreationException;
import primenumbers.core.PrimeNumberCheckerFactory;

public class HttpPrimeNumberCheckerFactory implements PrimeNumberCheckerFactory {
	
	private List<HostAndPort> hosts;
	private int lastFreeHost = 0;
	
	public HttpPrimeNumberCheckerFactory(List<HostAndPort> hosts) {
		this.hosts = hosts;
	}

	@Override
	public PrimeNumberChecker createPrimeNumberChecker() throws PrimeNumberCheckerCreationException {
		if(lastFreeHost >= hosts.size()) {
			throw new PrimeNumberCheckerCreationException("Cannot create http client. Only "+lastFreeHost+" defined");
		}
		HostAndPort hostAndPort = hosts.get(lastFreeHost++);
		return new HttpPrimeNumberChecker(hostAndPort.getHost(), hostAndPort.getPort());
	}

}
