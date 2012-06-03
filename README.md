# Bean Machine
## What is it?
The Bean Machine is a simple tool for ordering a list of beans in your application. Beans are annotated
in such a way as to define a dependency graph between them, using `@Precedes` / `@Succeeds`. Additional granularity
is available by using the `@First` and `@Last` annotations, which provide ordering hints at each level of the tree.

## New in 1.1
* `BeanManager` abstraction, with out-of-the-box Spring and Pojo managers

## How to use it.
### Spring
* Annotate your beans and register them with Spring through component scanning, or manually delcare them.
* Scan or declare the `SpringBeanManager` component.
* Inject the live `SpringBeanManager` into your application and create a new `BeanMachine` with it.
* request a list of beans by type.
  * `#getOrderedList(Class<T> type)`

### POJO
* Create a new `PojoBeanManager`
* Create a new `BeanMachine` with it.
* Load it up with beans.
* Request an ordered list of beans.

When a list is requested, it is retreived either from the cache or recomputed. The cache is cleared automatically
when the context is refreshed. When the list of beans is first computed the graph is checked for cycles, and if one
is detected an exception is thrown.

## ...why?
The tool was initially written for several projects where pluggable processing steps were being used. Developers
could add their own steps at loadtime or runtime simply by declaring their position relative to other steps
(after 'A' but before 'D'). The beans are retrieved programmatically from Spring, which means that prototype beans
without constructor arguments are also possible.

Care to share how you are using it differently? Send an email!
* sequenced processing of beans
* ordered event notification
* ordering guarantees without having to keep track of `@Order` values

## Thanks!
-Ben  
blouis@unquietcode.com