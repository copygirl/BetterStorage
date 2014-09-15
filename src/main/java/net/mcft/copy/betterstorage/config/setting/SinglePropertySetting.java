package net.mcft.copy.betterstorage.config.setting;

import net.mcft.copy.betterstorage.config.Config;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.common.config.Property.Type;
import cpw.mods.fml.client.config.ConfigGuiType;

public abstract class SinglePropertySetting<T> extends Setting<T> {
	
	private Property property;
	
	public SinglePropertySetting(Config config, String fullName, T defaultValue, ConfigGuiType type, String langKey) {
		super(config, fullName, defaultValue, type, langKey);
	}
	
	public SinglePropertySetting(Config config, String fullName, T defaultValue, ConfigGuiType type) {
		super(config, fullName, defaultValue, type);
	}
	
	protected Property getProperty(Configuration config) {
		if (property == null)
			property = config.get(category, name, String.valueOf(defaultValue), comment, getPropertyType());
		return property;
	}
	
	protected abstract Type getPropertyType();
	
	@Override
	public void load(Configuration config) { setValue(loadInternal(config)); }
	@Override	
	public void save(Configuration config) { saveInternal(config, getInternalValue()); }
	
	@Override
	public void read(NBTTagCompound compound) { setSyncedValue(readInternal(compound)); }
	@Override
	public void write(NBTTagCompound compound) { writeInternal(compound, getInternalValue()); }
	
	protected abstract T loadInternal(Configuration config);
	protected abstract void saveInternal(Configuration config, T value);
	
	protected abstract T readInternal(NBTTagCompound compound);
	protected abstract void writeInternal(NBTTagCompound compound, T value);
}
