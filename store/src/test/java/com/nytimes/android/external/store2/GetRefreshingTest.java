package com.nytimes.android.external.store2;

import com.nytimes.android.external.store2.base.Clearable;
import com.nytimes.android.external.store2.base.Fetcher;
import com.nytimes.android.external.store2.base.Persister;
import com.nytimes.android.external.store2.base.impl.BarCode;
import com.nytimes.android.external.store2.base.impl.Store;
import com.nytimes.android.external.store2.base.impl.StoreBuilder;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicInteger;

import javax.annotation.Nonnull;

import io.reactivex.Maybe;
import io.reactivex.Single;
import io.reactivex.observers.TestObserver;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class GetRefreshingTest {
    @Mock
    ClearingPersister persister;
    AtomicInteger networkCalls;
    private Store<Integer, BarCode> store;

    @Before
    public void setUp() {
        networkCalls = new AtomicInteger(0);
        store = StoreBuilder.<Integer>barcode()
                .fetcher(new Fetcher<Integer, BarCode>() {
                    @Nonnull
                    @Override
                    public Single<Integer> fetch(@Nonnull BarCode barCode) {
                        return Single.fromCallable(new Callable<Integer>() {
                            @Override
                            public Integer call() {
                                return networkCalls.incrementAndGet();
                            }
                        });
                    }
                })
                .persister(persister)
                .open();
    }

    @Test
    public void testRefreshOnClear() {
        BarCode barcode = new BarCode("type", "key");
        when(persister.read(barcode))
                .thenReturn(Maybe.<Integer>empty()) //read from disk
                .thenReturn(Maybe.just(1)) //read from disk after fetching from network
                .thenReturn(Maybe.<Integer>empty()) //read from disk after clearing disk cache
                .thenReturn(Maybe.just(1)); //read from disk after making additional network call
        when(persister.write(barcode, 1)).thenReturn(Single.just(true));
        when(persister.write(barcode, 2)).thenReturn(Single.just(true));


        TestObserver<Integer> refreshingObservable = store.getRefreshing(barcode).test();
        refreshingObservable.assertValueCount(1);
        assertThat(networkCalls.intValue()).isEqualTo(1);
        //clearing the store should produce another network call
        store.clear(barcode);
        refreshingObservable.assertValueCount(2);
        assertThat(networkCalls.intValue()).isEqualTo(2);

        store.get(barcode).test().awaitTerminalEvent();
        refreshingObservable.assertValueCount(2);
        assertThat(networkCalls.intValue()).isEqualTo(2);
    }

    @Test
    public void testRefreshOnClearAll() {
        BarCode barcode1 = new BarCode("type", "key");
        BarCode barcode2 = new BarCode("type", "key2");

        when(persister.read(barcode1))
                .thenReturn(Maybe.<Integer>empty()) //read from disk
                .thenReturn(Maybe.just(1)) //read from disk after fetching from network
                .thenReturn(Maybe.<Integer>empty()) //read from disk after clearing disk cache
                .thenReturn(Maybe.just(1)); //read from disk after making additional network call
        when(persister.write(barcode1, 1)).thenReturn(Single.just(true));
        when(persister.write(barcode1, 2)).thenReturn(Single.just(true));

        when(persister.read(barcode2))
                .thenReturn(Maybe.<Integer>empty()) //read from disk
                .thenReturn(Maybe.just(1)) //read from disk after fetching from network
                .thenReturn(Maybe.<Integer>empty()) //read from disk after clearing disk cache
                .thenReturn(Maybe.just(1)); //read from disk after making additional network call

        when(persister.write(barcode2, 1)).thenReturn(Single.just(true));
        when(persister.write(barcode2, 2)).thenReturn(Single.just(true));

        TestObserver<Integer> testObservable1 = store.getRefreshing(barcode1).test();
        TestObserver<Integer> testObservable2 = store.getRefreshing(barcode2).test();
        testObservable1.assertValueCount(1);
        testObservable2.assertValueCount(1);

        assertThat(networkCalls.intValue()).isEqualTo(2);

        store.clear();
        assertThat(networkCalls.intValue()).isEqualTo(4);


    }

    //everything will be mocked
    static class ClearingPersister implements Persister<Integer, BarCode>, Clearable<BarCode> {
        @Override
        public void clear(@Nonnull BarCode key) {
            throw new RuntimeException();
        }

        @Nonnull
        @Override
        public Maybe<Integer> read(@Nonnull BarCode barCode) {
            throw new RuntimeException();
        }

        @Nonnull
        @Override
        public Single<Boolean> write(@Nonnull BarCode barCode, @Nonnull Integer integer) {
            throw new RuntimeException();
        }
    }

}
