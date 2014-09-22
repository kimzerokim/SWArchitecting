/**
 * @file EventHandler.java
 * @author youngkim
 * @brief EventHandler interface
 */

/**
 * @namespace week8_server
 * @brief project package 
 */
package week8_server;

import java.io.InputStream;

public interface EventHandler {
	public String getHandler();

	public void handleEvent(InputStream is);
}
