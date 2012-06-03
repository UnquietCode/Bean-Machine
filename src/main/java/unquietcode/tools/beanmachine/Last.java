package unquietcode.tools.beanmachine;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author Ben Fagin
 * @version 10-12-2011
 *
 * Indicates that this bean should be the last in a list.
 * This is logically equivalent to 'succeeds all beans'.
 * If multiple last beans are encountered, then the ordering
 * is not guaranteed to place the bean at the absolute last
 * position, but rather near the end of the list.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Last { }
