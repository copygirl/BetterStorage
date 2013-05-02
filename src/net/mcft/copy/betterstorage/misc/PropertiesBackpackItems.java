package net.mcft.copy.betterstorage.misc;

import net.mcft.copy.betterstorage.utils.NbtUtils;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.common.IExtendedEntityProperties;

public class PropertiesBackpackItems implements IExtendedEntityProperties {
	
	public static final String identifier = "BetterStorage.BackpackItems";
	
	public ItemStack[] contents = null;
	
	@Override
	public void init(Entity entity, World world) {  }
	
	@Override
	public void saveNBTData(NBTTagCompound compound) {
		if (contents == null) return;
		NBTTagCompound backpack = new NBTTagCompound();
		backpack.setInteger("count", contents.length);
		backpack.setTag("Items", NbtUtils.writeItems(contents));
		compound.setTag("Backpack", backpack);
	}
	
	@Override
	public void loadNBTData(NBTTagCompound compound) {
		if (!compound.hasKey("Backpack")) return;
		NBTTagCompound backpack = compound.getCompoundTag("Backpack");
		contents = new ItemStack[backpack.getInteger("count")];
		NbtUtils.readItems(contents, backpack.getTagList("Items"));
	}
	
}
