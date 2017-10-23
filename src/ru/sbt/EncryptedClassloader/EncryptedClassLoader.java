package ru.sbt.EncryptedClassloader;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class EncryptedClassLoader extends ClassLoader {
    private final String key;
    private final File dir;

    public EncryptedClassLoader(String key, File dir, ClassLoader parent) {
        super(parent);
        this.key = key;
        this.dir = dir;
    }

    @Override
    public Class<?> loadClass ( String name ) throws ClassNotFoundException {

        Class <?> result = findClass (name);
        File encryptedClassFile = new File (dir + "\\" + name + ".class");
        if (result != null) return result;
        try {
            byte[] classBytes = loadFileAsBytes (encryptedClassFile);
            result = defineClass (name, classBytes, 0, classBytes.length);
        } catch (IOException e) {
            throw new ClassNotFoundException ("Cannot load class " + name + ": " + e);
        } catch (ClassFormatError e) {
            throw new ClassNotFoundException ("Format of class file incorrect for class " + name + ": " + e);
        }

        return result;
    }

    private byte[] loadFileAsBytes ( File file ) throws IOException {
        byte[] result = new byte[(int) file.length ()];
        FileInputStream f = new FileInputStream (file);
        try {
            f.read (result, 0, result.length);
        } finally {
            try {
                f.close ();
            } catch (Exception e) {
            }
        }
        // Encryption
        for (int i = 0 ; i < result.length ; ++i) {
            result[i] += Byte.valueOf (key);
        }
        return result;
    }
}

