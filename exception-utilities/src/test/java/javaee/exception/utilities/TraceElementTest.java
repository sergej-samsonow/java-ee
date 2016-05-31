package javaee.exception.utilities;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;

import org.junit.Before;
import org.junit.Test;

import javaee.exception.utilities.TraceElement;

public class TraceElementTest {
	
	private StackTraceElement stackTraceElement;
	private TraceElement traceElement;

	@Before 
	public void prepare() {
		stackTraceElement = new StackTraceElement("javaee.exceptionprocessor.TraceElementTest", "testGetPackageName", "TraceElementTest", 10);
		traceElement = new TraceElement(stackTraceElement);
	}

	@Test
	public void testGetPackageName() throws Exception {
		assertThat(traceElement.getPackageName(), equalTo("javaee.exceptionprocessor"));
	}

	@Test
	public void testGetPackageNameDefaultPackege() throws Exception {
		stackTraceElement = new StackTraceElement("TraceElementTest", "testGetPackageName", "TraceElementTest", 10);
		traceElement = new TraceElement(stackTraceElement);
		assertThat(traceElement.getPackageName(), nullValue());
	}

	@Test
	public void testGetClassNameOnly() throws Exception {
		assertThat(traceElement.getClassNameOnly(), equalTo("TraceElementTest"));
	}

}
