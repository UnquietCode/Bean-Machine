package unquietcode.tools.beanmachine;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Component;

import java.util.*;


/**
 * @author  Ben Fagin
 * @version 10-12-2011
 *
 * @see Precedes
 * @see Succeeds
 */
@Component
public class BeanMachine implements ApplicationListener {

	@Autowired
	ApplicationContext context;

	private Map<Class, List> resultCache = new HashMap<Class, List>();

	/**
	 * Given a type of bean, returns an ordered list of beans.
	 * If there are cyclic dependencies amongst the bean classes
	 * then they will not be detected until runtime calling of this method.
	 *
	 * The lists are cached internally, but could change with an application
	 * context refresh event. This means that a list of beans could become
	 * stale. You should always acquire a list by calling the BeanMachine
	 * methods directly.
	 *
	 * The returned list is unmodifiable! If you find that you are needing to
	 * manipulate the list then perhaps reconsider your implementation.
	 * 
	 *
	 * @param type the class of beans to retrieve and sort
	 * @return an ordered list of beans
	 * @throws CyclicDependenciesException if a cycle is detected while attempting to order the beans
	 */
	@SuppressWarnings("unchecked")
	public <T> List<T> getOrderedList(Class<T> type) throws CyclicDependenciesException {
		if (resultCache.containsKey(type)) {
			return resultCache.get(type);
		}

		Collection<T> list = context.getBeansOfType(type).values();
		List<T> result = sort(list, type);
		result = Collections.unmodifiableList(result);
		resultCache.put(type, result);
		
		return result;
	}

	/**
	 * Respond to application context events, specifically a refresh event
	 * which could invalidate our list of sorted beans.
	 * 
	 * @param event the event
	 */
	public void onApplicationEvent(ApplicationEvent event) {
		if (event instanceof ContextRefreshedEvent) {
			resultCache.clear();
		}
	}

	/*
		Sort the list of beans.
	 */
	private <T> List<T> sort(Collection<T> beans, Class<T> type) throws CyclicDependenciesException {
		List<T> list = new ArrayList<T>(beans);

		// nothing to sort if too small
		if (list.size() < 2) {
			return list;
		}

		// create a graph
		Set<Node> candidates = new HashSet<Node>();
		for (T bean : beans) {
			Node n = retrieveNode(bean.getClass(), null);
			candidates.add(n);
		}

		// Clone all nodes and all of their relations so that we can be destructive.
		// We also apply a filter against the current type.
		Map<Node, Node> cloneMap = new HashMap<Node, Node>();
		Set<Node> temp = new HashSet<Node>();

		for (Node node : candidates) {
			if (type.isAssignableFrom(node.klaus)) {
				temp.add(cloneAndFilter(node, cloneMap, type));
			}
		}
		candidates = temp;

		// remove everyone that has dependencies
		// (if a child exists then so should a parent, and vice-versa)
		Set<Node> removals = new HashSet<Node>();
		for (Node node : candidates) {
			removals.addAll(node.children);
		}
		candidates.removeAll(removals);

		// there should be at least one dependency-free node (the list was not empty)
		if (candidates.isEmpty()) {
			throw new CyclicDependenciesException("Cyclic dependencies detected!");
		}

		// perform a topological sort
		List<Node> ordered = new ArrayList<Node>();
		while (!candidates.isEmpty()) {
			Node n = candidates.iterator().next();
			candidates.remove(n);
			ordered.add(n);

			for (Node c : n.children) {
				c.parents.remove(n);

				if (c.parents.isEmpty()) {
					candidates.add(c);
				}
			}

			n.children.clear(); // 1:1 removal parents to children (avoids concurrent modification above)
		}
		
		// sort the list of provided beans
		order(ordered, list);

		return list;
	}

