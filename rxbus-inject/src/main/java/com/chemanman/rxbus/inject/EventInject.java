package com.chemanman.rxbus.inject;

import java.util.HashMap;

/**
 * Created by huilin on 2017/7/14.
 */
@Deprecated
public class EventInject {

    private static final HashMap<String, Inject> injectHashMap = new HashMap<>();

    public static void inject(Object host) {
        String className = host.getClass().getName();
        Inject inject = injectHashMap.get(className);

        if (inject == null) {
            try {
                Class<?> aClass = Class.forName(className + "_BindInject");
                inject = (Inject) aClass.newInstance();
                inject.inject(host);
                injectHashMap.put(className, inject);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }else{
            injectHashMap.get(className).inject(host);
        }
    }

    public static void unInject(Object host) {
        String className = host.getClass().getName();
        Inject inject = injectHashMap.get(className);
        if (inject != null) {
            inject.unInject(host);
        }
    }
}
