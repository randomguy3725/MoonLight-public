package wtf.moonlight.events;

import wtf.moonlight.events.annotations.EventPriority;
import wtf.moonlight.events.annotations.EventTarget;
import wtf.moonlight.events.impl.Event;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class EventManager {
    private final Map<Method, Class<?>> registeredMethodMap;
    private final Map<Method, Object> methodObjectMap;
    private final Map<Class<? extends Event>, List<Method>> priorityMethodMap;

    public EventManager() {
        registeredMethodMap = new ConcurrentHashMap<>();
        methodObjectMap = new ConcurrentHashMap<>();
        priorityMethodMap = new ConcurrentHashMap<>();
    }

    /**
     * Registers one or more objects to associate their methods with event annotations and stores them in the event handler.
     *
     * @param obj One or more objects to register.
     */
    public void register(Object... obj) {
        for (Object object : obj) {
            register(object);
        }
    }

    /**
     * Registers an object to associate its methods with event annotations and stores them in the event handler.
     *
     * @param obj The object to register.
     */
    public void register(Object obj) {
        Class<?> clazz = obj.getClass();
        Method[] methods = clazz.getDeclaredMethods();

        for (Method method : methods) {
            Annotation[] annotations = method.getDeclaredAnnotations();

            for (Annotation annotation : annotations) {
                if (annotation.annotationType() == EventTarget.class && method.getParameterTypes().length == 1) {
                    registeredMethodMap.put(method, method.getParameterTypes()[0]);
                    methodObjectMap.put(method, obj);

                    Class<? extends Event> eventClass = method.getParameterTypes()[0].asSubclass(Event.class);
                    priorityMethodMap.computeIfAbsent(eventClass, k -> new CopyOnWriteArrayList<>()).add(method);
                }
            }
        }
    }

    /**
     * Unregisters an object, removing its associated methods from the event handler.
     *
     * @param obj The object to unregister.
     */
    public void unregister(Object obj) {
        Class<?> clazz = obj.getClass();
        Method[] methods = clazz.getDeclaredMethods();
        for (Method method : methods) {
            if (registeredMethodMap.containsKey(method)) {
                registeredMethodMap.remove(method);
                methodObjectMap.remove(method);
                Class<? extends Event> eventClass = method.getParameterTypes()[0].asSubclass(Event.class);
                List<Method> priorityMethods = priorityMethodMap.get(eventClass);
                if (priorityMethods != null) {
                    priorityMethods.remove(method);
                }
            }
        }
    }

    /**
     * Calls the registered methods associated with the provided event, respecting their priorities.
     *
     * @param event The event to call the registered methods for.
     * @return The modified or processed event after calling the methods.
     */
    public Event call(Event event) {
        Class<? extends Event> eventClass = event.getClass();

        List<Method> methods = priorityMethodMap.get(eventClass);
        if (methods != null) {
            methods.sort(Comparator.comparingInt(method -> {
                EventPriority priority = method.getAnnotation(EventPriority.class);
                return (priority != null) ? priority.value() : 10;
            }));

            for (Method method : methods) {
                Object obj = methodObjectMap.get(method);
                method.setAccessible(true);
                try {
                    method.invoke(obj, event);
                } catch (Exception e) {
                    //e.printStackTrace();
                }
            }
        }

        return event;
    }
}