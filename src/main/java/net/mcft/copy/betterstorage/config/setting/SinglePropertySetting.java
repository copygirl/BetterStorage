package net.mcft.copy.betterstorage.config.setting;

import net.mcft.copy.betterstorage.config.Config;
import net.minecraftforge.common.Configuration;
import net.minecraftforge.common.Property;

public abstract class SinglePropertySetting<T> extends Setting<T> {
	
	protected final Property property;
	
	public SinglePropertySetting(Config config, String fullName, T defaultValue) {
		super(config, fullName, defaultValue);
		property = getProperty(config.forgeConfig);
	}
	
	@Override
	public SinglePropertySetting<T> setComment(String comment) {
		property.comment = comment;
		super.setComment(comment);
		return this;
	}
	
	protected abstract Property getProperty(Configuration config);
	
}
