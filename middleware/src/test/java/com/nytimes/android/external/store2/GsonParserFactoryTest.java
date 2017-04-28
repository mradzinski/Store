package com.nytimes.android.external.store2;

import com.google.gson.Gson;
import com.nytimes.android.external.store2.middleware.GsonParserFactory;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.lang.reflect.Type;

public class GsonParserFactoryTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Mock
    Type type;
    private final Gson gson = new Gson();

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void shouldCreateParsersProperly() {
        GsonParserFactory.createReaderParser(gson, type);
        GsonParserFactory.createSourceParser(gson, type);
        GsonParserFactory.createStringParser(gson, type);
    }

    @Test
    public void shouldThrowExceptionWhenCreatingReaderWithNullType() {
        expectedException.expect(NullPointerException.class);
        GsonParserFactory.createReaderParser(gson, null);
    }

    @Test
    public void shouldThrowExceptionWhenCreatingReaderWithNullGson() {
        expectedException.expect(NullPointerException.class);
        GsonParserFactory.createReaderParser(null, type);
    }

    @Test
    public void shouldThrowExceptionWhenCreatingSourceWithNullType() {
        expectedException.expect(NullPointerException.class);
        GsonParserFactory.createSourceParser(gson, null);
    }

    @Test
    public void shouldThrowExceptionWhenCreatingSourceWithNullGson() {
        expectedException.expect(NullPointerException.class);
        GsonParserFactory.createSourceParser(null, type);
    }

    @Test
    public void shouldThrowExceptionWhenCreatingStringWithNullType() {
        expectedException.expect(NullPointerException.class);
        GsonParserFactory.createStringParser(gson, null);
    }

    @Test
    public void shouldThrowExceptionWhenCreatingStringWithNullGson() {
        expectedException.expect(NullPointerException.class);
        GsonParserFactory.createStringParser(null, type);
    }
}
