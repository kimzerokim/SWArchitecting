/**
 * @file ThreadPoolDispatcher.java
 * @author youngkim
 * @brief dispatcher create thread pool and use for handle request
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
 * @brief dispatcher create thread pool and use for handle request
 * @details create thread pool to handle request
 */
public class ThreadPoolDispatcher implements Dispatcher {

	static final String NUMTHREADS = "8";
	static final String THREADPROP = "Threads";

	private int numThreads;
	
	/**
	 * @brief constructor / set threadNum from system property
	 * @param none
	 * @return none
	 */
	public ThreadPoolDispatcher() {
		numThreads = Integer.parseInt(System
				.getProperty(THREADPROP, NUMTHREADS));
	}
	
	/**
	 * @brief create thread pool for demultiplexer
	 * @details init demultiplexer and run as thread / create thread pool
	 * @param serverSocket, handleMap
	 * @return none
	 */
	public void dispatch(final ServerSocket serverSocket,
			final HandleMap handleMap) {
		for (int i = 0; i < (numThreads - 1); i++) {
			Thread thread = new Thread() {
				public void run() {
					dispatchLoop(serverSocket, handleMap);
				}
			};
			thread.start();
			System.out.println("Created and started Thread = "
					+ thread.getName());
		}

		dispatchLoop(serverSocket, handleMap);
	}
	
	/**
	 * @brief excute demultiplexer (handle request) - run on thread
	 * @param serverSocket, handleMap
	 * @return none
	 */
	private void dispatchLoop(ServerSocket serverSocket, HandleMap handleMap) {
		while (true) {
			try {
				Socket socket = serverSocket.accept();
				Runnable demultiplexer = new Demultiplexer(socket, handleMap);
				demultiplexer.run();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

}