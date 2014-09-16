package week8_server;

import java.io.IOException;
import java.io.InputStream;
import java.util.StringTokenizer;

import org.apache.log4j.Logger;

public class StreamSayHelloEventHandler implements EventHandler {

	public static Logger logger = Logger.getLogger(ServerInitializer.class
			.getName());

	private static final int DATA_SIZE = 512;
	private static final int TOKEN_NUM = 2;

	public void handleEvent(InputStream is) {

		try {
			byte[] buffer = new byte[DATA_SIZE];
			is.read(buffer);
			String data = new String(buffer);

			String[] params = new String[TOKEN_NUM];
			StringTokenizer token = new StringTokenizer(data, "|");

			int i = 0;
			while (token.hasMoreTokens()) {
				params[i] = token.nextToken();
				++i;
			}

			sayHello(params);

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void sayHello(String[] params) {
		System.out.println("SayHello -> name : " + params[0] + "age : "
				+ params[1]);
		logger.info("SayHello -> name : " + params[0] + "age : " + params[1]);
	}

	@Override
	public String getHandler() {
		return "0x5001";
	}
}