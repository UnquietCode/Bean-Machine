package unquietcode.tools.beanmachine;

import java.util.Collection;

/**
 * @author Ben Fagin
 * @version 06-03-2012
 *
 * BeanManager implementations fulfil requests to retrieve lists of beans.
 * They also alert the {@link BeanMachine} about changes in the application
 * state.
 */
public interface BeanManager {
	<T> Collection<T> getBeansOfType(Class<T> type);
	void setBeanMachine(BeanMachine beanMachine);
}
