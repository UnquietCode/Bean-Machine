package unquietcode.tools.beanmachine;

public class BeanMachineException extends Exception {

	public BeanMachineException(String message) {
		super(message);
	}

	public BeanMachineException(Throwable cause) {
		super(cause);
	}

	public BeanMachineException(String message, Throwable cause) {
		super(message, cause);
	}
}