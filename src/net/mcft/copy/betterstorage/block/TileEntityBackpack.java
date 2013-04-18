package net.mcft.copy.betterstorage.block;

import net.mcft.copy.betterstorage.BetterStorage;
import net.mcft.copy.betterstorage.Config;
import net.mcft.copy.betterstorage.utils.StackUtils;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class TileEntityBackpack extends TileEntityContainer {
	
	/** Stores the damage of the backpack as an item. */
	private int damage;
	/** Affects if items drop when the backpack is destroyed. */
	public boolean equipped = false;
	
	@Override
	public String getName() { return "container.backpack"; }
	
	// TileEntityContainer stuff

	@Override
	public int getRows() { return Config.backpackRows; }
	@Override
	public int getGuiId() { return getRows() - 1; }
	@Override
	protected int getGuiRows(int guiId) { return guiId + 1; }
	
	// Update entity
	
	@Override
	protected float getLidSpeed() { return 0.2F; }
	
	@Override
	public void updateEntity() {
		super.updateEntity();
		
		double x = xCoord + 0.5;
		double y = yCoord + 0.5;
		double z = zCoord + 0.5;
		
		String sound = Block.soundSnowFootstep.getStepSound();
		// Play sound when opening
		if (lidAngle > 0.0F && prevLidAngle <= 0.0F)
			worldObj.playSoundEffect(x, y, z, sound, 0.6F, 0.5F);
		// Play sound when closing
		if (lidAngle < 0.2F && prevLidAngle >= 0.2F)
			worldObj.playSoundEffect(x, y, z, sound, 0.4F, 0.3F);
	}
	
	// From and to ItemStack
	
	/** Creates an item from this backpack, to be dropped or equipped. */
	public ItemStack toItem() {
		ItemStack stack = new ItemStack(BetterStorage.backpack, 1, damage);
		if (hasCustomTitle()) StackUtils.set(stack, getCustomTitle(), "display", "CustomName");
		if (equipped) StackUtils.setStackContents(stack, contents);
		return stack;
	}
	
	/** Fills the data of this backpack from the ItemStack. */
	public void fromItem(ItemStack stack) {
		damage = stack.getItemDamage();
		setCustomTitle(StackUtils.get(stack, (String)null, "display", "CustomName"));
		ItemStack[] itemContents = StackUtils.getStackContents(stack, contents.length);
		for (int i = 0; i < contents.length; i++)
			contents[i] = itemContents[i];
	}
	
	// Reading from / writing to NBT
	
	@Override
	public void readFromNBT(NBTTagCompound compound) {
		super.readFromNBT(compound);
		damage = compound.getInteger("damage");
	}
	@Override
	public void writeToNBT(NBTTagCompound compound) {
		super.writeToNBT(compound);
		compound.setInteger("damage", damage);
	}
	
}
