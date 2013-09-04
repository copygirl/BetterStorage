package net.mcft.copy.betterstorage.misc;

import net.mcft.copy.betterstorage.utils.NbtUtils;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.common.IExtendedEntityProperties;

public class PropertiesBackpack implements IExtendedEntityProperties {
	
	public static final String identifier = Constants.modId + ".backpack";
	
	public ItemStack[] contents = null;
	
	/** If the backpack has been initialized for the entity yet. */
	public boolean initialized = false;
	/** When certain mobs spawn, they have a chance to
	 *  spawn with a backpack that contains some items. */
	public boolean spawnsWithBackpack = false;
	
	/** How many players are using the backpack (server only). */
	public int playersUsing = 0;
	/** If the backpack contains any items (wearer only),
	 *  because the client doesn't have the contents. */
	public boolean hasItems = false;
	
	public float lidAngle = 0;
	public float prevLidAngle = 0;
	
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
