package net.mcft.copy.betterstorage.utils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class ReflectionUtils {
	
	private static Map<Cache, Field> cachedFields = new HashMap<Cache, Field>();
	private static Map<MethodCache, Method> cachedMethods = new HashMap<MethodCache, Method>();
	
	private ReflectionUtils() {  }
	
	
	public static Field findField(Class clazz, String srgName, String mcpName) {
		try {
			for (String name : new String[]{ srgName, mcpName }) {
				Cache fieldCache = new Cache(clazz, name);
				Field field = cachedFields.get(fieldCache);
				if (field == null) {
					try { field = clazz.getDeclaredField(name); }
					catch (NoSuchFieldException ex) { continue; }
					if (field == null) continue;
					field.setAccessible(true);
					cachedFields.put(fieldCache, field);
				}
				return field;
			}
		} catch (SecurityException ex) { throw new RuntimeException(ex); }
		throw new RuntimeException(String.format("Could not find field '%s' for class %s", mcpName, clazz.getName()));
	}
	
	public static <T, U extends T, V> void set(Class<T> clazz, U instance, String srgName, String mcpName, V value) {
		try { findField(clazz, srgName, mcpName).set(instance, value); }
		catch (IllegalArgumentException ex) { throw new RuntimeException(ex); }
		catch (IllegalAccessException ex) { throw new RuntimeException(ex); }
	}
	
	public static <T, U extends T, V> V get(Class<T> clazz, U instance, String srgName, String mcpName) {
		try { return (V)findField(clazz, srgName, mcpName).get(instance); }
		catch (IllegalArgumentException ex) { throw new RuntimeException(ex); }
		catch (IllegalAccessException ex) { throw new RuntimeException(ex); }
	}
	
	
	public static Method findMethod(Class clazz, String srgName, String mcpName, Class... parameterTypes) {
		try {
			for (String name : new String[]{ srgName, mcpName }) {
				MethodCache methodCache = new MethodCache(clazz, name, parameterTypes);
				Method method = cachedMethods.get(methodCache);
				if (method == null) {
					try { method = clazz.getDeclaredMethod(name, parameterTypes); }
					catch (NoSuchMethodException ex) { continue; }
					if (method == null) continue;
					method.setAccessible(true);
					cachedMethods.put(methodCache, method);
				}
				return method;
			}
		} catch (SecurityException ex) { throw new RuntimeException(ex); }
		throw new RuntimeException(String.format("Could not find method '%s' for class %s", mcpName, clazz.getName()));
	}
	
	public static <T, U extends T, V> V invoke(Class<T> clazz, U instance, String srgName, String mcpName, Object... args) {
		Class[] parameterTypes = new Class[args.length / 2];
		Object[] parameters = new Object[args.length / 2];
		for (int i = 0; i < args.length / 2; i++) {
			parameterTypes[i] = (Class)args[i];
			parameters[i] = args[args.length / 2 + i];
		}
		try { return (V)findMethod(clazz, srgName, mcpName, parameterTypes).invoke(instance, parameters); }
		catch (IllegalAccessException ex) { throw new RuntimeException(ex); }
		catch (IllegalArgumentException ex) { throw new RuntimeException(ex); }
		catch (InvocationTargetException ex) { throw new RuntimeException(ex); }
	}
	
	
	private static class Cache {
		public final Class clazz;
		public final String name;
		public Cache(Class clazz, String name) {
			this.clazz = clazz;
			this.name = name;
		}
		@Override public boolean equals(Object obj) {
			if (!(obj instanceof Cache)) return false;
			Cache cache = (Cache)obj;
			return ((cache.clazz == clazz) && (cache.name == name));
		}
		@Override public int hashCode() {
			return (clazz.hashCode() ^ name.hashCode());
		}
	}
	
	private static class MethodCache extends Cache {
		public final Class[] types;
		public MethodCache(Class clazz, String name, Class... parameterTypes) {
			super(clazz, name);
			this.types = parameterTypes;
		}
		@Override
		public boolean equals(Object obj) {
			if (!(obj instanceof MethodCache)) return false;
			MethodCache cache = (MethodCache)obj;
			return (super.equals(cache) && Arrays.equals(cache.types, types));
		}
		@Override
		public int hashCode() {
			return (super.hashCode() ^ Arrays.hashCode(types)); 
		}
	}
	
}
