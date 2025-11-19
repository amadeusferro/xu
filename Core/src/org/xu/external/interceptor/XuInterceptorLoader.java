package org.xu.external.interceptor;

import io.github.classgraph.ClassGraph;
import io.github.classgraph.ScanResult;
import org.xu.interceptor.XuPassiveInterceptor;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@SuppressWarnings("rawtypes")
public class XuInterceptorLoader {

    public List<XuPassiveInterceptor> loadExternalInterceptors() {
        String externalInterceptorsPath = "/home/vitor/IdeaProjects/Xu/project/external/interceptors";
        Set<XuPassiveInterceptor> interceptors = new HashSet<>();

        try {
            File interceptorDir = new File(externalInterceptorsPath);
            File[] jarFiles = interceptorDir.listFiles((_, name) -> name.endsWith(".jar"));

            if (jarFiles != null) {
                URL[] urls = new URL[jarFiles.length];
                for (int i = 0; i < jarFiles.length; i++) {
                    urls[i] = jarFiles[i].toURI().toURL();
                }

                URLClassLoader classLoader = new URLClassLoader(urls, XuInterceptorLoader.class.getClassLoader());

                try (ScanResult scanResult = new ClassGraph()
                        .enableClassInfo()
                        .overrideClassLoaders(classLoader)
                        .scan()) {

                    List<Class<XuPassiveInterceptor>> interceptorClasses = scanResult
                            .getClassesImplementing(XuPassiveInterceptor.class.getName())
                            .loadClasses(XuPassiveInterceptor.class);

                    for (Class<? extends XuPassiveInterceptor> interceptorClass : interceptorClasses) {
                        if (!interceptorClass.getPackage().getName().startsWith("org.xu.")) {
                            interceptors.add(interceptorClass.getDeclaredConstructor().newInstance());
                        }
                    }
                } catch (InvocationTargetException | InstantiationException | IllegalAccessException |
                         NoSuchMethodException e) {
                    throw new RuntimeException(e);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return new ArrayList<>(interceptors);
    }
}
