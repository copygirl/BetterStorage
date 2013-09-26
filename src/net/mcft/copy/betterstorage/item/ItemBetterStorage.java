package net.mcft.copy.betterstorage.item;

import java.util.Locale;

import net.mcft.copy.betterstorage.BetterStorage;
import net.mcft.copy.betterstorage.misc.Constants;
import net.mcft.copy.betterstorage.utils.StackUtils;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public abstract class ItemBetterStorage extends Item {
	
	public static final String TAG_COLOR = "color";
	public static final String TAG_FULL_COLOR = "fullColor";
	public static final String TAG_KEYLOCK_ID = "id";
	
	public ItemBetterStorage(int id) {
		
		// Adjusts the ID so the item's config ID
		// represents the actual ID of the item.
		super(id - 256);
		
		setMaxStackSize(1);
		setCreativeTab(BetterStorage.creativeTab);
		
		String name = getClass().getSimpleName();                                    // ItemMyItem
		name = name.substring(4, 5).toLowerCase(Locale.ENGLISH) + name.substring(5); // 'm' + "yItem"
		setUnlocalizedName(Constants.modId + "." + name);                            // modname.myItem
		
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IconRegister iconRegister) {
		String name = getUnlocalizedName();
		name = name.substring(name.lastIndexOf('.') + 1);
		itemIcon = iconRegister.registerIcon(Constants.modId + ":" + name);
	}
	
	// NBT helper functions
	// Only used by keys and locks currently.
	
	public static int getColor(ItemStack stack) {
		String type = StackUtils.getType(stack, TAG_FULL_COLOR);
		if (type == "BYTE") return -1;
		else return StackUtils.get(stack, -1, TAG_COLOR);
	}
	public static void setColor(ItemStack stack, int color) {
		String type = StackUtils.getType(stack, TAG_FULL_COLOR);
		if (type == "BYTE") {
			int fullColor = StackUtils.get(stack, -1, TAG_COLOR);
			StackUtils.set(stack, fullColor, TAG_FULL_COLOR);
		}
		StackUtils.set(stack, color, TAG_COLOR);
	}
	
	public static int getFullColor(ItemStack stack) {
		String type = StackUtils.getType(stack, TAG_FULL_COLOR);
		if (type == "BYTE") // Backwards compatibility.
			return StackUtils.get(stack, -1, TAG_COLOR);
		else if (type == "INT")
			return StackUtils.get(stack, -1, TAG_FULL_COLOR);
		else return -1;
	}
	public static void setFullColor(ItemStack stack, int fullColor) {
		StackUtils.set(stack, fullColor, TAG_FULL_COLOR);
	}
	
	public static int getID(ItemStack stack) {
		return StackUtils.get(stack, 0, TAG_KEYLOCK_ID);
	}
	public static void setID(ItemStack stack, int id) {
		StackUtils.set(stack, id, TAG_KEYLOCK_ID);
	}
	
}
