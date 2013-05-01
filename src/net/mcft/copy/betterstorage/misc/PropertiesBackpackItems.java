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
		compound.setTag("BackpackItems", NbtUtils.writeItems(contents));
	}
	
	@Override
	public void loadNBTData(NBTTagCompound compound) {
		if (!compound.hasKey("BackpackItems")) return;
		contents = new ItemStack[compound.getInteger("BackpackItemsCount")];
		NbtUtils.readItems(contents, compound.getTagList("BackpackItems"));
	}
	
}
