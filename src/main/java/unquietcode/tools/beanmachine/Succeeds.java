package unquietcode.tools.beanmachine;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author Ben Fagin
 * @version 10-12-2011
 *
 * Indicates that this class should be sequenced after any of the listed classes.
 * That is, the listed classes should be viewed as a dependency of the annotated class.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Succeeds {
	Class[] value() default EMPTY.class;

	static final class EMPTY {}
}
