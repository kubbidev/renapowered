package me.kubbidev.renapowered.common.dependencies.classloader;

import java.net.URL;
import java.net.URLClassLoader;

/**
 * A classloader "isolated" from the rest of the application.
 *
 * <p>Used to load specific RenaPowered dependencies without causing conflicts
 * with other plugins, or libraries provided by the application.</p>
 */
public class IsolatedClassLoader extends URLClassLoader {
    static {
        ClassLoader.registerAsParallelCapable();
    }

    public IsolatedClassLoader(URL[] urls) {
        /*
         * ClassLoader#getSystemClassLoader returns the AppClassLoader
         *
         * Calling #getParent on this returns the ExtClassLoader (Java 8) or
         * the PlatformClassLoader (Java 9). Since we want this classloader to
         * be isolated from the app, we set the parent to be the platform
         * class loader.
         */
        super(urls, ClassLoader.getSystemClassLoader().getParent());
    }

}