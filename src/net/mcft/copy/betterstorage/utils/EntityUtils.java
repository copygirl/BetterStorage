package net.mcft.copy.betterstorage.utils;

import net.minecraft.entity.Entity;
import net.minecraftforge.common.IExtendedEntityProperties;

public class EntityUtils {
	
	private EntityUtils() {  }
	
	private static String getIdentifier(Class<? extends IExtendedEntityProperties> propertiesClass) {
		try { return (String)propertiesClass.getField("identifier").get(null); }
		catch (Exception e) { throw new RuntimeException(e); }
	}
	
	public static <T extends IExtendedEntityProperties> T getProperties(Entity entity, Class<T> propertiesClass) {
		String identifier = getIdentifier(propertiesClass);
		IExtendedEntityProperties properties = entity.getExtendedProperties(identifier);
		return (propertiesClass.isInstance(properties) ? (T)properties : null);
	}
	
	public static <T extends IExtendedEntityProperties> T createProperties(Entity entity, Class<T> propertiesClass) {
		try {
			T properties = propertiesClass.getConstructor().newInstance();
			entity.registerExtendedProperties(getIdentifier(propertiesClass), properties);
			return properties;
		} catch (Exception e) { throw new RuntimeException(e); }
	}
	
	public static <T extends IExtendedEntityProperties> T getOrCreateProperties(Entity entity, Class<T> propertiesClass) {
		T properties = getProperties(entity, propertiesClass);
		return ((properties != null) ? properties : createProperties(entity, propertiesClass));
	}
	
}
