package net.mcft.copy.betterstorage.utils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class ReflectionUtils {
	
	public static void set(Class clazz, Object obj, Object value, String name) {
		try {
			Field f = clazz.getDeclaredField(name);
			f.setAccessible(true);
			f.set(obj, value);
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException();
		}
	}
	
	public static Object get(Class clazz, Object obj, String name) {
		try {
			Field f = clazz.getDeclaredField(name);
			f.setAccessible(true);
			return f.get(obj);
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException();
		}
	}
	
	public static Object invoke(Class clazz, Object obj, String methodName, Class[] parameterTypes, Object[] paramters)
	{
		try {
			Method m = clazz.getDeclaredMethod(methodName, parameterTypes);
			m.setAccessible(true);
			return m.invoke(obj, paramters);
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException();
		}
	}
}
