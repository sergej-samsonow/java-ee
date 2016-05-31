package javaee.exception.utilities;

import static org.hamcrest.MatcherAssert.assertThat;

import org.hamcrest.CoreMatchers;
import org.junit.Test;

import business.layer.BusinessLogic;

public class ClassNameStartsWithTest {

    @Test
    public void testElementAfterLastOccurency() throws Exception {
        BusinessLogic subject = new BusinessLogic();
        subject.businessMethod();
        StackTraceElement access = subject.service.access;
        assertThat(access.getClassName(), CoreMatchers.equalTo(BusinessLogic.class.getName()));
    }

}
