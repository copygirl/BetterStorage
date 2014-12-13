package net.mcft.copy.betterstorage.config.setting;

import java.util.Arrays;

import net.mcft.copy.betterstorage.config.Config;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property.Type;

import org.apache.commons.lang3.ArrayUtils;

import net.minecraftforge.fml.client.config.ConfigGuiType;

public class IntegerSetting extends SinglePropertySetting<Integer> {
	
	protected int minValue = Integer.MIN_VALUE;
	protected int maxValue = Integer.MAX_VALUE;
	protected int[] validValues = null;
	
	public IntegerSetting(Config config, String fullName, Integer defaultValue) {
		super(config, fullName, defaultValue, ConfigGuiType.INTEGER);
	}
	
	public IntegerSetting(Config config, String fullName, Integer defaultValue, String langKey) {
		super(config, fullName, defaultValue, ConfigGuiType.INTEGER, langKey);
	}
	
	public IntegerSetting(Config config, String fullName) {
		this(config, fullName, 0);
	}
	
	public IntegerSetting(Config config, String fullName, String langKey) {
		this(config, fullName, 0, langKey);
	}
	
	@Override
	public IntegerSetting setComment(String comment) {
		super.setComment(comment);
		return this;
	}
	@Override
	public IntegerSetting setSynced() {
		super.setSynced();
		return this;
	}
	
	/** Sets the valid range of values for this setting. */
	public IntegerSetting setValidRange(int min, int max) {
		minValue = min;
		maxValue = max;
		return this;
	}
	/** Sets the valid values for this setting. */
	public IntegerSetting setValidValues(int... values) {
		validValues = values;
		return this;
	}
	
	@Override
	public String validateInternal(Integer value) {
		if ((value < minValue) || (value > maxValue))
			return String.format("Value %s is not in valid range, %s to %s",
			                     value, minValue, maxValue);
		if ((validValues != null) && !ArrayUtils.contains(validValues, value))
			return String.format("Value %s is not valid, needs to be one of %s",
			                     value, Arrays.toString(validValues));
		return null;
	}
	
	@Override
	protected Type getPropertyType() { return Type.INTEGER; }
	
	@Override
	protected Integer loadInternal(Configuration config) { return getProperty(config).getInt(); }
	@Override
	protected void saveInternal(Configuration config, Integer value) { getProperty(config).set(value); }
	
	@Override
	protected Integer readInternal(NBTTagCompound compound) { return compound.getInteger(fullName); }
	@Override
	protected void writeInternal(NBTTagCompound compound, Integer value) { compound.setInteger(fullName, value); }
	
	@Override
	public Integer getMinValue() {
		return minValue;
	}
	
	@Override
	public Integer getMaxValue() {
		return maxValue;
	}	
	
}
