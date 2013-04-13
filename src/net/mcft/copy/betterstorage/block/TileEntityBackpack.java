package net.mcft.copy.betterstorage.block;

import net.mcft.copy.betterstorage.inventory.InventoryWrapper;
import net.mcft.copy.betterstorage.utils.NbtUtils;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.ForgeDirection;

public class TileEntityBackpack extends TileEntity {
	
	private ItemStack[] contents;
	private IInventory wrapper;
	
	public float lidAngle = 0;
	public float prevLidAngle = 0;
	
	public IInventory getWrapper() { return wrapper; }
	
	public TileEntityBackpack() {
		contents = new ItemStack[3 * 9];
		wrapper = new InventoryWrapper("container.backpack", contents, null);
	}
	
	// Reading from / writing to NBT
	
	@Override
	public void readFromNBT(NBTTagCompound compound) {
		super.readFromNBT(compound);
		NBTTagList items = compound.getTagList("Items");
		contents = NbtUtils.readItems(items, contents.length);
		wrapper = new InventoryWrapper("container.backpack", contents, null);
	}
	
	@Override
	public void writeToNBT(NBTTagCompound compound) {
		super.writeToNBT(compound);
		compound.setTag("Items", NbtUtils.writeItems(contents));
	}
	
}
