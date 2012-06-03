package unquietcode.tools.beanmachine;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * @author  Ben Fagin
 * @version 10-12-2011
 */
public class SimpleTest extends Runner {
	private static boolean printMessages = false;

	enum BEAN {
		A, B, C, D, E
	}

	static List<BEAN> helloEvents;
	static List<BEAN> goodbyeEvents;

	@Before
	public void setup() {
		helloEvents = new ArrayList<BEAN>();
		goodbyeEvents = new ArrayList<BEAN>();
	}

	@Test
	public void basic() {
		List<HelloType> hello;
		List<GoodbyeType> goodbye;

		try {
			hello = machine.getOrderedList(HelloType.class);
			goodbye = machine.getOrderedList(GoodbyeType.class);
		} catch (CyclicDependenciesException ex) {
			throw new RuntimeException("error encountered", ex);
		}

		Assert.assertEquals("expected some beans", 3, hello.size());
		Assert.assertEquals("expected some beans", 3, goodbye.size());

		BEAN[] expectedHelloOrder = new BEAN[]{ BEAN.A, BEAN.C, BEAN.E };
		for (HelloType bean : hello) {
			String message = bean.sayHello("Eric");

			if (printMessages) {
				System.out.println(message);
			}
		}

		BEAN[] actuals = helloEvents.toArray(new BEAN[helloEvents.size()]);
		Assert.assertArrayEquals("expected same order of events", expectedHelloOrder, actuals);

		BEAN[] expectedGoodbyeOrder = new BEAN[]{ BEAN.D, BEAN.B, BEAN.C };
		for (GoodbyeType bean : goodbye) {
			String message = bean.sayGoodbye("Samantha");

			if (printMessages) {
				System.out.println(message);
			}
		}

		actuals = goodbyeEvents.toArray(new BEAN[goodbyeEvents.size()]);
		Assert.assertArrayEquals("expected same order of events", expectedGoodbyeOrder, actuals);
	}


	private static String saySomething(String what, String name) {
		return what+", "+name+"!";
	}

	// -------------------------------------------- //

	@Component
	public static class SimpleBeanA implements HelloType {
		public String sayHello(String name) {
			helloEvents.add(BEAN.A);
			return saySomething("Hello", name);
		}
	}

	@Component
	@Succeeds(SimpleBeanA.class)
	public static class SimpleBeanB implements GoodbyeType {
		public String sayGoodbye(String name) {
			goodbyeEvents.add(BEAN.B);
			return saySomething("Goodbye", name);
		}
	}

	@Component
	@Succeeds({SimpleBeanA.class, SimpleBeanB.class})
	@Precedes({SimpleBeanE.class})
	public static class SimpleBeanC implements HelloType, GoodbyeType {
		public String sayHello(String name) {
			helloEvents.add(BEAN.C);
			return saySomething("Hi", name);
		}

		public String sayGoodbye(String name) {
			goodbyeEvents.add(BEAN.C);
			return saySomething("Bye", name);
		}
	}

	@First
	@Component
	public static class SimpleBeanD implements GoodbyeType {
		public String sayGoodbye(String name) {
			goodbyeEvents.add(BEAN.D);
			return saySomething("See you", name);
		}
	}

	@Component
	@Succeeds(SimpleBeanA.class)
	public static class SimpleBeanE implements HelloType {
		public String sayHello(String name) {
			helloEvents.add(BEAN.E);
			return saySomething("Hey", name);
		}
	}

	public static interface GoodbyeType {
		String sayGoodbye(String name);
	}

	public static interface HelloType {
		String sayHello(String name);
	}
}
