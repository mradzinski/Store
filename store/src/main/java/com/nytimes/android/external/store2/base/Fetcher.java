package com.nytimes.android.external.store2.base;

import javax.annotation.Nonnull;

import io.reactivex.Single;


/**
 * Interface for fetching new data for a Store
 *
 * @param <Raw> data type before parsing
 */
public interface Fetcher<Raw, Key> {

    /**
     * @param key Container with Key and Type used as a request param
     * @return Observable that emits {@link Raw} data
     */
    @Nonnull
    Single<Raw> fetch(@Nonnull Key key);
}
