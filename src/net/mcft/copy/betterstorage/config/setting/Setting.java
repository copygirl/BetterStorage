package net.mcft.copy.betterstorage.config.setting;

import net.mcft.copy.betterstorage.BetterStorage;
import net.mcft.copy.betterstorage.config.Config;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.Configuration;

public abstract class Setting<T> {
	
	/** The config object for this setting. */
	public final Config config;
	/** The default value for this setting. */
	public final T defaultValue;
	
	/** The name including the category, for example "block.storage.flint". */
	public final String fullName;
	/** The name not including the category, for example "flint". */
	public final String name;
	/** The category, for example "block.storage". */
	public final String category;
	
	protected T value;
	protected T syncedValue;
	protected String comment = null;
	private boolean synced = false;
	
	public Setting(Config config, String fullName, T defaultValue) {
		this.config = config;
		this.defaultValue = defaultValue;
		
		this.fullName = fullName;
		int dotIndex = fullName.lastIndexOf('.');
		if (dotIndex < 1) throw new IllegalArgumentException("fullName doesn't contain a category.");
		name = fullName.substring(dotIndex + 1);
		category = fullName.substring(0, dotIndex);
		
		value = defaultValue;
		syncedValue = value;
		config.add(this);
	}
	
	/** Sets the comment for this setting. */
	public Setting<T> setComment(String comment) {
		this.comment = comment;
		return this;
	}
	/** When set, sends the setting from the
	 *  server to clients when they connect. */
	public Setting<T> setSynced() {
		if (!synced) {
			synced = true;
			config.syncSetting(this);
		}
		return this;
	}
	
	/** Gets the value of the setting. */ 
	public T getValue() { return syncedValue; }
	
	/** Validates the setting and returns a warning
	 *  string, or null if validation was successful. */
	protected String validateInternal() { return null; }
	
	/** Validates the setting. If value is invalid, prints a
	 *  warning to console and uses the default value instead. */
	public void validate() {
		String warning = validateInternal();
		if (warning != null) {
			value = defaultValue;
			BetterStorage.log.warning(String.format("Config setting %s is invalid: %s. Using default value: %s.",
			                                        fullName, warning, defaultValue));
		}
	}
	
	/** Loads the setting's value from the config. */
	public void load(Configuration config) { value = loadInternal(config); }
	/** Saves the setting's value to the config. */
	public void save(Configuration config) { saveInternal(config, value); }
	
	/** Reads the setting's synced value from the compound tag. */
	public void read(NBTTagCompound compound) { syncedValue = readInternal(compound); }
	/** Writes the setting's value to the compound tag. */
	public void write(NBTTagCompound compound) { writeInternal(compound, value); }
	
	protected abstract T loadInternal(Configuration config);
	protected abstract void saveInternal(Configuration config, T value);
	
	protected abstract T readInternal(NBTTagCompound compound);
	protected abstract void writeInternal(NBTTagCompound compound, T value);
	
}
