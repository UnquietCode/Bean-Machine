package unquietcode.tools.beanmachine;

public class CyclicDependenciesException extends BeanMachineException {

	public CyclicDependenciesException(String message) {
		super(message);
	}

	public CyclicDependenciesException(Throwable cause) {
		super(cause);
	}

	public CyclicDependenciesException(String message, Throwable cause) {
		super(message, cause);
	}
}