package sort;

public class BubbleSort implements ISort {
	@Override
	public int[] Sort(int[] input) {
		int n = input.length;
	    for (int pass=1; pass < n; pass++) {  // count how many times
	        // This next loop becomes shorter and shorter
	        for (int i=0; i < n-pass; i++) {
	            if (input[i] > input[i+1]) {
	                // exchange elements
	                int temp = input[i];  input[i] = input[i+1];  input[i+1] = temp;
	            }
	        }
	    }
	    return input;
	}
}
