package net.mcft.copy.betterstorage.tile.entity;

import net.mcft.copy.betterstorage.content.BetterStorageTiles;
import net.mcft.copy.betterstorage.inventory.InventoryCardboardBox;
import net.mcft.copy.betterstorage.inventory.InventoryTileEntity;
import net.mcft.copy.betterstorage.item.tile.ItemCardboardBox;
import net.mcft.copy.betterstorage.misc.Constants;
import net.mcft.copy.betterstorage.utils.StackUtils;
import net.mcft.copy.betterstorage.utils.WorldUtils;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;

public class TileEntityCardboardBox extends TileEntityContainer {
	
	public int uses = 1;
	public int color = -1;
	public boolean destroyed = false;
	
	protected boolean canPickUp() { return (uses >= 0); }
	
	protected ItemStack getItemDropped() {
		return new ItemStack(BetterStorageTiles.cardboardBox);
	}
	
	protected void onItemDropped(ItemStack stack) {
		if (ItemCardboardBox.getUses() > 0) StackUtils.set(stack, uses, "uses");
		if (color >= 0) StackUtils.set(stack, color, "display", "color");
		if (getCustomTitle() != null) stack.setStackDisplayName(getCustomTitle());
	}
	
	// TileEntityContainer stuff
	
	@Override
	public String getName() { return Constants.containerCardboardBox; }
	
	@Override
	public int getRows() { return ItemCardboardBox.getRows(); }
	
	@Override
	public InventoryTileEntity makePlayerInventory() {
		return new InventoryTileEntity(this, new InventoryCardboardBox(contents));
	}
	
	@Override
	public void onBlockPlaced(EntityLivingBase player, ItemStack stack) {
		super.onBlockPlaced(player, stack);
		// If the cardboard box item has items, set the container contents to them.
		if (StackUtils.has(stack, "Items")) {
			ItemStack[] itemContents = StackUtils.getStackContents(stack, contents.length);
			System.arraycopy(itemContents, 0, contents, 0, itemContents.length);
		}
		int maxUses = ItemCardboardBox.getUses();
		if (maxUses > 0) uses = StackUtils.get(stack, maxUses, "uses");
		color = StackUtils.get(stack, -1, "display", "color");
	}
	
	@Override
	public void onBlockDestroyed() {
		if (!canPickUp() || destroyed) return;
		boolean empty = StackUtils.isEmpty(contents);
		if (!empty) {
			uses--;
			if (!canPickUp()) {
				destroyed = true;
				dropContents();
				return;
			}
		}
		ItemStack stack = getItemDropped();
		if (!empty)
			StackUtils.setStackContents(stack, contents);
		onItemDropped(stack);
		// Don't drop an empty cardboard box in creative.
		if (!empty || !brokenInCreative)
			WorldUtils.dropStackFromBlock(this, stack);
	}
	
	// Tile entity synchronization
	
	@Override
	public Packet getDescriptionPacket() {
		NBTTagCompound compound = new NBTTagCompound();
		if (color >= 0) compound.setInteger("color", color);
		return new S35PacketUpdateTileEntity(getPos(), 0, compound);
	}
	@Override
	public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity packet) {
		NBTTagCompound compound = packet.getNbtCompound();
		color = (compound.hasKey("color") ? compound.getInteger("color") : -1);
		worldObj.markBlockForUpdate(getPos());
	}
	
	// Reading from / writing to NBT
	
	@Override
	public void readFromNBT(NBTTagCompound compound) {
		super.readFromNBT(compound);
		uses = (compound.hasKey("uses") ? compound.getInteger("uses") : ItemCardboardBox.getUses());
		color = (compound.hasKey("color") ? compound.getInteger("color") : -1);
	}
	@Override
	public void writeToNBT(NBTTagCompound compound) {
		super.writeToNBT(compound);
		if (ItemCardboardBox.getUses() > 0) compound.setInteger("uses", uses);
		if (color >= 0) compound.setInteger("color", color);
	}
	
}
