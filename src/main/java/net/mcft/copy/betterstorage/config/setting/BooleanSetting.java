package net.mcft.copy.betterstorage.config.setting;

import net.mcft.copy.betterstorage.config.Config;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property.Type;
import net.minecraftforge.fml.client.config.ConfigGuiType;

public class BooleanSetting extends SinglePropertySetting<Boolean> {
	
	public BooleanSetting(Config config, String fullName, Boolean defaultValue) {
		super(config, fullName, defaultValue, ConfigGuiType.BOOLEAN);
	}
	
	public BooleanSetting(Config config, String fullName, Boolean defaultValue, String langKey) {
		super(config, fullName, defaultValue, ConfigGuiType.BOOLEAN, langKey);
	}
	
	public BooleanSetting(Config config, String fullName) {
		this(config, fullName, false);
	}
	
	public BooleanSetting(Config config, String fullName, String langKey) {
		this(config, fullName, false, langKey);
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
	protected Type getPropertyType() { return Type.BOOLEAN; }
	
	@Override
	protected Boolean loadInternal(Configuration config) { return getProperty(config).getBoolean(defaultValue); }
	@Override
	protected void saveInternal(Configuration config, Boolean value) { getProperty(config).set(value); }
	
	@Override
	protected Boolean readInternal(NBTTagCompound compound) { return compound.getBoolean(fullName); }
	@Override
	protected void writeInternal(NBTTagCompound compound, Boolean value) { compound.setBoolean(fullName, value); }
	
}
