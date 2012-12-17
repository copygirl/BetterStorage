package net.mcft.copy.betterstorage.blocks;

import java.util.Random;

import net.mcft.copy.betterstorage.BetterStorage;
import net.minecraft.src.Block;
import net.minecraft.src.EntityItem;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.IInventory;
import net.minecraft.src.ItemStack;
import net.minecraft.src.NBTTagCompound;
import net.minecraft.src.NBTTagList;
import net.minecraft.src.TileEntity;

public class TileEntityReinforcedChest extends TileEntity implements IInventory {
	
	// Code from this class is mainly stolen from the TileEntityChest class.
	// A little prettified here and there, but basically the same.
	
	public boolean adjacentChestChecked = false;
	public TileEntityReinforcedChest adjacentChestZNeg;
	public TileEntityReinforcedChest adjacentChestZPos;
	public TileEntityReinforcedChest adjacentChestXNeg;
	public TileEntityReinforcedChest adjacentChestXPos;
	
	public float lidAngle = 0.0f;
	public float prevLidAngle = 0.0f;
	public int numUsingPlayers = 0;
	
	public ItemStack[] contents;
	public int id;
	
	public TileEntityReinforcedChest() { this(null); }
	public TileEntityReinforcedChest(Block block) {
		id = ((block != null) ? block.blockID : -1);
		contents = new ItemStack[getColumns() * getRows()];
	}
	
	@Override
	public int getSizeInventory() { return contents.length; }
	
	public int getColumns() { return 13; }
	public int getRows() { return 3; }
	
	@Override
	public ItemStack getStackInSlot(int slot) { return contents[slot]; }
	
	@Override
	public ItemStack decrStackSize(int slot, int amount) {
		ItemStack stack = getStackInSlot(slot);
		if (stack == null) return null;
		ItemStack returnStack;
		if (stack.stackSize <= amount) {
			returnStack = stack;
			contents[slot] = null;
		} else returnStack = stack.splitStack(amount);
		onInventoryChanged();
		return returnStack;
	}
	
	@Override
	public ItemStack getStackInSlotOnClosing(int slot) {
		ItemStack stack = contents[slot];
		contents[slot] = null;
		return stack;
	}
	
	@Override
	public void setInventorySlotContents(int slot, ItemStack stack) {
		contents[slot] = stack;
		if (stack != null && stack.stackSize > getInventoryStackLimit())
			stack.stackSize = getInventoryStackLimit();
		onInventoryChanged();
	}
	
	@Override
	public String getInvName() { return "container.reinforcedChest"; }
	
	@Override
	public void readFromNBT(NBTTagCompound compound) {
		super.readFromNBT(compound);
		NBTTagList items = compound.getTagList("Items");
		contents = new ItemStack[getSizeInventory()];
		for (int i = 0; i < items.tagCount(); i++) {
			NBTTagCompound item = (NBTTagCompound)items.tagAt(i);
			int slot = item.getByte("Slot") & 255;
			if (slot >= 0 && slot < contents.length)
				contents[slot] = ItemStack.loadItemStackFromNBT(item);
		}
	}
	
	@Override
	public void writeToNBT(NBTTagCompound compound) {
		super.writeToNBT(compound);
		NBTTagList list = new NBTTagList();
		for (int i = 0; i < contents.length; i++) {
			if (contents[i] == null) continue;
			NBTTagCompound item = new NBTTagCompound();
			item.setByte("Slot", (byte)i);
			contents[i].writeToNBT(item);
			list.appendTag(item);
		}
		compound.setTag("Items", list);
	}
	
	@Override
	public int getInventoryStackLimit() { return 64; }
	
	@Override
	public boolean isUseableByPlayer(EntityPlayer player) {
		return (worldObj.getBlockTileEntity(xCoord, yCoord, zCoord) == this &&
				player.getDistanceSq(xCoord + 0.5D, yCoord + 0.5D, zCoord + 0.5D) <= 64.0D);
	}
	
	@Override
	public void updateContainingBlockInfo() {
		super.updateContainingBlockInfo();
		adjacentChestChecked = false;
	}
	
