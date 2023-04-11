import java.util.ArrayList;
import java.util.List;

public class Test {
	public static void main(String[] args) {
		GeneralLoops loops = new Loops();
		
		List<Integer> lower = new ArrayList<Integer>();
		lower.add(0);
		lower.add(0);
		lower.add(1);

		List<Integer> upper = new ArrayList<Integer>();
		upper.add(1);
		upper.add(2);
		upper.add(2);

		loops.setLowerLimits(lower);
		loops.setUpperLimits(upper);

		List<List<Integer>> result = loops.getResult();

		System.out.println(result);
	}
}
