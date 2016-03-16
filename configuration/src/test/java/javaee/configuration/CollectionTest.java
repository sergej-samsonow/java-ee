package javaee.configuration;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

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

    @Mock
    private Map<String, String> data;

    private Collection collection;

    @Before
    public void prepare() {
        collection = spy(new Collection(NAME, data));
    }

    @Test
    public void name() throws Exception {
        collection = new Collection(NAME, Collections.emptyMap());
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
    public void boolOrDefaultReturnTrueDefaultContainsEvaluated() throws Exception {
        doReturn(false).when(collection).contains(KEY);
        assertThat(collection.bool(KEY, true), equalTo(true));
        verify(collection).contains(KEY);
        verify(collection, never()).bool(KEY);
    }

    @Test
    public void boolOrDefaultReturnFalseDefaultContainsEvaluated() throws Exception {
        doReturn(false).when(collection).contains(KEY);
        assertThat(collection.bool(KEY, false), equalTo(false));
        verify(collection).contains(KEY);
        verify(collection, never()).bool(KEY);
    }

    @Test
    public void boolOrDefaultReturnCollectedTrue() throws Exception {
        doReturn(true).when(collection).contains(KEY);
        doReturn(true).when(collection).bool(KEY);
        assertThat(collection.bool(KEY, true), equalTo(true));
        verify(collection).contains(KEY);
        verify(collection).bool(KEY);
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
        doReturn(false).when(collection).contains(KEY);
        assertThat(collection.str(KEY, DEFAULT_STRING_VALUE), equalTo(DEFAULT_STRING_VALUE));
        verify(collection).contains(KEY);
        verify(collection, never()).str(KEY);
    }

    @Test
    public void strOrDefaultReturnCollectedContainsEvaluated() throws Exception {
        doReturn(true).when(collection).contains(KEY);
        doReturn(STR_VALUE).when(collection).str(KEY);
        assertThat(collection.str(KEY, DEFAULT_STRING_VALUE), equalTo(STR_VALUE));
        verify(collection).contains(KEY);
        verify(collection).str(KEY);
    }

    @Test
    public void strOrDefaultReturnNullIfDefaultKeySet() throws Exception {
        doReturn(true).when(collection).contains(KEY);
        doReturn(null).when(collection).str(KEY);
        assertThat(collection.str(KEY, DEFAULT_STRING_VALUE), nullValue());
        verify(collection).contains(KEY);
        verify(collection).str(KEY);
    }

}
