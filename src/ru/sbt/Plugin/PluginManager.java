package ru.sbt.Plugin;

import java.io.*;


public class PluginManager extends ClassLoader {
    private final String pluginRootDirectory;
    private java.util.Map classesHash = new java.util.HashMap ();

    public PluginManager ( String pluginRootDirectory ) {
        this.pluginRootDirectory = pluginRootDirectory;
    }

    public Plugin load ( String pluginName, String pluginClassName ) {
        Plugin plugin = null;
        try {
            Class <?> pluginClass = findClass (pluginName, pluginClassName);
            plugin = (Plugin) pluginClass.newInstance ();
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
            e.printStackTrace ();
        }


        return plugin;
    }

    private static byte[] loadFileAsBytes ( File file ) throws IOException {
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
        return result;
    }

    private Class findClass ( String pluginName, String pluginClassName ) throws ClassNotFoundException {
        Class result = (Class) classesHash.get (pluginClassName);
        if (result != null) {
            return result;
        }
        File pluginFile = new File (pluginRootDirectory + "\\" + pluginName + "\\" + pluginClassName + ".class");

        try {
            byte[] classBytes = loadFileAsBytes (pluginFile);
            result = defineClass (pluginClassName, classBytes, 0, classBytes.length);
        } catch (IOException e) {
            throw new ClassNotFoundException ("Cannot load class " + pluginClassName + ": " + e);
        } catch (ClassFormatError e) {
            throw new ClassNotFoundException ("Format of class file incorrect for class " + pluginClassName + ": " + e);
        }

        classesHash.put (pluginClassName, result);
        return result;
    }

    @Override
    protected synchronized Class loadClass ( String name, boolean resolve ) throws ClassNotFoundException {
        Class result = findClass (name);
        if (resolve)
            resolveClass (result);
        return result;
    }


}
