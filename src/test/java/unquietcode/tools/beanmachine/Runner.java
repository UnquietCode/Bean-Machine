package unquietcode.tools.beanmachine;

import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.PostConstruct;

/**
 * @author Ben Fagin
 * @version 10-12-2011
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:applicationContext-test.xml")
public class Runner {

	@Autowired
    private BeanManager springBeanManager;

	protected BeanMachine machine;

	@PostConstruct
	void init() {
		machine = new BeanMachine(springBeanManager);
	}
}
