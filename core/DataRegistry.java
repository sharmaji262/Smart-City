package com.smartcity.core;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Optional;
public class DataRegistry {
    private final ConcurrentHashMap<String, Object> store =
        new ConcurrentHashMap<>();
    public void set(String key, Object value) {
        store.put(key, value);
    }
    public Optional<Object> get(String key) {
        return Optional.ofNullable(store.get(key));
    }
    public double getDouble(String key) {
        Object v = store.get(key);
        return (v instanceof Number n) ? n.doubleValue() : 0.0;
    }
    public int getInt(String key) {
        Object v = store.get(key);
        return (v instanceof Number n) ? n.intValue() : 0;
    }
    public boolean getBool(String key) {
        Object v = store.get(key);
        return (v instanceof Boolean b) && b;
    }
    public String getString(String key) {
        Object v = store.get(key);
        return v != null ? v.toString() : "";
    }
    public boolean has(String key) {
        return store.containsKey(key);
    }
    public void remove(String key) {
        store.remove(key);
    }
}