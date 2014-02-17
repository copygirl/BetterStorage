package net.mcft.copy.betterstorage.config.setting;

import net.mcft.copy.betterstorage.config.Config;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.Configuration;
import net.minecraftforge.common.Property;

public class BooleanSetting extends SinglePropertySetting<Boolean> {
	
	public BooleanSetting(Config config, String fullName, Boolean defaultValue) {
		super(config, fullName, defaultValue);
	}
	public BooleanSetting(Config config, String fullName) {
		this(config, fullName, false);
	}
	
	@Override
	protected Property getProperty(Configuration config) {
		return config.get(category, name, defaultValue);
	}
	
	@Override
	public BooleanSetting setComment(String comment) {
		super.setComment(comment);
		return this;
	}
	@Override
	public BooleanSetting setSynced() {
		super.setSynced();
		return this;
	}
	
	@Override
	protected Boolean loadInternal(Configuration config) { return property.getBoolean(defaultValue); }
	@Override
	protected void saveInternal(Configuration config, Boolean value) {
		super.saveInternal(config, value); property.set(value); }
	
	@Override
	protected Boolean readInternal(NBTTagCompound compound) { return compound.getBoolean(fullName); }
	@Override
	protected void writeInternal(NBTTagCompound compound, Boolean value) { compound.setBoolean(fullName, value); }
	
}
