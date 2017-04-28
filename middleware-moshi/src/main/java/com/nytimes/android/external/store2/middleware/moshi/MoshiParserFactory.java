package com.nytimes.android.external.store2.middleware.moshi;

import com.nytimes.android.external.cache.Preconditions;
import com.nytimes.android.external.store2.base.Parser;
import com.squareup.moshi.Moshi;

import java.lang.reflect.Type;

import javax.annotation.Nonnull;

import okio.BufferedSource;

/**
 * Factory which returns various Moshi {@link Parser} implementations.
 */
public final class MoshiParserFactory {

    private MoshiParserFactory() {
    }

    /**
     * Returns a new Parser which parses from a String to the specified type, using
     * the provided {@link Moshi} instance.
     */
    @Nonnull
    public static <T> Parser<String, T> createStringParser(@Nonnull Moshi moshi, @Nonnull Type type) {
        Preconditions.checkNotNull(moshi, "moshi cannot be null.");
        Preconditions.checkNotNull(type, "type cannot be null.");
        return new MoshiStringParser<>(moshi, type);
    }

    /**
     * Returns a new Parser which parses from a String to the specified type, using
     * a new default {@link Moshi} instance.
     */
    @Nonnull
    public static <T> Parser<String, T> createStringParser(@Nonnull Class<T> type) {
        return createStringParser(new Moshi.Builder().build(), type);
    }

    /**
     * Returns a new Parser which parses from {@link BufferedSource} to the specified type, using
     * the provided {@link Moshi} instance.
     */
    @Nonnull
    public static <T> Parser<BufferedSource, T> createSourceParser(@Nonnull Moshi moshi, @Nonnull Type type) {
        Preconditions.checkNotNull(moshi, "moshi cannot be null.");
        Preconditions.checkNotNull(type, "type cannot be null.");
        return new MoshiSourceParser<>(moshi, type);
    }

    /**
     * Returns a new Parser which parses from {@link BufferedSource} to the specified type, using
     * a new default configured {@link Moshi} instance.
     */
    @Nonnull
    public static <T> Parser<BufferedSource, T> createSourceParser(@Nonnull Type type) {
        return createSourceParser(new Moshi.Builder().build(), type);
    }
}
