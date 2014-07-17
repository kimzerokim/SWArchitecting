package sort;

import java.io.File;

import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

public class sortFactory {
	public static ISort getSortInstance(String filePATH) {
		ISort sortStrategy = null;
		
		Serializer serializer = new Persister();
		File file = new File(filePATH);

		String sortType = null;

		// read XML file
		try {
			XMLParser parser = serializer.read(XMLParser.class, file);
			sortType = "sort." + parser.GetSortType();
			sortStrategy = (ISort) Class.forName(sortType).newInstance();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return sortStrategy;
	}
}
