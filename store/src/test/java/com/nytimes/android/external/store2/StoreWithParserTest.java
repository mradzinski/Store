package com.nytimes.android.external.store2;

import com.nytimes.android.external.store2.base.Fetcher;
import com.nytimes.android.external.store2.base.Parser;
import com.nytimes.android.external.store2.base.Persister;
import com.nytimes.android.external.store2.base.impl.BarCode;
import com.nytimes.android.external.store2.base.impl.ParsingStoreBuilder;
import com.nytimes.android.external.store2.base.impl.Store;

import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


import io.reactivex.Maybe;
import io.reactivex.Single;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class StoreWithParserTest {

    private static final String DISK = "persister";
    private static final String NETWORK = "fetch";
    @Mock
    Fetcher<String, BarCode> fetcher;
    @Mock
    Persister<String, BarCode> persister;
    @Mock
    Parser<String, String> parser;

    private final BarCode barCode = new BarCode("key", "value");

    @Test
    public void testSimple() throws Exception {
        MockitoAnnotations.initMocks(this);


        Store<String, BarCode> simpleStore = ParsingStoreBuilder.<String, String>builder()
                .persister(persister)
                .fetcher(fetcher)
                .parser(parser)
                .open();

        when(fetcher.fetch(barCode))
                .thenReturn(Single.just(NETWORK));

        when(persister.read(barCode))
                .thenReturn(Maybe.<String>empty())
                .thenReturn(Maybe.just(DISK));

        when(persister.write(barCode, NETWORK))
                .thenReturn(Single.just(true));

        when(parser.apply(DISK)).thenReturn(barCode.getKey());

        String value = simpleStore.get(barCode).blockingGet();
        assertThat(value).isEqualTo(barCode.getKey());
        value = simpleStore.get(barCode).blockingGet();
        assertThat(value).isEqualTo(barCode.getKey());
        verify(fetcher, times(1)).fetch(barCode);
    }

    @Test
    public void testSubclass() throws Exception {
        MockitoAnnotations.initMocks(this);

        Store<String, BarCode> simpleStore = new SampleParsingStore(fetcher, persister, parser);

        when(fetcher.fetch(barCode))
                .thenReturn(Single.just(NETWORK));

        when(persister.read(barCode))
                .thenReturn(Maybe.<String>empty())
                .thenReturn(Maybe.just(DISK));

        when(persister.write(barCode, NETWORK))
                .thenReturn(Single.just(true));

        when(parser.apply(DISK)).thenReturn(barCode.getKey());

        String value = simpleStore.get(barCode).blockingGet();
        assertThat(value).isEqualTo(barCode.getKey());
        value = simpleStore.get(barCode).blockingGet();
        assertThat(value).isEqualTo(barCode.getKey());
        verify(fetcher, times(1)).fetch(barCode);
    }
}
