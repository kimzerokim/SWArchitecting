/**
 * @file ServerListData.java
 * @author youngkim
 * @brief class for simpleframework
 */

/**
 * @namespace week8_server
 * @brief project package 
 */
package week8_server;

import java.util.List;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;

@Element(name = "serverList")
public class ServerListData {
	@ElementList(entry="server", inline=true)
	private List<HandlerListData> server;
	
	public List<HandlerListData> getServer() {
		return server;
	}
}