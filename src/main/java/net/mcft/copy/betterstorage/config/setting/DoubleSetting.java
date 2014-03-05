package net.mcft.copy.betterstorage.config.setting;

import net.mcft.copy.betterstorage.config.Config;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.Configuration;
import net.minecraftforge.common.Property.Type;

public class DoubleSetting extends SinglePropertySetting<Double> {
	
	protected double minValue = Double.MIN_VALUE;
	protected double maxValue = Double.MAX_VALUE;
	
	public DoubleSetting(Config config, String fullName, Double defaultValue) {
		super(config, fullName, defaultValue);
	}
	public DoubleSetting(Config config, String fullName) {
		this(config, fullName, 0.0);
	}
	
	@Override
	public DoubleSetting setComment(String comment) {
		super.setComment(comment);
		return this;
	}
	@Override
	public DoubleSetting setSynced() {
		super.setSynced();
		return this;
	}
	
	/** Sets the valid range of values for this setting. */
	public DoubleSetting setValidRange(double min, double max) {
		minValue = min;
		maxValue = max;
		return this;
	}
	
	@Override
	public String validateInternal(Double value) {
		if ((value < minValue) || (value > maxValue))
			return String.format("Value %s is not in valid range, %s to %s",
			                     value, minValue, maxValue);
		return null;
	}
	
	@Override
	protected Type getPropertyType() { return Type.DOUBLE; }
	
	@Override
	protected Double loadInternal(Configuration config) { return getProperty(config).getDouble(defaultValue); }
	@Override
	protected void saveInternal(Configuration config, Double value) { getProperty(config).set(value); }
	
	@Override
	protected Double readInternal(NBTTagCompound compound) { return compound.getDouble(fullName); }
	@Override
	protected void writeInternal(NBTTagCompound compound, Double value) { compound.setDouble(fullName, value); }
	
}
