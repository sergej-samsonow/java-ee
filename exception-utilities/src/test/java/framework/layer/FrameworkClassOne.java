package framework.layer;

import javaee.exception.utilities.ClassNameStartsWith;

public class FrameworkClassOne {

    public StackTraceElement access;
    protected void internalMethod() {
       FrameworkClassTwo subservice = new FrameworkClassTwo();
       subservice.method();
    }
    public void methodForBusinessLogic() {
        try {
            internalMethod();
        }
        catch (Exception exception) {
            ClassNameStartsWith filter = new ClassNameStartsWith("framework.layer");
            access = filter.elementAfterLastOccurency(exception);
        }
    }

}
