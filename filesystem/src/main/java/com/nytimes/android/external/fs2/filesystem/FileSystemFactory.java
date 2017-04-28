package com.nytimes.android.external.fs2.filesystem;

import java.io.File;
import java.io.IOException;

import javax.annotation.Nonnull;

/**
 * Factory for {@link FileSystem}.
 */
public final class FileSystemFactory {
    private FileSystemFactory() {
    }

    /**
     * Creates new instance of {@link FileSystemImpl}.
     *
     * @param root root directory.
     * @return new instance of {@link FileSystemImpl}.
     * @throws IOException
     */
    @Nonnull
    public static FileSystem create(@Nonnull File root) throws IOException {
        return new FileSystemImpl(root);
    }
}
