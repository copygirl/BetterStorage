package net.mcft.copy.betterstorage.client.gui;

import java.util.ArrayList;

import net.mcft.copy.betterstorage.BetterStorage;
import net.mcft.copy.betterstorage.misc.Constants;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.fml.client.config.DummyConfigElement;
import net.minecraftforge.fml.client.config.GuiConfig;
import net.minecraftforge.fml.client.config.IConfigElement;

public class GuiBetterStorageConfig extends GuiConfig {

	private static ArrayList<IConfigElement> configElements;
	
	public GuiBetterStorageConfig(GuiScreen parentScreen) {
		super(parentScreen, configElements, Constants.modId, true, false, Constants.modName);
	}

	public static void initialize(Minecraft minecraftInstance) {
		configElements = new ArrayList<IConfigElement>();
		
		ArrayList<IConfigElement> itemCategory = new ArrayList<IConfigElement>();
		ArrayList<IConfigElement> blockCategory = new ArrayList<IConfigElement>();
		ArrayList<IConfigElement> enchantmentCategory = new ArrayList<IConfigElement>();
		
		itemCategory.addAll(BetterStorage.globalConfig.getSettings("item").values());
		blockCategory.addAll(BetterStorage.globalConfig.getSettings("tile").values());
		enchantmentCategory.addAll(BetterStorage.globalConfig.getSettings("enchantment").values());
		
		configElements.add(new DummyConfigElement.DummyCategoryElement("item", "config.betterstorage.category.item", itemCategory));
		configElements.add(new DummyConfigElement.DummyCategoryElement("tile", "config.betterstorage.category.tile", blockCategory));
		configElements.add(new DummyConfigElement.DummyCategoryElement("enchantment", "config.betterstorage.category.enchantment", enchantmentCategory));
		configElements.addAll(BetterStorage.globalConfig.getSettings("general").values());	
	}
	
}
