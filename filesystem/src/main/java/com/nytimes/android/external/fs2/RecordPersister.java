package com.nytimes.android.external.fs2;

import com.nytimes.android.external.fs2.filesystem.FileSystem;
import com.nytimes.android.external.store2.base.RecordProvider;
import com.nytimes.android.external.store2.base.RecordState;
import com.nytimes.android.external.store2.base.impl.BarCode;

import java.util.concurrent.TimeUnit;

import javax.annotation.Nonnull;
import javax.inject.Inject;

public class RecordPersister extends SourcePersister implements RecordProvider<BarCode> {

    @Nonnull
    private final TimeUnit expirationUnit;
    private final long expirationDuration;

    @Inject
    public RecordPersister(FileSystem fileSystem,
                           long expirationDuration,
                           @Nonnull TimeUnit expirationUnit) {
        super(fileSystem);
        this.expirationDuration = expirationDuration;
        this.expirationUnit = expirationUnit;
    }

    public static RecordPersister create(FileSystem fileSystem,
                                         long expirationDuration,
                                         @Nonnull TimeUnit expirationUnit) {
        return new RecordPersister(fileSystem, expirationDuration, expirationUnit);
    }

    @Nonnull
    @Override
    public RecordState getRecordState(@Nonnull BarCode barCode) {
        return sourceFileReader.getRecordState(barCode, expirationUnit, expirationDuration);
    }
}