	private <T> void order(List<Node> orderedNodes, List<T> objects) {
		final Map<Class, Integer> orderMap = new HashMap<Class, Integer>();

		for (int i=0; i < orderedNodes.size(); ++i) {
			orderMap.put(orderedNodes.get(i).klaus, i);
		}

		Collections.sort(objects, new Comparator<T>() {
			public int compare(T o1, T o2) {
				Integer i1 = orderMap.get(o1.getClass());
				Integer i2 = orderMap.get(o2.getClass());

				if (i1 == null || i2 == null) {
					throw new RuntimeException("Not enough information to order. (This is an internal error.)");
				}

				return i1.compareTo(i2);
			}
		});
	}

	
	/*
		Clone the node against a local universe of copies.
		Child and Parent nodes not matching the filter class are discarded.
	 */
	private Node cloneAndFilter(Node node, Map<Node, Node> cloneMap, Class filter) {
		if (cloneMap.containsKey(node)) {
			return cloneMap.get(node);
		}

		// create a new node
		Node n = new Node(node.klaus);
		cloneMap.put(node, n);

		// set all children
		for (Node c : node.children) {
			Node x = cloneAndFilter(c, cloneMap, filter);
			if (filter.isAssignableFrom(x.klaus)) {
				n.children.add(x);
			}
		}

		// set all parents
		for (Node p : node.parents) {
			Node x = cloneAndFilter(p, cloneMap, filter);
			if (filter.isAssignableFrom(x.klaus)) {
				n.parents.add(cloneAndFilter(p, cloneMap, filter));
			}
		}

		return n;
	}

	//--------------------//

	private Set<Class> workingSet1 = new HashSet<Class>();
	private Set<Class> workingSet2 = new HashSet<Class>();
	private Map<Object, Node> nodeCache = new HashMap<Object, Node>();

	/*
		Given a class, retrieve the generated node.
		Scans annotations and other information, sets children and parents.
		Caches the nodes to avoid repeated work.
		Deals with cycles as they come up.
	 */
	private Node retrieveNode(Class bean, Set<Class> workingSet) throws CyclicDependenciesException {
		boolean topLevel = workingSet == null;

		if (topLevel) {
			workingSet1.add(bean);
			workingSet2.add(bean);
		} else {
			// are we in a loop?
			if (workingSet.contains(bean)) {
				throw new CyclicDependenciesException("Cycle detected! Object is " + bean.getName());
			}

			workingSet.add(bean);
		}

		// not cached?
		if (!nodeCache.containsKey(bean)) {
			Node node = new Node(bean);
			nodeCache.put(bean, node);

			// set parents
			Succeeds parentClasses = AnnotationUtils.findAnnotation(bean, Succeeds.class);
			if (parentClasses != null) {
				for (Class obj : parentClasses.value()) {
					Node p = retrieveNode(obj, workingSet1);
					node.parents.add(p);
					p.children.add(node);
				}
			}

			// set children
			Precedes childClasses = AnnotationUtils.findAnnotation(bean, Precedes.class);
			if (childClasses != null) {
				for (Class obj : childClasses.value()) {
					Node c = retrieveNode(obj, workingSet2);
					node.children.add(c);
					c.parents.add(node);
				}
			}

			// first or last?
			First first = AnnotationUtils.findAnnotation(bean, First.class);
			if (first != null) { node.isFirst = true; }

			Last last = AnnotationUtils.findAnnotation(bean, Last.class);
			if (last != null) { node.isLast = true; }
		}

		if (topLevel) {
			workingSet1.remove(bean);
			workingSet2.remove(bean);
		} else {
			workingSet.remove(bean);
		}

		// definitely in the cache by this point
		return nodeCache.get(bean);
	}


	/*
		Struct for holding the processed parent/child relations for
		each class.
	 */
	private static class Node {
		Class klaus;
		Set<Node> children = new HashSet<Node>();
		Set<Node> parents = new HashSet<Node>();
		boolean isFirst = false;
		boolean isLast = false;

		Node(Class klaus) {
			this.klaus = klaus;
		}
	}
}