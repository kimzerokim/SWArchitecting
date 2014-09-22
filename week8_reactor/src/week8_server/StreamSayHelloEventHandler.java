/**
 * @file StreanSayHelloEventHandler.java
 * @author youngkim
 * @brief handle request that header is 0x5001
 */

/**
 * @namespace week8_server
 * @brief project package 
 */
package week8_server;

import java.io.IOException;
import java.io.InputStream;
import java.util.StringTokenizer;

import org.apache.log4j.Logger;

/**
 * @class StreamSayHelloEventHandler
 * @date 2014-09-17
 * @author youngkim, ky200223@nhnnext.org
 * @brief route request with header info to each handler
 * @details get request that start with 0x5001(header) and print result
 */
public class StreamSayHelloEventHandler implements EventHandler {

	public static Logger logger = Logger.getLogger(ServerInitializer.class
			.getName());

	private static final int DATA_SIZE = 512;
	private static final int TOKEN_NUM = 2;

	/**
	 * @brief request handler
	 * @details read stream and tokenize for get each data
	 * @param is (inputstream)
	 * @return none
	 */
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

	/**
	 * @brief print request
	 * @param string[]
	 * @return none
	 */
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