package org.apache.skywalking.apm.agent.core.util;


import org.apache.skywalking.apm.util.StringUtil;

import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author 5yl
 * date: 2022/7/22
 */
public class ThreadLocalUtil {


    public static Map<ThreadLocal, Object> getThreadLocalMap() {
        Map<ThreadLocal, Object> threadLocals = new HashMap<>();
        Thread thread = Thread.currentThread();
        try {
            Field threadLocalsField = Thread.class.getDeclaredField("inheritableThreadLocals");
            threadLocalsField.setAccessible(true);
            Object threadLocalMap = threadLocalsField.get(thread);
            if (Objects.isNull(threadLocalMap)) {
                return Collections.EMPTY_MAP;
            }
            Field tableField = threadLocalMap.getClass().getDeclaredField("table");
            tableField.setAccessible(true);
            Object[] table = (Object[]) tableField.get(threadLocalMap);
            for (int i = 0; i < table.length; i++) {
                Object entry = table[i];
                if (entry != null) {
                    WeakReference<ThreadLocal> threadLocalRef = (WeakReference<ThreadLocal>) entry;
                    ThreadLocal threadLocal = threadLocalRef.get();
                    if (threadLocal != null) {
                        Object threadLocalValue = threadLocal.get();
                        threadLocals.put(threadLocal, threadLocalValue);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return threadLocals;
    }

    public static void iteratorTtl() {
        Map<ThreadLocal, Object> threadLocalMap = getThreadLocalMap();
        threadLocalMap.keySet().forEach(o -> {
            System.out.println(o.getClass().getName());
            if (threadLocalMap.get(o) instanceof String) {
                System.out.println(threadLocalMap.get(o));
            }
        });
    }

    public static String getIdentifier() {
        AtomicReference<String> result = new AtomicReference<>();
        Map<ThreadLocal, Object> threadLocalMap = getThreadLocalMap();
        threadLocalMap.keySet().forEach(o -> {
//            System.out.println(o.getClass().getName());
            if (threadLocalMap.get(o) instanceof String) {
//                System.out.println((String) threadLocalMap.get(o));
                result.set((String) threadLocalMap.get(o));
            }
        });
        return StringUtil.isEmpty(result.get()) ? "none" : result.get();
    }

//    public static void main(String[] args) {
//        TTL.getInstance().set("test");
//        Map<ThreadLocal, Object> threadLocalMap = ThreadLocalUtil.getThreadLocalMap();
//        System.out.println(threadLocalMap);
//        threadLocalMap.keySet().forEach(o -> {
//            System.out.println(o.getClass().getName());
////            System.out.println(threadLocalMap.get(o).getClass().getName());
//            if (threadLocalMap.get(o) instanceof String) {
//                System.out.println(threadLocalMap.get(o));
//            }
//        });
//
//        threadLocalMap.get(TTL.getInstance());
//    }
}
