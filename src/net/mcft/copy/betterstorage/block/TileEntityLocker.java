package net.mcft.copy.betterstorage.block;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.mcft.copy.betterstorage.utils.InventoryUtils;
import net.mcft.copy.betterstorage.utils.NbtUtils;
import net.mcft.copy.betterstorage.utils.WorldUtils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.Packet132TileEntityData;
import net.minecraft.util.AxisAlignedBB;
import net.minecraftforge.common.ForgeDirection;

public class TileEntityLocker extends TileEntityConnectable implements IInventory {
	
	private ItemStack[] contents;

	private int numUsingPlayers = 0;
	private int ticksSinceSync;
	
	public float lidAngle = 0;
	public float prevLidAngle = 0;
	
	public boolean mirror = false;
	
	public TileEntityLocker() {
		contents = new ItemStack[3 * 9];
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public AxisAlignedBB getRenderBoundingBox() {
		return WorldUtils.getAABB(this, 0, 0, 0, 0, 1, 0);
	}
	
	// TileEntityConnectable stuff
	
	public TileEntityLocker getMainLocker() {
		return (TileEntityLocker)getMain();
	}
	public TileEntityLocker getConnectedLocker() {
		return (TileEntityLocker)getConnected();
	}
	
	private static ForgeDirection[] neighbors = {
		ForgeDirection.DOWN, ForgeDirection.UP };
	@Override
	public ForgeDirection[] getPossibleNeighbors() { return neighbors; }
	
	@Override
	public boolean canConnect(TileEntityConnectable connectable) {
		if (!(connectable instanceof TileEntityLocker)) return false;
		TileEntityLocker locker = (TileEntityLocker)connectable;
		return (super.canConnect(connectable) && (mirror == locker.mirror));
	}
	
	// TileEntity synchronization
	
	@Override
	public Packet getDescriptionPacket() {
		Packet132TileEntityData packet =
				(Packet132TileEntityData)super.getDescriptionPacket();
		NBTTagCompound compound = packet.customParam1;
		compound.setBoolean("mirror", mirror);
        return packet;
	}
	
	@Override
	public void onDataPacket(INetworkManager net, Packet132TileEntityData packet) {
		super.onDataPacket(net, packet);
		NBTTagCompound compound = packet.customParam1;
		mirror = compound.getBoolean("mirror");
	}
	
	// Reading from / writing to NBT
	
	@Override
	public void readFromNBT(NBTTagCompound compound) {
		super.readFromNBT(compound);
		orientation = ForgeDirection.getOrientation(compound.getByte("orientation"));
		mirror = compound.getBoolean("mirror");
		NBTTagList items = compound.getTagList("Items");
		contents = NbtUtils.readItems(items, contents.length);
	}
	
	@Override
	public void writeToNBT(NBTTagCompound compound) {
		super.writeToNBT(compound);
		compound.setByte("orientation", (byte)orientation.ordinal());
		compound.setBoolean("mirror", mirror);
		compound.setTag("Items", NbtUtils.writeItems(contents));
	}
	
	// IInventory stuff
	
	@Override
	public String getInvName() { return "container.locker"; }
	@Override
	public int getInventoryStackLimit() { return 64; }
	
	@Override
	public int getSizeInventory() { return contents.length; }
	
	@Override
	public ItemStack getStackInSlot(int slot) { return contents[slot]; }
	@Override
	public void setInventorySlotContents(int slot, ItemStack stack) { contents[slot] = stack; }
	@Override
	public ItemStack decrStackSize(int slot, int amount) {
		return InventoryUtils.unsafeDecreaseStackSize(this, slot, amount);
	}
	@Override
	public ItemStack getStackInSlotOnClosing(int slot) { return null; }
	
	@Override
	public boolean isUseableByPlayer(EntityPlayer player) {
		return WorldUtils.isTileEntityUsableByPlayer(this, player);
	}
	@Override
	public boolean isInvNameLocalized() { return false; }
	@Override
	public boolean isStackValidForSlot(int i, ItemStack stack) { return true; }
	
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
	@Override
	public boolean receiveClientEvent(int eventId, int val) {
		numUsingPlayers = val;
		return true;
	}
	
	@Override
	public void updateEntity() {
		numUsingPlayers = WorldUtils.syncPlayersUsing(this, ++ticksSinceSync, numUsingPlayers);
		
		prevLidAngle = lidAngle;
		float lidSpeed = 0.1F;
		double x = xCoord + 0.5;
		double y = yCoord + 0.5;
		double z = zCoord + 0.5;
		if (isConnected()) {
			TileEntityConnectable connectable = getConnected();
			if (connectable != null) {
				x = (x + connectable.xCoord + 0.5) / 2;
				z = (z + connectable.zCoord + 0.5) / 2;
			}
		}
		
		// Play sound when opening
		if (numUsingPlayers > 0 && lidAngle == 0.0F && isMain())
			worldObj.playSoundEffect(x, y, z, "random.chestopen", 0.5F,
			                         worldObj.rand.nextFloat() * 0.1F + 0.9F);
		
		if ((numUsingPlayers == 0 && lidAngle > 0.0F) ||
		    (numUsingPlayers >  0 && lidAngle < 1.0F)) {
			
			if (numUsingPlayers > 0) lidAngle = Math.min(1.0F, lidAngle + lidSpeed);
			else lidAngle = Math.max(0.0F, lidAngle - lidSpeed);
			
			// Play sound when closing
			if (lidAngle < 0.5F && prevLidAngle >= 0.5F && isMain())
				worldObj.playSoundEffect(x, y, z, "random.chestclosed", 0.5F,
                        worldObj.rand.nextFloat() * 0.1F + 0.9F);
		}
	}
	
	// Dropping stuff
	
	public void dropContents() {
		for (ItemStack stack : contents)
			WorldUtils.dropStackFromBlock(worldObj, xCoord, yCoord, zCoord, stack);
	}
	
}
