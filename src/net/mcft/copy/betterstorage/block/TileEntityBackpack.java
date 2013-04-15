package net.mcft.copy.betterstorage.block;

import net.mcft.copy.betterstorage.inventory.InventoryWrapper;
import net.mcft.copy.betterstorage.utils.NbtUtils;
import net.mcft.copy.betterstorage.utils.WorldUtils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;

public class TileEntityBackpack extends TileEntity {
	
	private ItemStack[] contents;
	private IInventory wrapper;
	
	private int numUsingPlayers = 0;
	private int ticksSinceSync;
	
	public int damage;
	public boolean equipped = false;
	
	public float lidAngle = 0;
	public float prevLidAngle = 0;
	
	private IInventory openAndClose = new IInventory() {
		
		@Override public void setInventorySlotContents(int i, ItemStack istack) {  }
		@Override public void onInventoryChanged() {  }
		@Override public boolean isStackValidForSlot(int i, ItemStack stack) { return true; }
		@Override public boolean isInvNameLocalized() { return false; }
		@Override public ItemStack getStackInSlotOnClosing(int i) { return null; }
		@Override public ItemStack getStackInSlot(int i) { return null; }
		@Override public int getSizeInventory() { return 0; }
		@Override public int getInventoryStackLimit() { return 0; }
		@Override public String getInvName() { return "container.backpack"; }
		@Override public ItemStack decrStackSize(int i, int j) { return null; }

		@Override
		public boolean isUseableByPlayer(EntityPlayer player) {
			return WorldUtils.isTileEntityUsableByPlayer(TileEntityBackpack.this, player);
		}
		@Override
		public void openChest() {
			numUsingPlayers++;
			worldObj.addBlockEvent(xCoord, yCoord, zCoord, getBlockType().blockID, 1, numUsingPlayers);
		}
		@Override
		public void closeChest() {
			numUsingPlayers--;
			worldObj.addBlockEvent(xCoord, yCoord, zCoord, getBlockType().blockID, 1, numUsingPlayers);
		}
		
	};
	
	public ItemStack[] getContents() { return contents; }
	public IInventory getWrapper() { return wrapper; }
	
	public TileEntityBackpack() {
		contents = new ItemStack[3 * 9];
		wrapper = new InventoryWrapper(contents, openAndClose);
	}
	
	@Override
	public boolean receiveClientEvent(int eventId, int val) {
		numUsingPlayers = val;
		return true;
	}
	
	@Override
	public void updateEntity() {
		numUsingPlayers = WorldUtils.syncPlayersUsing(this, ++ticksSinceSync, numUsingPlayers, wrapper);
		
		prevLidAngle = lidAngle;
		float lidSpeed = 0.1F;
		double x = xCoord + 0.5;
		double y = yCoord + 0.5;
		double z = zCoord + 0.5;
		
		if ((numUsingPlayers == 0 && lidAngle > 0.0F) ||
		    (numUsingPlayers >  0 && lidAngle < 1.0F)) {
			if (numUsingPlayers > 0) lidAngle = Math.min(1.0F, lidAngle + lidSpeed);
			else lidAngle = Math.max(0.0F, lidAngle - lidSpeed);
		}
	}
	
	// Reading from / writing to NBT
	
	@Override
	public void readFromNBT(NBTTagCompound compound) {
		super.readFromNBT(compound);
		damage = compound.getInteger("damage");
		NBTTagList items = compound.getTagList("Items");
		contents = NbtUtils.readItems(items, contents.length);
		wrapper = new InventoryWrapper("container.backpack", contents, null);
	}
	
	@Override
	public void writeToNBT(NBTTagCompound compound) {
		super.writeToNBT(compound);
		compound.setInteger("damage", damage);
		compound.setTag("Items", NbtUtils.writeItems(contents));
	}
	
	// Dropping stuff
	
	public void dropContents() {
		for (ItemStack stack : contents)
			WorldUtils.dropStackFromBlock(worldObj, xCoord, yCoord, zCoord, stack);
	}
	
}
