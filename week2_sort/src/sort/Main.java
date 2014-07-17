package sort;

import java.util.Arrays;

public class Main {	
	public static void main(String args[]) {
		int testArray[] = {3,4,2,5,1,6,7,9,8};
		
		int resultArray[] = null;
		
		//get sort type
		ISort sortStrategy = sortFactory.getSortInstance("sort.xml");
		
		resultArray = sortStrategy.Sort(testArray);
		System.out.println(sortStrategy.getClass());
		System.out.println(Arrays.toString(resultArray));	
	}
}
