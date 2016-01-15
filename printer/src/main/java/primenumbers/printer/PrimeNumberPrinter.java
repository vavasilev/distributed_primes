package primenumbers.printer;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.PlainDocument;

import primenumbers.core.DistributedPrimeNumbersService;
import primenumbers.core.PrimeNumberCheckerCreationException;
import primenumbers.core.PrimeNumberCheckerFactory;
import primenumbers.core.RandomCheckerSelectionStrategy;
import primenumbers.httpclient.HostAndPort;
import primenumbers.httpclient.HttpPrimeNumberCheckerFactory;
import primenumbers.persistence.PersistenceException;
import primenumbers.persistence.file.FilePrimeNumbersPersistenceService;

public class PrimeNumberPrinter extends JFrame {

	public static final int NUMBERS_TO_SHOW = 100;
	private Path primeNumbersFile = Paths.get(".primenumbers");
	private Path lastNumberFile = Paths.get(".lastnumber");

	/**
	 * 
	 */
	private static final long serialVersionUID = -2725376692793448785L;

	private List<HostAndPort> hosts;

	private JTextArea textArea;
	private JScrollPane scrollPane;
	private Document document;

	private DistributedPrimeNumbersService dpns;

	public PrimeNumberPrinter(List<HostAndPort> hosts) {
		this.hosts = hosts;

		setTitle("Prime Numbers Printer");
		setSize(500, 350);
		setLocation(10, 200);
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				dpns.stop();
				try {
					dpns.awaitTermination(10, TimeUnit.SECONDS);
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
				System.exit(0);
			}
		});
		document = new PlainDocument();
		textArea = new JTextArea(document);
		textArea.setEditable(false);
		scrollPane = new JScrollPane(textArea);
		add(scrollPane);

		try {
			init();
		} catch (PrimeNumberCheckerCreationException | PersistenceException e) {
			e.printStackTrace();
			setVisible(false);
			System.exit(-1);
		}
	}

	private void init() throws PrimeNumberCheckerCreationException, PersistenceException {
		FilePrimeNumbersPersistenceService persistentService = new FilePrimeNumbersPersistenceService(NUMBERS_TO_SHOW);
		persistentService.setLastNumberFile(lastNumberFile);
		persistentService.setPrimeNumbersFile(primeNumbersFile);

		PrimeNumberCheckerFactory primeNumberCheckerFactory = new HttpPrimeNumberCheckerFactory(hosts);

		dpns = new DistributedPrimeNumbersService();
		dpns.setPrimeNumberCheckerFactory(primeNumberCheckerFactory);
		dpns.setCheckerSelectionStrategy(new RandomCheckerSelectionStrategy());
		dpns.setCheckersSize(hosts.size());
		dpns.addNumberResultListener(numberResult -> {
			try {
				persistentService.saveLastNumber(numberResult.getNumber());
				if (numberResult.isPrime()) {
					persistentService.savePrimeNumber(numberResult.getNumber());
				}
			} catch (PersistenceException e) {
				e.printStackTrace();
			}
		});
		
		dpns.addNumberResultListener(numberResult -> {
			if(numberResult.isPrime()) {
				SwingUtilities.invokeLater(() -> {
					try {
						document.insertString(document.getLength(), numberResult.getNumber()+"\n", null);
						textArea.setCaretPosition(textArea.getDocument().getLength());
					} catch (BadLocationException e) {
						e.printStackTrace();
					}
				});
			}
		});
		
		try {
			List<Long> primeNumbers = persistentService.getPrimeNumbers();
			for(Long primeNumber : primeNumbers) {
				document.insertString(document.getLength(), primeNumber+"\n", null);
			}
		} catch (BadLocationException e) {
			e.printStackTrace();
		}
		
		dpns.start(persistentService.getLastNumber());
	}

	public static void main(String[] args) {
		List<HostAndPort> hosts = new ArrayList<>();

		if (args.length == 0) {
			System.err.println("Please, specify list of hosts and ports to connect to in the form \"host:port\"");
			return;
		}

		for (String arg : args) {
			int hostPortSeparatorIndex = arg.indexOf(':');
			String host = "";
			String port = "80";
			if (hostPortSeparatorIndex < 0) {
				host = arg;
			} else {
				host = arg.substring(0, hostPortSeparatorIndex);
				port = arg.substring(hostPortSeparatorIndex + 1);
			}
			hosts.add(new HostAndPort(host, port));
		}

		JFrame f = new PrimeNumberPrinter(hosts);
		f.setVisible(true);
	}
}
