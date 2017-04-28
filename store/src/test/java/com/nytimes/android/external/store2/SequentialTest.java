package com.nytimes.android.external.store2;

import com.nytimes.android.external.store2.base.Fetcher;
import com.nytimes.android.external.store2.base.impl.BarCode;
import com.nytimes.android.external.store2.base.impl.Store;
import com.nytimes.android.external.store2.base.impl.StoreBuilder;

import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.Callable;

import javax.annotation.Nonnull;

import io.reactivex.Single;

import static org.assertj.core.api.Assertions.assertThat;

public class SequentialTest {

    int networkCalls = 0;
    private Store<Integer, BarCode> store;

    @Before
    public void setUp() {
        networkCalls = 0;
        store = StoreBuilder.<Integer>barcode()
                .fetcher(new Fetcher<Integer, BarCode>() {
                    @Nonnull
                    @Override
                    public Single<Integer> fetch(@Nonnull BarCode barCode) {
                        return Single.fromCallable(new Callable<Integer>() {
                            @Override
                            public Integer call() {
                                return networkCalls++;
                            }
                        });
                    }
                })
                .open();
    }

    @Test
    public void sequentially() {
        BarCode b = new BarCode("one", "two");
        store.get(b).test().awaitTerminalEvent();
        store.get(b).test().awaitTerminalEvent();

        assertThat(networkCalls).isEqualTo(1);
    }

    @Test
    public void parallel() {
        BarCode b = new BarCode("one", "two");
        Single<Integer> first = store.get(b);
        Single<Integer> second = store.get(b);

        first.test().awaitTerminalEvent();
        second.test().awaitTerminalEvent();

        assertThat(networkCalls).isEqualTo(1);
    }
}
