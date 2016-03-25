package javaee.configuration;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.enterprise.event.Event;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import javaee.configuration.event.collection.InvalidIntegerValue;

@RunWith(MockitoJUnitRunner.class)
public class CollectionTest {

    private static final String DEFAULT_STRING_VALUE = "dv";
    private static final String STR_VALUE = "v";
    private static final String KEY = "k";
    private static final String NAME = "simple";

    private static final String BOOL_STR_TRUE_VALUE = "true";
    private static final String BOOL_STR_FALSE_VALUE = "false";
    private static final Boolean BOOL_TRUE_VALUE = true;
    private static final Boolean BOOL_FALSE_VALUE = false;

    private static final String INTEGER_STR_VALUE = "4801";
    private static final Integer INTEGER_VALUE = 4801;
    private static final Integer DEFAULT_INTEGER_VALUE = 8589;

    @Mock
    private Map<String, String> data;

    @Mock
    private Event<InvalidIntegerValue> event;

    @Spy
    @InjectMocks
    private Collection collection = new Collection(NAME, Collections.emptyMap());

    @Test
    public void name() throws Exception {
        // collection = new Collection(NAME, Collections.emptyMap());
        assertThat(collection.getName(), equalTo(NAME));
    }

    @Test
    public void data() throws Exception {
        assertThat(collection.getData(), equalTo(data));
    }

    @Test
    public void containsTrue() throws Exception {
        String key = KEY;
        when(data.containsKey(key)).thenReturn(true);
        assertThat(collection.contains(key), equalTo(true));
        verify(data).containsKey(key);
    }

    @Test
    public void containsFalse() throws Exception {
        String key = KEY;
        when(data.containsKey(key)).thenReturn(false);
        assertThat(collection.contains(key), equalTo(false));
        verify(data).containsKey(key);
    }

    @Test
    public void boolCollectionCalled() throws Exception {
        collection.bool(KEY);
        verify(data).get(KEY);
    }

    @Test
    public void boolTrueValueReturned() throws Exception {
        when(data.get(KEY)).thenReturn(BOOL_STR_TRUE_VALUE);
        assertThat(collection.bool(KEY), equalTo(BOOL_TRUE_VALUE));
        verify(data).get(KEY);
    }

    @Test
    public void boolNullValueIfEntryIsNull() throws Exception {
        when(data.get(KEY)).thenReturn(null);
        assertThat(collection.bool(KEY), nullValue());
        verify(data).get(KEY);
    }

    @Test
    public void boolFalseValueReturned() throws Exception {
        when(data.get(KEY)).thenReturn(BOOL_STR_FALSE_VALUE);
        assertThat(collection.bool(KEY), equalTo(BOOL_FALSE_VALUE));
        verify(data).get(KEY);
    }

    @Test
    public void boolOrTrueReturnCollectedValue() throws Exception {
        doReturn(false).when(collection).bool(KEY);
        assertThat(collection.boolOrTrue(KEY), equalTo(false));
    }

    @Test
    public void boolOrTrueReturnTrue() throws Exception {
        doReturn(null).when(collection).bool(KEY);
        assertThat(collection.boolOrTrue(KEY), equalTo(true));
    }

    @Test
    public void boolOrFalseReturnCollectedValue() throws Exception {
        doReturn(true).when(collection).bool(KEY);
        assertThat(collection.boolOrFalse(KEY), equalTo(true));
    }

    @Test
    public void boolOrFalseReturnFalse() throws Exception {
        doReturn(null).when(collection).bool(KEY);
        assertThat(collection.boolOrFalse(KEY), equalTo(false));
    }

    @Test
    public void boolOrDefaultReturnTrueDefaultContainsEvaluated() throws Exception {
        assertThat(collection.bool(KEY, true), equalTo(true));
    }

    @Test
    public void boolOrDefaultReturnFalseDefaultContainsEvaluated() throws Exception {
        assertThat(collection.bool(KEY, false), equalTo(false));
    }

    @Test
    public void boolOrDefaultReturnCollectedTrue() throws Exception {
        assertThat(collection.bool(KEY, true), equalTo(true));
        verify(collection).bool(KEY);
    }

    @Test
    public void boolOrDefaultReturnNotNull() throws Exception {
        doReturn(null).when(collection).bool(KEY);
        assertThat(collection.bool(KEY, BOOL_TRUE_VALUE), equalTo(BOOL_TRUE_VALUE));
    }

    @Test
    public void boolOrDefaultReturnNotNullIfDefaultValueIsNull() throws Exception {
        doReturn(null).when(collection).bool(KEY);
        assertThat(collection.bool(KEY, null), equalTo(Collection.DEFAULT_BOOLEAN_VALUE));
    }

    @Test
    public void integerCollectionCalled() throws Exception {
        collection.integer(KEY);
        verify(data).get(KEY);
    }

    @Test
    public void integerValueReturned() throws Exception {
        when(data.get(KEY)).thenReturn(INTEGER_STR_VALUE);
        assertThat(collection.integer(KEY), equalTo(INTEGER_VALUE));
        verify(data).get(KEY);
    }

