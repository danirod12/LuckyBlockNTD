package com.github.danirod12.luckyblock.engine.loader;

import com.github.danirod12.luckyblock.api.model.LuckyDrop;
import com.github.danirod12.luckyblock.engine.drop.LuckyDropType;
import com.github.danirod12.luckyblock.engine.drop.special.SpecialLuckyDrop;

import java.lang.reflect.Method;

public class LuckyDropLoader {
    public LuckyDrop deserialize(String data) {
        String[] raw = data.split(" : ");

        // first argument is a LuckyDropType OR class name, other are considered to be data for the drop
        // myclass.MyType : arg0 : arg1... OR message : Hello world

        Class<?> loader;
        LuckyDropType type = LuckyDropType.fromString(raw[0]);
        if (type != null) {
            loader = type.getClazz();
        } else {
            try {
                loader = Class.forName(raw[0]);
            } catch (ClassNotFoundException e) {
                throw new IllegalArgumentException("Invalid drop type: " + raw[0]);
            }
        }

        String[] dataArgs = new String[raw.length - 1];
        System.arraycopy(raw, 1, dataArgs, 0, dataArgs.length);

        try {
            Method deserializeMethod = loader.getDeclaredMethod("deserialize", String[].class);
            return (LuckyDrop) deserializeMethod.invoke(null, (Object) dataArgs);
        } catch (NoSuchMethodException e) {
            throw new IllegalArgumentException("Drop class " + loader.getName()
                    + " does not have a deserialize method with String[] argument");
        } catch (Throwable e) {
            throw new RuntimeException("Failed to deserialize drop of type " + loader.getName(), e);
        }
    }

    public String serialize(LuckyDrop drop) {
        if (drop instanceof SpecialLuckyDrop) {
            return "SPECIAL : " + String.join(" : ", SpecialDropLoader.serialize(drop)); // TODO rework
        }

        Class<?> clazz = drop.getClass();
        try {
            String[] data = null;
            for (Method method : clazz.getDeclaredMethods()) {
                if (method.getName().equalsIgnoreCase("serialize")
                        && method.getParameterCount() == 1
                        && method.getParameters()[0].getType().isAssignableFrom(clazz)) {
                    data = (String[]) method.invoke(null, drop);
                    break;
                }
            }
            if (data == null) {
                throw new IllegalArgumentException("Drop class " + clazz.getName()
                        + " does not have a serialize method with " + clazz.getSimpleName() + " argument");
            }

            String typeName = null;
            for (LuckyDropType type : LuckyDropType.values()) {
                if (type.getClazz().equals(clazz)) {
                    typeName = type.name();
                    break;
                }
            }
            if (typeName == null) {
                typeName = clazz.getName();
            }
            return typeName + " : " + String.join(" : ", data);
        } catch (Throwable e) {
            throw new RuntimeException("Failed to serialize drop of type " + clazz.getName(), e);
        }
    }
}