	public void checkForAdjacentChests() {
		if (adjacentChestChecked) return;
		
		adjacentChestChecked = true;
		adjacentChestZNeg = null;
		adjacentChestXPos = null;
		adjacentChestXNeg = null;
		adjacentChestZPos = null;
		
		if (worldObj.getBlockId(xCoord, yCoord, zCoord - 1) == id)
			adjacentChestZNeg = (TileEntityReinforcedChest)worldObj.getBlockTileEntity(xCoord, yCoord, zCoord - 1);
		if (worldObj.getBlockId(xCoord, yCoord, zCoord + 1) == id)
			adjacentChestZPos = (TileEntityReinforcedChest)worldObj.getBlockTileEntity(xCoord, yCoord, zCoord + 1);
		if (worldObj.getBlockId(xCoord - 1, yCoord, zCoord) == id)
			adjacentChestXNeg = (TileEntityReinforcedChest)worldObj.getBlockTileEntity(xCoord - 1, yCoord, zCoord);
		if (worldObj.getBlockId(xCoord + 1, yCoord, zCoord) == id)
			adjacentChestXPos = (TileEntityReinforcedChest)worldObj.getBlockTileEntity(xCoord + 1, yCoord, zCoord);
		if (adjacentChestZNeg != null)
			adjacentChestZNeg.updateContainingBlockInfo();
		if (adjacentChestZPos != null)
			adjacentChestZPos.updateContainingBlockInfo();
		if (adjacentChestXNeg != null)
			adjacentChestXNeg.updateContainingBlockInfo();
		if (adjacentChestXPos != null)
			adjacentChestXPos.updateContainingBlockInfo();
	}
	
	int i = 0;
	@Override
	public void updateEntity() {
		if (id == -1) getBlockType();
		checkForAdjacentChests();
		prevLidAngle = lidAngle;
		float lidSpeed = 0.1F;
		
		// Play sound when opening chest.
		if (numUsingPlayers > 0 && lidAngle == 0.0F && 
			adjacentChestZNeg == null && adjacentChestXNeg == null) {
			double x = xCoord + 0.5D;
			double z = zCoord + 0.5D;
			if (adjacentChestZPos != null) z += 0.5D;
			if (adjacentChestXPos != null) x += 0.5D;
			worldObj.playSoundEffect(x, yCoord + 0.5D, z, "random.chestopen",
			                         0.5F, worldObj.rand.nextFloat() * 0.1F + 0.9F);
		}
		
		if ((numUsingPlayers == 0 && lidAngle > 0.0F) ||
			(numUsingPlayers > 0 && lidAngle < 1.0F)) {
			
			lidAngle = Math.max(0, Math.min(1, lidAngle + ((numUsingPlayers > 0) ? lidSpeed : -lidSpeed)));
			
			// Play sound when closing chest.
			if (lidAngle < 0.5F && prevLidAngle >= 0.5F && 
				adjacentChestZNeg == null && adjacentChestXNeg == null) {
				double x = xCoord + 0.5D;
				double z = zCoord + 0.5D;
				if (adjacentChestZPos != null) z += 0.5D;
				if (adjacentChestXPos != null) x += 0.5D;
				worldObj.playSoundEffect(x, yCoord + 0.5D, z, "random.chestclosed",
				                         0.5F, worldObj.rand.nextFloat() * 0.1F + 0.9F);
			}
		}
	}
	
	@Override
	public void receiveClientEvent(int par1, int par2) {
		if (par1 == 1) numUsingPlayers = par2;
	}
	
	@Override
	public void openChest() {
		numUsingPlayers++;
		worldObj.addBlockEvent(xCoord, yCoord, zCoord, id, 1, numUsingPlayers);
	}
	
	@Override
	public void closeChest() {
		numUsingPlayers--;
		worldObj.addBlockEvent(xCoord, yCoord, zCoord, id, 1, numUsingPlayers);
	}
	
	@Override
	public void invalidate() {
		updateContainingBlockInfo();
		checkForAdjacentChests();
		super.invalidate();
	}

	public void dropContents() {
		Random random = BetterStorage.random;
		for (int slot = 0; slot < contents.length; slot++) {
			ItemStack stack = contents[slot];
			if (stack == null) continue;
			float x = random.nextFloat() * 0.8F + 0.1F;
			float y = random.nextFloat() * 0.8F + 0.1F;
			float z = random.nextFloat() * 0.8F + 0.1F;
			EntityItem item = new EntityItem(worldObj, xCoord + x, yCoord + y, zCoord + z, stack);
			item.motionX = random.nextGaussian() * 0.05F;
			item.motionY = random.nextGaussian() * 0.05F + 0.2F;
			item.motionZ = random.nextGaussian() * 0.05F;
			worldObj.spawnEntityInWorld(item);
		}
	}
	
	@Override
	public Block getBlockType() {
		super.getBlockType();
		if (blockType != null)
			id = blockType.blockID;
		return blockType;
	}
	
}
