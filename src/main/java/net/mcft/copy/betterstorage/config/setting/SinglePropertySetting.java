package net.mcft.copy.betterstorage.config.setting;

import net.mcft.copy.betterstorage.config.Config;
import net.minecraftforge.common.Configuration;
import net.minecraftforge.common.Property;
import net.minecraftforge.common.Property.Type;

public abstract class SinglePropertySetting<T> extends Setting<T> {
	
	private Property property;
	
	public SinglePropertySetting(Config config, String fullName, T defaultValue) {
		super(config, fullName, defaultValue);
	}
	
	protected Property getProperty(Configuration config) {
		if (property == null)
			property = config.get(category, name, String.valueOf(defaultValue), comment, getPropertyType());
		return property;
	}
	
	protected abstract Type getPropertyType();
	
}
