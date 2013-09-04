package net.mcft.copy.betterstorage.utils;

import net.minecraft.entity.Entity;
import net.minecraftforge.common.IExtendedEntityProperties;

public final class EntityUtils {
	
	private EntityUtils() {  }
	
	public static <T extends IExtendedEntityProperties> T getProperties(Entity entity, Class<T> propertiesClass, String identifier) {
		IExtendedEntityProperties properties = entity.getExtendedProperties(identifier);
		return (propertiesClass.isInstance(properties) ? (T)properties : null);
	}
	
	public static <T extends IExtendedEntityProperties> T createProperties(Entity entity, Class<T> propertiesClass, String identifier) {
		try {
			T properties = propertiesClass.getConstructor().newInstance();
			entity.registerExtendedProperties(identifier, properties);
			return properties;
		} catch (Exception e) { throw new RuntimeException(e); }
	}
	
	public static <T extends IExtendedEntityProperties> T getOrCreateProperties(Entity entity, Class<T> propertiesClass, String identifier) {
		T properties = getProperties(entity, propertiesClass, identifier);
		return ((properties != null) ? properties : createProperties(entity, propertiesClass, identifier));
	}
	
}
