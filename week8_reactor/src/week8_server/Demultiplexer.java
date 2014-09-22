/**
 * @file Demultiplexer.java
 * @author youngkim
 * @brief route request with header info to each handler
 */

/**
 * @namespace week8_server
 * @brief project package 
 */
package week8_server;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;

/**
 * @class Demultiplexer
 * @date 2014-09-17
 * @author youngkim, ky200223@nhnnext.org
 * @brief route request with header info to each handler
 * @details get request from socket, parse header then throw request to each
 *          header
 */
public class Demultiplexer implements Runnable {

	private final int HEADER_SIZE = 6;

	private Socket socket;
	private HandleMap handleMap;

	public Demultiplexer(Socket socket, HandleMap handleMap) {
		this.socket = socket;
		this.handleMap = handleMap;
	}

	@Override
	public void run() {
		try {
			InputStream is = socket.getInputStream();

			byte[] buffer = new byte[HEADER_SIZE];
			is.read(buffer);
			String header = new String(buffer);

			handleMap.get(header).handleEvent(is);

			socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}