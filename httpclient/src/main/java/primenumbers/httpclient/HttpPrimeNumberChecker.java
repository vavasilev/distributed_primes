package primenumbers.httpclient;

import java.io.IOException;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import primenumbers.core.PrimeNumberChecker;
import primenumbers.core.PrimeNumberCheckerException;

public class HttpPrimeNumberChecker implements PrimeNumberChecker {
	
	private String host;
	private String port;
	
	public HttpPrimeNumberChecker(String host, String port) {
		this.host = host;
		this.port = port;
	}

	@Override
    public boolean isPrime(long number) throws PrimeNumberCheckerException {
		CloseableHttpClient client = HttpClients.createDefault();
		HttpGet httpGet = new HttpGet("http://"+host+":"+port+"/?number="+number);
		
		String result = null;
		try(CloseableHttpResponse response = client.execute(httpGet);) {
			HttpEntity entity = response.getEntity();
			result = EntityUtils.toString(entity);
			EntityUtils.consume(entity);
		} catch (IOException e) {
			throw new PrimeNumberCheckerException(e);
		}
		
        return Boolean.parseBoolean(result);
    }
}
