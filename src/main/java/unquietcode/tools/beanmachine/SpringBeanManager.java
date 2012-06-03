package unquietcode.tools.beanmachine;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import java.util.Collection;

/**
 * @author Ben Fagin
 * @version 06-03-2012
 */
@Component
public class SpringBeanManager implements BeanManager, ApplicationListener {
	private BeanMachine beanMachine;

	@Autowired
	ApplicationContext context;


	@Override
	public <T> Collection<T> getBeansOfType(Class<T> type) {
		return context.getBeansOfType(type).values();
	}

	@Override
	public void setBeanMachine(BeanMachine beanMachine) {
		this.beanMachine = beanMachine;
	}

	/**
	 * Respond to application context events, specifically a refresh event
	 * which could invalidate the list of sorted beans.
	 *
	 * @param event the event
	 */
	@Override
	public void onApplicationEvent(ApplicationEvent event) {
		if (event instanceof ContextRefreshedEvent) {
			if (beanMachine != null) { beanMachine.clearCache(); }
		}
	}
}
