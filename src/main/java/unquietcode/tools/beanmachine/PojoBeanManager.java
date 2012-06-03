package unquietcode.tools.beanmachine;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author Ben Fagin
 * @version 06-03-2012
 *
 * Implementation of {@link BeanManager} which keeps a list of beans.
 * Retrieving a list of beans by type takes O(n) time to scan the
 * list. Use {@link #clearAll()} to clear the internal list of beans.
 * This will also clear the {@link BeanMachine} cache.
 */
public class PojoBeanManager implements BeanManager {
	private BeanMachine beanMachine;
	private List<Object> beans = new ArrayList<Object>();


	public boolean addBean(Object bean) {
		if (bean == null) {
			throw new IllegalArgumentException("null beans are not allowed");
		}

		return beans.add(bean);
	}

	public void clearAll() {
		beans = new ArrayList<Object>();
		beanMachine.clearCache();
	}

	@Override
	public <T> Collection<T> getBeansOfType(Class<T> type) {
		List<T> filtered = new ArrayList<T>();

		for (Object bean : beans) {
			if (type.isAssignableFrom(bean.getClass())) {
				@SuppressWarnings("unchecked") T cast = (T) bean;
				filtered.add(cast);
			}
		}

		return filtered;
	}

	@Override
	public void setBeanMachine(BeanMachine beanMachine) {
		this.beanMachine = beanMachine;
	}
}
