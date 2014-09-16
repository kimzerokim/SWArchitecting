package week8_server;

import java.util.List;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.ElementList;

public class HandlerListData {
	@ElementList(entry = "handler", inline = true)
	private List<String> handler;

	@Attribute
	private String name;

	public List<String> getHandler() {
		return handler;
	}

	public String getName() {
		return name;
	}
}