package net.mcft.copy.betterstorage.utils;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.entity.Entity;
import net.minecraftforge.common.IExtendedEntityProperties;

public final class EntityUtils {
	
	private static Map<Class, String> propertiesLookup = new HashMap<Class, String>();
	
	private EntityUtils() {  }
	
	public static String getIdentifier(Class<? extends IExtendedEntityProperties> propertiesClass) {
		String identifier = propertiesLookup.get(propertiesClass);
		if (identifier == null) {
			try { identifier = (String)propertiesClass.getField("identifier").get(null); }
			catch (Exception e) { throw new Error(e); }
			propertiesLookup.put(propertiesClass, identifier);
		}
		return identifier;
	}
	
	public static <T extends IExtendedEntityProperties> T getProperties(Entity entity, Class<T> propertiesClass) {
		IExtendedEntityProperties properties = entity.getExtendedProperties(getIdentifier(propertiesClass));
		return (propertiesClass.isInstance(properties) ? (T)properties : null);
	}
	
	public static <T extends IExtendedEntityProperties> T createProperties(Entity entity, Class<T> propertiesClass) {
		try {
			T properties = propertiesClass.getConstructor().newInstance();
			entity.registerExtendedProperties(getIdentifier(propertiesClass), properties);
			return properties;
		} catch (Exception e) { throw new Error(e); }
	}
	
	public static <T extends IExtendedEntityProperties> T getOrCreateProperties(Entity entity, Class<T> propertiesClass) {
		T properties = getProperties(entity, propertiesClass);
		return ((properties != null) ? properties : createProperties(entity, propertiesClass));
	}
	
}
