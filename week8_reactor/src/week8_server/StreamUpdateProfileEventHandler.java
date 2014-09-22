/**
 * @file StreamUpdateProfileEventHandler.java
 * @author youngkim
 * @brief handle request that header is 0x6001
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
 * @class StreamUpdateProfileEventHandler
 * @date 2014-09-17
 * @author youngkim, ky200223@nhnnext.org
 * @brief route request with header info to each handler
 * @details get request that start with 0x6001(header) and print result
 */
public class StreamUpdateProfileEventHandler implements EventHandler {

	public static Logger logger = Logger.getLogger(ServerInitializer.class
			.getName());

	private static final int DATA_SIZE = 1024;
	private static final int TOKEN_NUM = 5;

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

			updateProfile(params);

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * @brief print request
	 * @param string[]
	 * @return none
	 */
	private void updateProfile(String[] params) {
		logger.info("UpdateProfile -> " + " id : " + params[0] + " password : "
				+ params[1] + " name : " + params[2] + " age : " + params[3]
				+ " gender : " + params[4]);
	}

	@Override
	public String getHandler() {
		return "0x6001";
	}
}