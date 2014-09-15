package net.mcft.copy.betterstorage.config.setting;

import java.util.regex.Pattern;

import net.mcft.copy.betterstorage.config.Config;
import cpw.mods.fml.client.config.ConfigGuiType;
import cpw.mods.fml.client.config.GuiEditArrayEntries.IArrayEntry;

public abstract class ArrayPropertySetting<T> extends Setting<T> {
	
	public ArrayPropertySetting(Config config, String fullName, T[] defaultValue, ConfigGuiType type, 
			boolean isListFixedLength, int maxListLength, Pattern validStringPattern) {
		this(config, fullName, defaultValue, "config.betterstorage." + fullName, type, isListFixedLength, maxListLength, validStringPattern);
	}
	
	public ArrayPropertySetting(Config config, String fullName, T[] defaultValues, String langKey, ConfigGuiType type, 
			boolean isListFixedLength, int maxListLength, Pattern validStringPattern) {
		super(config, fullName, null, type, langKey);
		
		this.defaultValues = (T[]) defaultValues;
		this.values = (T[]) defaultValues;
		this.isListFixedLength = isListFixedLength;
		this.maxListLength = maxListLength;
		this.validStringPattern = validStringPattern;
		
		isList = true;
	}
	
	public void setCustomEditListEntryClass(Class<? extends IArrayEntry> clazz)
    {
        this.arrayEntryClass = clazz;
    }
}
