package unquietcode.tools.beanmachine;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

/**
 * @author Ben Fagin
 * @version 06-03-2012
 */
public class PojoManagerTest {

	BeanMachine machine;

	@Before
	public void setup() {
		PojoBeanManager manager = new PojoBeanManager();
		manager.addBean(new PojoA());
		manager.addBean(new PojoB());
		manager.addBean(new PojoC());
		manager.addBean(new PojoD());
		manager.addBean(new PojoE());

		machine = new BeanMachine(manager);
	}

	/*
		D -> B -> A -> C
	 */
	@Test
	public void basic() throws Exception {
		List<Marker> beans = machine.getOrderedList(Marker.class);
		Assert.assertEquals("expected beans", 4, beans.size());

		Assert.assertTrue("expected ordered beans", PojoD.class.equals(beans.get(0).getClass()));
		Assert.assertTrue("expected ordered beans", PojoB.class.equals(beans.get(1).getClass()));
		Assert.assertTrue("expected ordered beans", PojoA.class.equals(beans.get(2).getClass()));
		Assert.assertTrue("expected ordered beans", PojoC.class.equals(beans.get(3).getClass()));
	}

	static class PojoA implements Marker { }

	@Precedes(PojoA.class)
	static class PojoB implements Marker { }

	@Succeeds(PojoA.class)
	static class PojoC implements Marker { }

	@Precedes(PojoB.class)
	static class PojoD implements Marker { }

	static interface Marker { }

	// Not a Marker!
	static class PojoE { }
}
