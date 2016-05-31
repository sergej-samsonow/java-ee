package javaee.exception.utilities;

public class ClassNameStartsWith {

    private String startWith;

    public ClassNameStartsWith(String startWith) {
        this.startWith = startWith;
    }
    
    public StackTraceElement elementAfterLastOccurency(Exception exception) {
        StackTraceElement[] trace = exception.getStackTrace();
        int last = -1;
        for (int i = 0; i < trace.length; i++) {
            StackTraceElement element = trace[i];
            if (element.getClassName().startsWith(startWith)) {
                last = i;
            }
        }
        return trace[last + 1];
    }

}
