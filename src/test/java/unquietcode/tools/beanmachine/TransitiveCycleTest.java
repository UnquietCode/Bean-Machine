package unquietcode.tools.beanmachine;

import org.junit.Test;
import org.springframework.stereotype.Component;

/**
 * @author Ben Fagin
 * @version 06-03-2012
 */
public class TransitiveCycleTest extends Runner {

	/*
		A -> B -> C -> A
	 */
	@Test(expected = CyclicDependenciesException.class)
	public void transitiveCycle() throws Exception {
		machine.getOrderedList(TransitiveCycle.class);
	}


	// ------------------------------------------------- //

	@Component
	@Precedes(CycleBeanB.class)
	public static class CycleBeanA implements TransitiveCycle { }

	@Component
	@Precedes(CycleBeanC.class)
	public static class CycleBeanB implements TransitiveCycle { }

	@Component
	@Precedes(CycleBeanA.class)
	public static class CycleBeanC implements TransitiveCycle { }

	public static interface TransitiveCycle { }
}
