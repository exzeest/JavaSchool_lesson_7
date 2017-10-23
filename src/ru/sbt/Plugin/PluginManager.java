package ru.sbt.Plugin;

import java.io.*;


public class PluginManager extends ClassLoader {
    private final String pluginRootDirectory;

    public PluginManager ( String pluginRootDirectory ) {
        this.pluginRootDirectory = pluginRootDirectory;
    }

    public Plugin load ( String pluginName, String pluginClassName ) {
        Class<?> pluginClass = findLoadedClass (pluginClassName);
        Plugin plugin = null;
        try {
            if (pluginClass != null) {
                return (Plugin) pluginClass.newInstance ();
            }
        } catch (IllegalAccessException | InstantiationException e) {
            e.printStackTrace ();
        }

        File pluginFile = new File (pluginRootDirectory + "\\" + pluginName, pluginClassName + ".class");

        try {
            byte[] classBytes = loadFileAsBytes (pluginFile);
            pluginClass = defineClass (pluginClassName, classBytes, 0, classBytes.length);
            resolveClass (pluginClass);
            plugin = (Plugin) pluginClass.newInstance ();
        } catch (IOException | IllegalAccessException | InstantiationException e1) {
            e1.printStackTrace ();
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



}
