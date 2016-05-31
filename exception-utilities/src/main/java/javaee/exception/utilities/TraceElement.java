package javaee.exception.utilities;

public class TraceElement {

	private StackTraceElement element;
	private String fullClassName;
	private String packageName;
	private String classNameOnly;
	public TraceElement(StackTraceElement element) {
		super();
		this.element = element;
		parse();
	}
	private void parse() {
		fullClassName = element.getClassName();
		int lastPoint = element.getClassName().lastIndexOf('.');
		if (lastPoint > -1) {
			packageName = fullClassName.substring(0, lastPoint);
			classNameOnly = fullClassName.substring(lastPoint + 1, fullClassName.length());
		}
		else {
			classNameOnly = fullClassName;
		}

	}
	public String getPackageName() {
		return packageName;
	}
	public String getFullClassName() {
		return fullClassName;
	}
	public String getClassNameOnly() {
		return classNameOnly;
	}
	public String getNestedClassesNames() {
		return null;
	}
	
}
