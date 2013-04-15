package net.mcft.copy.betterstorage.utils;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.common.IExtendedEntityProperties;

public class PlayerUtils {
	
	private static String getIdentifier(Class<? extends IExtendedEntityProperties> propertiesClass) {
		try { return (String)propertiesClass.getField("identifier").get(null); }
		catch (Exception e) { e.printStackTrace(); return ""; }
	}
	
	public static <T extends IExtendedEntityProperties> T getProperties(EntityPlayer player, Class<T> propertiesClass) {
		String identifier = getIdentifier(propertiesClass);
		IExtendedEntityProperties properties = player.getExtendedProperties(identifier);
		return (propertiesClass.isInstance(properties) ? (T)properties : null);
	}
	
	public static <T extends IExtendedEntityProperties> T getOrCreateProperties(EntityPlayer player, Class<T> propertiesClass) {
		String identifier = getIdentifier(propertiesClass);
		T properties = getProperties(player, propertiesClass);
		if (properties == null) {
			try { properties = propertiesClass.getConstructor().newInstance(); }
			catch (Exception e) { e.printStackTrace(); return properties; }
			player.registerExtendedProperties(identifier, properties);
		}
		return properties;
	}
	
}
