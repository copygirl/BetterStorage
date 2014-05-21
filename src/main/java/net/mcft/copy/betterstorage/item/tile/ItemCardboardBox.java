package net.mcft.copy.betterstorage.item.tile;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import net.mcft.copy.betterstorage.BetterStorage;
import net.mcft.copy.betterstorage.api.IContainerItem;
import net.mcft.copy.betterstorage.config.GlobalConfig;
import net.mcft.copy.betterstorage.utils.LanguageUtils;
import net.mcft.copy.betterstorage.utils.StackUtils;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemCardboardBox extends ItemBlock implements IContainerItem {
	
	public ItemCardboardBox(Block block) {
		super(block);
		setMaxStackSize(1);
	}
	
	// Item stuff
	
	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean advancedTooltips) {
		
		// Show the contents in the cardboard box as tooltip.
		if (StackUtils.has(stack, "Items")) {
			
			// Using 27 instead of getRows() here because
			// rows setting is not synced to the client.
			ItemStack[] contents = StackUtils.getStackContents(stack, 27);
			int limit = (advancedTooltips ? 9 : 3);
			
			List<ItemStack> items = StackUtils.stackItems(contents);
			Collections.sort(items, new Comparator<ItemStack>() {
				@Override public int compare(ItemStack a, ItemStack b) {
					return (b.stackSize - a.stackSize); }
			});
			
			for (int i = 0; (i < items.size()) && (i < limit); i++) {
				ItemStack item = items.get(i);
				list.add(item.stackSize + "x " + item.getDisplayName());
			}
			
			if (items.size() <= limit) return;
			int count = 0;
			for (int i = 3; i < items.size(); i++)
				count += items.get(i).stackSize;
			list.add(count + " more item" + ((count > 1) ? "s" : "") + " ...");
			
		} else if (BetterStorage.globalConfig.getBoolean(GlobalConfig.enableHelpTooltips))
			list.add(LanguageUtils.translateTooltip(
					"cardboardBox.useHint" + (isReusable() ? ".reusable" : "")));
		
	}
	
	// IContainerItem implementation
	
	@Override
	public ItemStack[] getContainerItemContents(ItemStack container) {
		if (StackUtils.has(container, "Items"))
			return StackUtils.getStackContents(container, getRows());
		else return null;
	}
	
	@Override
	public boolean canBeStoredInContainerItem(ItemStack item) {
		return !StackUtils.has(item, "Items");
	}
	
	// Helper functions
	
	/** Returns the amount of rows in a cardboard box. */
	public static int getRows() {
		return BetterStorage.globalConfig.getInteger(GlobalConfig.cardboardBoxRows);
	}
	
	/** Returns if cardboard boxes are reusable. */
	public static boolean isReusable() {
		return BetterStorage.globalConfig.getBoolean(GlobalConfig.cardboardBoxReusable);
	}
	
}
