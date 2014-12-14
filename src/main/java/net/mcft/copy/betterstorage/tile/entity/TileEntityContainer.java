package net.mcft.copy.betterstorage.tile.entity;

import net.mcft.copy.betterstorage.container.ContainerBetterStorage;
import net.mcft.copy.betterstorage.inventory.InventoryTileEntity;
import net.mcft.copy.betterstorage.utils.NbtUtils;
import net.mcft.copy.betterstorage.utils.PlayerUtils;
import net.mcft.copy.betterstorage.utils.WorldUtils;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.util.Constants.NBT;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public abstract class TileEntityContainer extends TileEntity {
	
	public final ItemStack[] contents;
	
	private final InventoryTileEntity playerInventory;
	
	/** The custom title of this container, set by an anvil. */
	private String customTitle = null;
	
	private int playersUsing = 0;
	
	protected boolean brokenInCreative = false;
	
	public int ticksExisted = 0;
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
		contents = ((size > 0) ? new ItemStack[size] : null);
		playerInventory = makePlayerInventory();
	}
	
	public InventoryTileEntity makePlayerInventory() {
		return ((contents != null) ? new InventoryTileEntity(this) : null);
	}
	public InventoryTileEntity getPlayerInventory() { return playerInventory; }
	
	/** Returns if a player can use this container. This is called once before
	 *  the GUI is opened and then again every tick. Returning false when the
	 *  GUI is open, like when the player is too far away, will close the GUI. */
	public boolean canPlayerUseContainer(EntityPlayer player) {
		return WorldUtils.isTileEntityUsableByPlayer(this, player);
	}
	
	/** Creates and returns a Container for this container. */
	public ContainerBetterStorage createContainer(EntityPlayer player) {
		return new ContainerBetterStorage(player, getPlayerInventory());
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
	
	// Block functions
	
	/** Called when a block is placed by a player. Sets data of the tile
	 *  entity, like custom container title, enchantments or similar. */
	public void onBlockPlaced(EntityLivingBase player, ItemStack stack) {
		if (stack.hasDisplayName())
			setCustomTitle(stack.getDisplayName());
	}
	
	/** Called then the block is activated (right clicked).
	 *  Usually opens the GUI of the container.*/
	public boolean onBlockActivated(EntityPlayer player, int side, float hitX, float hitY, float hitZ) {
		if (worldObj.isRemote) return true;
		if (!canPlayerUseContainer(player)) return true;
		openGui(player);
		return true;
	}
	
	/** Opens the GUI of this container for the player. */
	public void openGui(EntityPlayer player) {
		ContainerBetterStorage container = createContainer(player);
		PlayerUtils.openGui(player, getName(), container.getColumns(), container.getRows(), getCustomTitle(), container);
	}
	
	/** Called when the block is picked (default: middle mouse).
	 *  Returns the item to be picked, or null if nothing. */
	public ItemStack onPickBlock(ItemStack block, MovingObjectPosition target) { return block; }
	
	/** Called when a block is attempted to be broken by a player.
	 *  Returns if the block should actually be broken. */
	public boolean onBlockBreak(EntityPlayer player) {
		brokenInCreative = player.capabilities.isCreativeMode;
		return true;
	}
	
	/** Called after the block is destroyed, drops contents etc. */
	public void onBlockDestroyed() {
		dropContents();
		brokenInCreative = false;
	}
	
	/** Called when the container is destroyed to drop its contents. */
	public void dropContents() {
		if (contents != null)
			for (ItemStack stack : contents)
				WorldUtils.dropStackFromBlock(worldObj, getPos(), stack);

	}
	
	/** Called before the tile entity is being rendered as an item.
	 *  Sets things like the material taken from the stack. <br>
	 *  Only gets called if an ItemRendererContainer is registered.*/
	@SideOnly(Side.CLIENT)
	public void onBlockRenderAsItem(ItemStack stack) {  }
	
	// Comparator related
	
	private boolean compAccessedOnLoad = false;
	private boolean compAccessed = false;
	private boolean compContentsChanged = false;
	
	protected boolean hasComparatorAccessed() { return compAccessed; }
	protected boolean hasContentsChanged() { return compContentsChanged; }
	
	/** Helper function. Returns the comparator signal strength of
	 *  the container at this position, or 0 if there is no container
	 *  or the function is called on client-side. */
	public static int getContainerComparatorSignalStrength(IBlockAccess world, BlockPos pos) {
		TileEntityContainer container = WorldUtils.get(world, pos, TileEntityContainer.class);
		return ((container != null) ? container.getComparatorSignalStrength() : 0);
	}
	
	/** Called when a comparator or similar block wants to know this
	 *  TileEntity's comparator signal strength. Marks this location
	 *  to be updated the next time the container contents change. */
	public int getComparatorSignalStrength() {
		markComparatorAccessed();
		return getComparatorSignalStengthInternal();
	}
	
	/** Called when a comparator or similar block wants to know this
	 *  container's comparator signal strength. Marks the block to
	 *  update blocks around it next updateEntity(). */
	protected void markComparatorAccessed() {
		compAccessed = true;
	}
	
	/** Called when the inventory is modified. When a comparator
	 *  accessed this container, marks to update blocks around it
	 *  next updateEntity(). */
	protected void markContentsChanged() {
		compContentsChanged = true;
	}
	
	/** Returns the container's comparator signal strength. Calculated
	 *  automatically if it implements IInventory. Overridden if necessary. */
	protected int getComparatorSignalStengthInternal() {
		return ((this instanceof IInventory) ? Container.calcRedstoneFromInventory((IInventory)this) : 0);
	}
	
	/** Resets accessed and contents changed flags and updates nearby blocks. */
	protected void comparatorUpdateAndReset() {
		compAccessed = false;
		compContentsChanged = false;
		worldObj.notifyNeighborsOfStateChange(getPos(), getBlockType());

	}
	
	/** Calls the TileEntity.markDirty function without affecting the
	 *  attached inventory (if any). Marks the tile entity to be saved. */
	public void markDirtySuper() {
		if (worldObj.isRemote) return;
		//TODO (1.8): What's that for? Why is it needed?
		//worldObj.markTileEntityChunkModified(xCoord, yCoord, zCoord, this);

		if (hasComparatorAccessed())
			markContentsChanged();
	}
	
	@Override
	public void markDirty() {
		markDirtySuper();
	}
	
	@Override
	public void validate() {
		if (compAccessedOnLoad) {
			markComparatorAccessed();
			compAccessedOnLoad = false;
		}
	}
	
	// Players using synchronization
	
	/** Returns if the container does synchronize playersUsing at all. */
	protected boolean doesSyncPlayers() { return false; }
	/** Returns if the container should synchronize playersUsing over the network, called each tick. */
	protected boolean syncPlayersUsing() {
		//TODO (1.8): Change it. Simple.
		return (!worldObj.isRemote && doesSyncPlayers() &&
		        (((ticksExisted + pos.getX() + pos.getY() + pos.getZ()) & 0xFF) == 0) &&
		        worldObj.isAreaLoaded(this.getPos(), 16));
	}
	/** Synchronizes playersUsing over the network. */
	private void doSyncPlayersUsing(int playersUsing) {
		if (!doesSyncPlayers()) return;
		worldObj.addBlockEvent(getPos(), getBlockType(), 0, playersUsing);
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
	
	//TODO (1.8): This can't be gone. Not possible.
//	@Override
	public void updateEntity() {
		ticksExisted++;
		
		// If a comparator or such has accessed the container and
		// the contents have been changed, send a block update.
		if (hasComparatorAccessed() && hasContentsChanged())
			comparatorUpdateAndReset();
		
		if (syncPlayersUsing()) {
			int newPlayersUsing = WorldUtils.syncPlayersUsing(this, playersUsing);
			if (newPlayersUsing != playersUsing)
				doSyncPlayersUsing(playersUsing = newPlayersUsing);
		}
		
		prevLidAngle = lidAngle;
		if (playersUsing > 0) {
			if (lidAngle < 1.0F) lidAngle = Math.min(1.0F, lidAngle + getLidSpeed());
		} else if (lidAngle > 0.0F) lidAngle = Math.max(0.0F, lidAngle - getLidSpeed());
	}
	
	// Reading from / writing to NBT
	
	@Override
	public void readFromNBT(NBTTagCompound compound) {
		super.readFromNBT(compound);
		if (compound.hasKey("CustomName"))
			customTitle = compound.getString("CustomName");
		if (contents != null)
			NbtUtils.readItems(contents, compound.getTagList("Items", NBT.TAG_COMPOUND));
		if (compound.getBoolean("ComparatorAccessed"))
			compAccessedOnLoad = true;
	}
	@Override
	public void writeToNBT(NBTTagCompound compound) {
		super.writeToNBT(compound);
		if (customTitle != null)
			compound.setString("CustomName", customTitle);
		if (contents != null)
			compound.setTag("Items", NbtUtils.writeItems(contents));
		if (hasComparatorAccessed())
			compound.setBoolean("ComparatorAccessed", true);
	}
	
	// Utility functions
	
	/** Marks the block for an update, which will cause
	 *  a description packet to be send to players. */
	public void markForUpdate() {
		worldObj.markBlockForUpdate(getPos());
		markDirty();
	}
	
}
