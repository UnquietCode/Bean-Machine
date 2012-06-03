package unquietcode.tools.beanmachine;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author Ben Fagin
 * @version 10-12-2011
 *
 * Indicates that this class should be sequenced before any of the listed classes.
 * That is, this class should be viewed as a dependency of the listed classes.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Precedes {
	Class[] value();
}
