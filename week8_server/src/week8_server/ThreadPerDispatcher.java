/**
 * @file ThreadPerDispatcher.java
 * @author youngkim
 * @brief dispatcher create thread for each stream
 */

/**
 * @namespace week8_server
 * @brief project package 
 */
package week8_server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * @class ThreadPerDispatcher
 * @date 2014-09-17
 * @author youngkim, ky200223@nhnnext.org
 * @brief dispatcher create thread for each stream
 * @details create thread for each stream to handle request
 */
public class ThreadPerDispatcher implements Dispatcher {

	/**
	 * @brief create thread for demultiplexer (handle request)
	 * @details init demultiplexer and run as thread / create thread for each request
	 * @param serverSocket, handleMap
	 * @return none
	 */
	public void dispatch(ServerSocket serverSocket, HandleMap handleMap) {
		while (true) {
			try {
				Socket socket = serverSocket.accept();

				Runnable demultiplexer = new Demultiplexer(socket, handleMap);
				Thread thread = new Thread(demultiplexer);
				thread.start();

			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

}