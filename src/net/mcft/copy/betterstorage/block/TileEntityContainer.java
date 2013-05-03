package net.mcft.copy.betterstorage.block;

import net.mcft.copy.betterstorage.container.ContainerBetterStorage;
import net.mcft.copy.betterstorage.inventory.InventoryTileEntity;
import net.mcft.copy.betterstorage.utils.NbtUtils;
import net.mcft.copy.betterstorage.utils.PlayerUtils;
import net.mcft.copy.betterstorage.utils.WorldUtils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;

public abstract class TileEntityContainer extends TileEntity {
	
	public final ItemStack[] contents;
	
	private final InventoryTileEntity playerInventory;
	
	/** The custom title of this container, set by an anvil. */
	private String customTitle = null;
	
	private int playersUsing = 0;
	private int ticksSinceSync;

	public float lidAngle = 0;
	public float prevLidAngle = 0;
	
	/** The amount of columns in the container. */
	public int getColumns() { return 9; }
	/** The amount of rows in the container. */
	public int getRows() { return 3; }
	
	/** The size of the container's contents, or 0 if it's not supposed to have any items. */
	protected int getSizeContents() { return getColumns() * getRows(); }
	
	/** The unlocalized name of the container, for example "container.chest". */
	public abstract String getName();
	
	/** The number of players which are currently accessing this container. */ 
	public final int getPlayersUsing() { return playersUsing; }
	
	public TileEntityContainer() {
		int size = getSizeContents();
		contents = ((size > 0) ? new ItemStack[getSizeContents()] : null);
		playerInventory = ((contents != null) ? new InventoryTileEntity(this) : null);
	}
	
	public InventoryTileEntity getPlayerInventory() { return playerInventory; }
	
	/** Returns if a player can use this container. */
	public boolean canPlayerUseContainer(EntityPlayer player) {
		return WorldUtils.isTileEntityUsableByPlayer(this, player);
	}
	
	// Container title related
	
	/** Returns the title of the container. */
	public String getContainerTitle() { return (hasCustomTitle() ? getCustomTitle() : getName()); }
	
	/** Returns the custom title of this container. */
	public String getCustomTitle() { return customTitle; }
	
	/** Returns if the container has a custom title. */
	public boolean hasCustomTitle() { return (getCustomTitle() != null); }
	
	/** Returns if the title of this container should be localized. */
	public boolean shouldLocalizeTitle() { return !hasCustomTitle(); }
	
	/** Returns if the title of this container can be set. */
	public boolean canSetCustomTitle() { return true; } 
	
	/** Sets the custom title of this container. Has no effect if it can't be set. */
	public void setCustomTitle(String title) { if (canSetCustomTitle()) customTitle = title; }
	
	// Container / GUI
	
	/** Opens a GUI of the container for the player. */
	public void openGui(EntityPlayer player) {
		if (!canPlayerUseContainer(player)) return;
		PlayerUtils.openGui(player, getName(), getColumns(), getRows(),
		                    getContainerTitle(), createContainer(player));
	}
	
	/** Creates and returns a Container for this container. */
	public ContainerBetterStorage createContainer(EntityPlayer player) {
		return new ContainerBetterStorage(player, getPlayerInventory());
	}
	
	// Players using synchronization
	
	/** Returns if the container should synchronize playersUsing over the network. */
	protected boolean syncPlayersUsing() { return true; }
	
	private void doSyncPlayersUsing(int playersUsing) {
		if (!syncPlayersUsing()) return;
		worldObj.addBlockEvent(xCoord, yCoord, zCoord, getBlockType().blockID, 0, playersUsing);
	}
	
	@Override
	public boolean receiveClientEvent(int event, int value) {
		if (event == 0) playersUsing = value;
		return true;
	}
	
	/** Called when a player opens this container. */
	public void onContainerOpened() {
		doSyncPlayersUsing(++playersUsing);
	}
	/** Called when a player closes this container. */
	public void onContainerClosed() {
		doSyncPlayersUsing(--playersUsing);
	}
	
	// Update entity
	
	protected float getLidSpeed() { return 0.1F; }
	
	@Override
	public void updateEntity() {
		if (!worldObj.isRemote && syncPlayersUsing())
			playersUsing = WorldUtils.syncPlayersUsing(this, ++ticksSinceSync, playersUsing);
		
		prevLidAngle = lidAngle;
		float lidSpeed = getLidSpeed();
		if (playersUsing > 0) lidAngle = Math.min(1.0F, lidAngle + lidSpeed);
		else lidAngle = Math.max(0.0F, lidAngle - lidSpeed);
	}
	
	// Dropping items
	
	/** Called when the container is destroyed, to drop all its contents. */
	public void dropContents() {
		for (ItemStack stack : contents)
			WorldUtils.dropStackFromBlock(worldObj, xCoord, yCoord, zCoord, stack);
	}
	
	// Reading from / writing to NBT
	
	@Override
	public void readFromNBT(NBTTagCompound compound) {
		super.readFromNBT(compound);
		if (compound.hasKey("CustomName"))
			customTitle = compound.getString("CustomName");
		if (contents != null)
			NbtUtils.readItems(contents, compound.getTagList("Items"));
	}
	@Override
	public void writeToNBT(NBTTagCompound compound) {
		super.writeToNBT(compound);
		if (customTitle != null)
			compound.setString("CustomName", customTitle);
		if (contents != null)
			compound.setTag("Items", NbtUtils.writeItems(contents));
	}
	
}
