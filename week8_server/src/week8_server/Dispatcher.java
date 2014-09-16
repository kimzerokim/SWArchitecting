/**
 * @file Dispatcher.java
 * @author youngkim
 * @brief dispatcher interface
 */

/**
 * @namespace week8_server
 * @brief project package 
 */
package week8_server;

import java.net.ServerSocket;

public interface Dispatcher {
	public void dispatch(ServerSocket serverSocket, HandleMap handleMap);
}