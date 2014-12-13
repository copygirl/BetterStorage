package net.mcft.copy.betterstorage.item.tile;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.mcft.copy.betterstorage.BetterStorage;
import net.mcft.copy.betterstorage.api.IContainerItem;
import net.mcft.copy.betterstorage.config.GlobalConfig;
import net.mcft.copy.betterstorage.item.IDyeableItem;
import net.mcft.copy.betterstorage.utils.LanguageUtils;
import net.mcft.copy.betterstorage.utils.StackUtils;
import net.minecraft.block.Block;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemCardboardBox extends ItemBlock implements IContainerItem, IDyeableItem {
	
	public ItemCardboardBox(Block block) {
		super(block);
		setMaxStackSize(1);
	}
	
	// Item stuff
	
	@Override
	public boolean showDurabilityBar(ItemStack stack) {
		int maxUses = getUses();
		return ((maxUses > 1) && (StackUtils.get(stack, maxUses, "uses") < maxUses));
	}
	
	@Override
	public double getDurabilityForDisplay(ItemStack stack) {
		int maxUses = getUses();
		return (1 - (float)StackUtils.get(stack, maxUses, "uses") / maxUses);
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public int getColorFromItemStack(ItemStack stack, int renderPass) {
		return StackUtils.get(stack, 0x705030, "display", "color");
	}
	
	@Override
	public boolean canDye(ItemStack stack) { return true; }
	
	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean advancedTooltips) {
		int maxUses = getUses();
		boolean hasItems = StackUtils.has(stack, "Items");
		
		if (!hasItems && BetterStorage.globalConfig.getBoolean(GlobalConfig.enableHelpTooltips))
			list.add(LanguageUtils.translateTooltip("cardboardBox.useHint" + ((maxUses > 0) ? ".reusable" : 0)));
		
		if (maxUses > 1)
			list.add(EnumChatFormatting.DARK_GRAY.toString() + EnumChatFormatting.ITALIC +
					LanguageUtils.translateTooltip("cardboardBox.uses",
							"%USES%", StackUtils.get(stack, maxUses, "uses").toString()));
		
		if (!hasItems) return;
		if (!BetterStorage.globalConfig.getBoolean(GlobalConfig.cardboardBoxShowContents)) {
			list.add(LanguageUtils.translateTooltip("cardboardBox.containsItems"));
			return;
		}
		// Show the contents in the cardboard box as tooltip.
		
		// Using 27 instead of getRows() here because
		// rows setting is not synced to the client.
		ItemStack[] contents = StackUtils.getStackContents(stack, 27);
		int limit = ((advancedTooltips || GuiScreen.isShiftKeyDown()) ? 6 : 3);
		
		List<DisplayNameStack> items = new ArrayList<DisplayNameStack>();
		outerLoop:
		for (int i = 0; i < contents.length; i++) {
			ItemStack contentStack = contents[i];
			if (contentStack == null) continue;
			for (DisplayNameStack itemsStack : items)
				if (itemsStack.matchAndAdd(contentStack))
					continue outerLoop;
			items.add(new DisplayNameStack(contentStack));
		}
		
		Collections.sort(items);
		for (int i = 0; (i < items.size()) && (i < limit); i++)
			list.add(items.get(i).toString());
		
		if (items.size() <= limit) return;
		
		int count = 0;
		for (int i = limit; i < items.size(); i++)
			count += items.get(i).stackSize;
		list.add(count + " more item" + ((count > 1) ? "s" : "") + " ...");
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
	
	/** Returns how many times cardboard boxes can be reused. */
	public static int getUses() {
		return BetterStorage.globalConfig.getInteger(GlobalConfig.cardboardBoxUses);
	}
	
	
	private static class DisplayNameStack implements Comparable<DisplayNameStack> {
		public final String name;
		public int stackSize;
		public DisplayNameStack(ItemStack stack) {
			name = stack.getItem().getItemStackDisplayName(stack);
			stackSize = stack.stackSize;
		}
		public boolean matchAndAdd(ItemStack stack) {
			if (name.equals(stack.getItem().getItemStackDisplayName(stack))) {
				stackSize += stack.stackSize;
				return true;
			} else return false;
		}
		@Override
		public String toString() {
			return (stackSize + "x " + name);
		}
		@Override
		public int compareTo(DisplayNameStack other) {
			return (other.stackSize - stackSize);
		}
	}
	
}