    @Test
    public void integerInvalidIntegerValue() throws Exception {
        ArgumentCaptor<InvalidIntegerValue> invalidIntegerEvent = ArgumentCaptor.forClass(InvalidIntegerValue.class);
        when(data.get(KEY)).thenReturn("ABC");
        assertThat(collection.integer(KEY), nullValue());
        verify(event).fire(invalidIntegerEvent.capture());
        InvalidIntegerValue data = invalidIntegerEvent.getValue();
        assertThat(data.getCollection(), equalTo(NAME));
        assertThat(data.getKey(), equalTo(KEY));
        assertThat(data.getValue(), equalTo("ABC"));
        assertThat(data.getException(), instanceOf(NumberFormatException.class));
    }

    @Test
    public void integerOrDefaultReturnDefaultContainsEvaluated() throws Exception {
        doReturn(false).when(collection).contains(KEY);
        assertThat(collection.integer(KEY, DEFAULT_INTEGER_VALUE), equalTo(DEFAULT_INTEGER_VALUE));
    }

    @Test
    public void integerOrDefaultReturnCollectedContainsEvaluated() throws Exception {
        doReturn(true).when(collection).contains(KEY);
        doReturn(INTEGER_VALUE).when(collection).integer(KEY);
        assertThat(collection.integer(KEY, DEFAULT_INTEGER_VALUE), equalTo(INTEGER_VALUE));
        verify(collection).integer(KEY);
    }

    @Test
    public void integerOrDefaultReturnNotNull() throws Exception {
        doReturn(true).when(collection).contains(KEY);
        doReturn(null).when(collection).integer(KEY);
        assertThat(collection.integer(KEY, DEFAULT_INTEGER_VALUE), equalTo(DEFAULT_INTEGER_VALUE));
    }

    @Test
    public void integerOrDefaultReturnNotNullIfDefaultValueIsNull() throws Exception {
        doReturn(true).when(collection).contains(KEY);
        doReturn(null).when(collection).integer(KEY);
        assertThat(collection.integer(KEY, null), equalTo(Collection.DEFAULT_INTEGER_VALUE));
    }

    @Test
    public void integerOrDefaultReturnNotNulllIfNotContainsEntryl() throws Exception {
        doReturn(false).when(collection).contains(KEY);
        doReturn(null).when(collection).integer(KEY);
        assertThat(collection.integer(KEY, null), equalTo(Collection.DEFAULT_INTEGER_VALUE));
    }

    @Test
    public void strCollectionCalled() throws Exception {
        collection.str(KEY);
        verify(data).get(KEY);
    }

    @Test
    public void strValueReturned() throws Exception {
        when(data.get(KEY)).thenReturn(STR_VALUE);
        assertThat(collection.str(KEY), equalTo(STR_VALUE));
        verify(data).get(KEY);
    }

    @Test
    public void strOrDefaultReturnDefaultContainsEvaluated() throws Exception {
        assertThat(collection.str(KEY, DEFAULT_STRING_VALUE), equalTo(DEFAULT_STRING_VALUE));
    }

    @Test
    public void strOrDefaultReturnCollectedContainsEvaluated() throws Exception {
        doReturn(STR_VALUE).when(collection).str(KEY);
        assertThat(collection.str(KEY, DEFAULT_STRING_VALUE), equalTo(STR_VALUE));
        verify(collection).str(KEY);
    }

    @Test
    public void strOrDefaultReturnNotNull() throws Exception {
        doReturn(null).when(collection).str(KEY);
        assertThat(collection.str(KEY, DEFAULT_STRING_VALUE), equalTo(DEFAULT_STRING_VALUE));
    }

    @Test
    public void strOrDefaultReturnNotNullIfDefaultValueIsNull() throws Exception {
        doReturn(null).when(collection).str(KEY);
        assertThat(collection.str(KEY, null), equalTo(Collection.DEFAULT_STRING_VALUE));
    }

    @Test
    public void strOrDefaultReturnNotNullIfNotContainsEntry() throws Exception {
        assertThat(collection.str(KEY, null), equalTo(Collection.DEFAULT_STRING_VALUE));
    }

    @Test
    public void immutableInputData() throws Exception {
        Map<String, String> data = new HashMap<>();
        data.put(KEY, STR_VALUE);
        collection = new Collection(NAME, data);
        assertThat(collection.contains(KEY), equalTo(true));
        data.remove(KEY);
        assertThat(collection.contains(KEY), equalTo(true));
    }

    @Test
    public void immutableOutputData() throws Exception {
        Map<String, String> data = new HashMap<>();
        data.put(KEY, STR_VALUE);
        collection = new Collection(NAME, data);
        assertThat(collection.contains(KEY), equalTo(true));
        data = collection.getData();
        data.remove(KEY);
        assertThat(collection.contains(KEY), equalTo(true));
    }

}
