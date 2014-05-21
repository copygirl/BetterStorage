package net.mcft.copy.betterstorage.item.tile;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import net.mcft.copy.betterstorage.BetterStorage;
import net.mcft.copy.betterstorage.api.IContainerItem;
import net.mcft.copy.betterstorage.config.GlobalConfig;
import net.mcft.copy.betterstorage.utils.LanguageUtils;
import net.mcft.copy.betterstorage.utils.StackUtils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemCardboardBox extends ItemBlock implements IContainerItem {
	
	public ItemCardboardBox(int id) {
		super(id);
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
			
			Map<String, Integer> map = new HashMap<String, Integer>();
			for (ItemStack s : contents) {
				if (s == null) continue;
				String name = s.getDisplayName();
				Integer amount = map.get(name);
				map.put(name, ((amount != null) ? amount : 0) + s.stackSize);
			}
			List<Entry<String, Integer>> items =
					new ArrayList<Entry<String, Integer>>(map.entrySet());
			Collections.sort(items, new Comparator<Entry<String, Integer>>() {
				@Override public int compare(Entry<String, Integer> a, Entry<String, Integer> b) {
					return (b.getValue() - a.getValue()); }
			});
			
			int max = (advancedTooltips ? 8 : 3);
			for (int i = 0; (i < items.size()) && (i < max); i++) {
				Entry<String, Integer> entry = items.get(i);
				list.add(entry.getValue() + "x " + entry.getKey());
			}
			
			int more = 0;
			for (int i = max; i < items.size(); i++)
				more += items.get(i).getValue();
			if (more > 0)
				list.add(EnumChatFormatting.DARK_GRAY.toString() + EnumChatFormatting.ITALIC +
						LanguageUtils.translateTooltip(
								"cardboardBox.more." + ((more == 1) ? "1" : "x"),
								"%X%", Integer.toString(more)));
			
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
