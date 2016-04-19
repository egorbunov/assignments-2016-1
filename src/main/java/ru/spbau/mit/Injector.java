package ru.spbau.mit;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;


public final class Injector {
    private static HashMap<String, Object> depInstances;
    private static HashSet<String> visited;

    private Injector() {
    }

    private static void reset() {
        depInstances = new HashMap<>();
        resetVisited();
    }

    private static void resetVisited() {
        visited = new HashSet<>();
    }

    private static Object getDependency(Class<?> depClass, List<String> implClsNames) throws Exception {
        // detecting cycle
        if (visited.contains(depClass.getName())) {
            throw new InjectionCycleException();
        }

        // marking, that this call is for instantiation depClass
        visited.add(depClass.getName());

        // if already instantiated, when return
        if (depInstances.containsKey(depClass.getName())) {
            return depInstances.get(depClass.getName());
        }

        // getting class candidates for inst
        List<Class<?>> candidates = new ArrayList<>();
        for (String s : implClsNames) {
            Class<?> aClass = Class.forName(s);
            if (depClass.isAssignableFrom(aClass)) {
                candidates.add(aClass);
            }
        }

        if (candidates.size() > 1) {
            throw new AmbiguousImplementationException();
        } else if (candidates.size() == 0) {
            throw new ImplementationNotFoundException();
        }

        Class<?> forInstantiation = candidates.get(0);

        // checking for constructor without parameters
        Constructor<?> constructor = forInstantiation.getConstructors()[0];
        Class<?>[] pTypes = constructor.getParameterTypes();
        if (pTypes.length == 0) {
            Object inst = constructor.newInstance();
            depInstances.put(depClass.getName(), inst);
            return inst;
        }

        Object[] params = new Object[pTypes.length];
        for (int i = 0; i < params.length; i++) {
            Class<?> type = pTypes[i];
            params[i] = getDependency(type, implClsNames);
        }

        Object obj = constructor.newInstance(params);

        // putting for name of initial dependency (may be interface)
        depInstances.put(depClass.getName(), obj);

        return obj;
    }

    /**
     * Create and initialize object of `rootClassName` class using classes from
     * `implementationClassNames` for concrete dependencies.
     */
    public static Object initialize(String rootClassName, List<String> implementationClassNames) throws Exception {
        reset();

        Class<?> clazz = Class.forName(rootClassName);
        Constructor<?> constructor = clazz.getConstructors()[0];
        Class<?>[] parameterTypes = constructor.getParameterTypes();

        // for preventing cycle dependencies on root class
        visited.add(clazz.getName());

        Object[] params = new Object[parameterTypes.length];
        for (int i = 0; i < params.length; ++i) {
            resetVisited();
            params[i] = getDependency(parameterTypes[i], implementationClassNames);
        }

        // instantiating...
        return constructor.newInstance(params);
    }
}
