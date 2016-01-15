package primenumbers.persistence.file;

import static java.nio.file.StandardOpenOption.APPEND;
import static java.nio.file.StandardOpenOption.CREATE;
import static java.nio.file.StandardOpenOption.WRITE;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import primenumbers.persistence.PersistenceException;
import primenumbers.persistence.PrimeNumbersPersistenceService;

public class FilePrimeNumbersPersistenceService implements
		PrimeNumbersPersistenceService {

	private Path primeNumbersFile;
	private Path lastNumberFile;

	private long lastNumber = -1;
	private long[] primeNumbers;
	private int bufferEnd = 0;
	private boolean bufferIsFull = false;

	public FilePrimeNumbersPersistenceService(int bufferSize) {
		primeNumbers = new long[bufferSize];
	}

	public long getBufferSize() {
		return primeNumbers.length;
	}

	public Path getPrimeNumbersFile() {
		return primeNumbersFile;
	}

	public void setPrimeNumbersFile(Path primeNumbersFile) {
		this.primeNumbersFile = primeNumbersFile;
	}

	public Path getLastNumberFile() {
		return lastNumberFile;
	}

	public void setLastNumberFile(Path lastNumberFile) {
		this.lastNumberFile = lastNumberFile;
	}

	@Override
	public void saveLastNumber(long number) throws PersistenceException {
		lastNumber = number;
		String numberAsString = "" + number;

		try (FileChannel fc = (FileChannel.open(lastNumberFile, CREATE, WRITE))) {
			fc.write(ByteBuffer.wrap(numberAsString.getBytes()), 0);
		} catch (IOException e) {
			throw new PersistenceException(e);
		}
	}

	@Override
	public void savePrimeNumber(long number) throws PersistenceException {
		writeInBuffer(number);
		
		String numberAsString = number+"\n";

		try (BufferedWriter writer = Files.newBufferedWriter(primeNumbersFile, CREATE, APPEND)) {
			writer.write(numberAsString);
		} catch (IOException e) {
			throw new PersistenceException(e);
		}
	}

	@Override
	public long getLastNumber() throws PersistenceException {
		if (lastNumber != -1) {
			return lastNumber;
		}
		try {
			if(!Files.exists(lastNumberFile)) {
				lastNumber = 0;
				return lastNumber;
			}
			byte[] bytes = Files.readAllBytes(lastNumberFile);
			String numberAsString = new String(bytes);
			lastNumber = Long.parseLong(numberAsString);
			return lastNumber;
		} catch (IOException e) {
			throw new PersistenceException(e);
		}
	}

	@Override
	public List<Long> getPrimeNumbers() throws PersistenceException {
		if(bufferEnd == 0 && !bufferIsFull) {
			if(!Files.exists(primeNumbersFile)) {
				return new ArrayList<Long>();
			}
			try (BufferedReader reader = Files.newBufferedReader(primeNumbersFile)) {
				String line = null;
			    while ((line = reader.readLine()) != null) {
			    	long number = Long.parseLong(line);
			    	writeInBuffer(number);
			    }
			} catch (IOException e) {
				throw new PersistenceException(e);
			}
		}
		
		List<Long> result = new ArrayList<Long>();
		int pos = bufferIsFull ? bufferEnd : 0;
		boolean runFirst = bufferIsFull;
		
		while(runFirst || pos != bufferEnd) {
			runFirst = false;
			result.add(primeNumbers[pos]);
			pos = (pos + 1) % primeNumbers.length;
		}
		
		return result;
	}

	private void writeInBuffer(long number) {
		primeNumbers[bufferEnd] = number;
		bufferEnd++;
		if (bufferEnd >= primeNumbers.length) {
			bufferEnd = 0;
			bufferIsFull = true;
		}
	}
}
