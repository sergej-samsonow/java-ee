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
