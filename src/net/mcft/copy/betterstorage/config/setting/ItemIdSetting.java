package net.mcft.copy.betterstorage.config.setting;

import net.mcft.copy.betterstorage.config.Config;
import net.minecraftforge.common.Configuration;
import net.minecraftforge.common.Property;

public class ItemIdSetting extends IntegerSetting {
	
	public ItemIdSetting(Config config, String fullName, Integer defaultId) {
		super(config, fullName, defaultId);
	}
	
	@Override
	protected Property getProperty(Configuration config) {
		return config.getItem(category, name, defaultValue, comment); }
	
}
