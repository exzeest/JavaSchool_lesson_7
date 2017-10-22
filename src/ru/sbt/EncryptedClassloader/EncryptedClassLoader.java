package ru.sbt.EncryptedClassloader;

import java.io.File;

public class EncryptedClassLoader extends ClassLoader {
    private final String key;
    private final File dir;

    public EncryptedClassLoader(String key, File dir, ClassLoader parent) {
        super(parent);
        this.key = key;
        this.dir = dir;
    }
}

