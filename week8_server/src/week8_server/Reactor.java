/**
 * @file Reactor.java
 * @author youngkim
 * @brief contains method for reactor server, start / get,set handler
 */

/**
 * @namespace week8_server
 * @brief project package 
 */
package week8_server;

import java.io.IOException;
import java.net.ServerSocket;

/**
 * @class Reactor
 * @date 2014-09-17
 * @author youngkim, ky200223@nhnnext.org
 * @brief contains method for reactor server, start / get,set handler
 * @details create handleMap/open ServerSocket, manage handlerEvent
 */
public class Reactor {
	private ServerSocket serverSocket;
	private HandleMap handleMap;
	
	/**
	 * @brief init variable for server
	 * @details create handleMap, open ServerSocket
	 * @param port
	 * @return none
	 */
	public Reactor(int port) {
		handleMap = new HandleMap();
		try {
			serverSocket = new ServerSocket(port);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * @brief startServer
	 * @details start dispatch
	 * @param none
	 * @return none
	 */
	public void startServer() {

		// Dispatcher dispatcher = new ThreadPerDispatcher();
		Dispatcher dispatcher = new ThreadPoolDispatcher();
		dispatcher.dispatch(serverSocket, handleMap);
	}

	/**
	 * @brief register Handler
	 * @details register Handler at HandleMap
	 * @param handler
	 * @return none
	 */
	public void registerHandler(EventHandler handler) {
		handleMap.put(handler.getHandler(), handler);
	}

	/**
	 * @brief remove Handler
	 * @details remove Handler at HandleMap
	 * @param handler
	 * @return none
	 */
	public void removeHandler(EventHandler handler) {
		handleMap.remove(handler.getHandler());
	}
}