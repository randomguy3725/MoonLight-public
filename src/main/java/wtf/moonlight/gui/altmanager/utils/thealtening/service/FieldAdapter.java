package wtf.moonlight.gui.altmanager.utils.thealtening.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Optional;

public final class FieldAdapter {

    private static final Logger LOGGER = LogManager.getLogger();

    private final HashMap<String, MethodHandle> fields = new HashMap<>();
    private static final MethodHandles.Lookup LOOKUP;
    private static Field MODIFIERS;

    public FieldAdapter(String parent) {
        try {
            final Class<?> clazz = Class.forName(parent);
            final Field modifiers = FieldAdapter.MODIFIERS;

            for (Field field : clazz.getDeclaredFields()) {
                field.setAccessible(true);

                final int accessFlags = field.getModifiers();

                if (Modifier.isFinal(accessFlags)) {
                    modifiers.setInt(field, accessFlags & ~Modifier.FINAL);
                }

                MethodHandle handler = LOOKUP.unreflectSetter(field);
                handler = handler.asType(handler.type().generic().changeReturnType(void.class));

                this.fields.put(field.getName(), handler);
            }
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Couldn't load/find the specified class");
        } catch (Exception e) {
            throw new RuntimeException("Couldn't create a method handler for the field");
        }
    }

    public void updateFieldIfPresent(String name, Object newValue) {
        Optional.ofNullable(this.fields.get(name)).ifPresent(setter -> {
            try {
                setter.invokeExact(newValue);
            } catch (Throwable e) {
                LOGGER.warn(e);
            }
        });
    }

    static {
        try {
            MODIFIERS = Field.class.getDeclaredField("modifiers");
            MODIFIERS.setAccessible(true);
        } catch (NoSuchFieldException e) {
            LOGGER.warn(e);
        }

        MethodHandles.Lookup lookupObject;

        try {
            final Field lookupImplField = MethodHandles.Lookup.class.getDeclaredField("IMPL_LOOKUP");
            lookupImplField.setAccessible(true);
            lookupObject = (MethodHandles.Lookup) lookupImplField.get(null);
        } catch (ReflectiveOperationException e) {
            lookupObject = MethodHandles.lookup();
        }

        LOOKUP = lookupObject;
    }
}
