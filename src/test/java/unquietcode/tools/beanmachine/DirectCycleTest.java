package unquietcode.tools.beanmachine;

import org.junit.Test;
import org.springframework.stereotype.Component;

/**
 * @author Ben Fagin
 * @version 06-03-2012
 */
public class DirectCycleTest extends Runner {

	/*
		A -> B -> A
	 */
	@Test(expected = CyclicDependenciesException.class)
	public void directCycle() throws Exception {
		machine.getOrderedList(DirectCycle.class);
	}


	// ---------------------------------------------- //

	@Component
	@Precedes(DirectCycleBeanB.class)
	public static class DirectCycleBeanA implements DirectCycle { }

	@Component
	@Precedes(DirectCycleBeanA.class)
	public static class DirectCycleBeanB implements DirectCycle { }

	public static interface DirectCycle { }
}
