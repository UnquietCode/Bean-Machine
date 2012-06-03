package unquietcode.tools.beanmachine;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author Ben Fagin
 * @version 10-12-2011
 *
 * Indicates that this bean should be the first in a list.
 * This is logically equivalent to 'precedes all beans'.
 * If multiple first beans are encountered, then the ordering
 * is not guaranteed to place the bean at the absolute first
 * position, but rather near the front of the list.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface First { }
