package primenumbers.httpclient;

public class HostAndPort {

	private String host;
	private String port;
	
	public HostAndPort(String host, String port) {
		super();
		this.host = host;
		this.port = port;
	}

	public String getHost() {
		return host;
	}

	public String getPort() {
		return port;
	}
}
