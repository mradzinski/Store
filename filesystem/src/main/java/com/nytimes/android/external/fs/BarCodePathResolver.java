package com.nytimes.android.external.fs;

import com.nytimes.android.external.store.base.impl.BarCode;

import javax.annotation.Nonnull;

class BarCodePathResolver implements PathResolver<BarCode> {
    @Nonnull
    @Override
    public String resolve(@Nonnull BarCode barCode) {
        return barCode.getType() + "/" + barCode.getKey();
    }
}
