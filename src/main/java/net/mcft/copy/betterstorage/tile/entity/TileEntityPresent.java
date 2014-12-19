package net.mcft.copy.betterstorage.tile.entity;

import net.mcft.copy.betterstorage.BetterStorage;
import net.mcft.copy.betterstorage.content.BetterStorageTiles;
import net.mcft.copy.betterstorage.item.tile.ItemCardboardBox;
import net.mcft.copy.betterstorage.network.packet.PacketPresentOpen;
import net.mcft.copy.betterstorage.utils.StackUtils;
import net.mcft.copy.betterstorage.utils.WorldUtils;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class TileEntityPresent extends TileEntityCardboardBox {
	
	public static final String TAG_COLOR_INNER = "presentColorInner";
	public static final String TAG_COLOR_OUTER = "presentColorOuter";
	public static final String TAG_SKOJANZA_MODE = "skojanzaMode";
	public static final String TAG_NAMETAG = "nameTag";
	
	public int colorInner = 14;
	public int colorOuter = 0;
	public boolean skojanzaMode = false;
	public String nameTag = null;
	
	public int breakProgress = 0;
	public int breakPause = 0;
	
	@Override
	protected boolean canPickUp() { return true; }
	
	@Override
	protected ItemStack getItemDropped() {
		return (!destroyed ? new ItemStack(BetterStorageTiles.present) : null);
	}
	
	@Override
	protected void onItemDropped(ItemStack stack) {
		super.onItemDropped(stack);
		NBTTagCompound compound = stack.getTagCompound();
		compound.setByte(TAG_COLOR_INNER, (byte)colorInner);
		compound.setByte(TAG_COLOR_OUTER, (byte)colorOuter);
		compound.setBoolean(TAG_SKOJANZA_MODE, skojanzaMode);
		if (nameTag != null)
			compound.setString(TAG_NAMETAG, nameTag);
		StackUtils.remove(stack, "display", "color");
		compound.setInteger("color", color);
	}
	
	@Override
	public void update() {
		breakPause = Math.max(0, breakPause - 1);
		if (breakPause <= 0)
			breakProgress = Math.max(0, breakProgress - 1);
	}
	
	// TileEntityContainer stuff
	
	@Override
	public void onBlockPlaced(EntityLivingBase player, ItemStack stack) {
		super.onBlockPlaced(player, stack);
		NBTTagCompound compound = stack.getTagCompound();
		colorInner = ((compound != null) ? compound.getByte(TAG_COLOR_INNER) : 14);
		colorOuter = ((compound != null) ? compound.getByte(TAG_COLOR_OUTER) : 0);
		skojanzaMode = ((compound != null) ? compound.getBoolean(TAG_SKOJANZA_MODE) : false);
		nameTag = (((compound != null) && compound.hasKey(TAG_NAMETAG))
				? compound.getString(TAG_NAMETAG) : null);
		color = (((compound != null) && compound.hasKey("color"))
				? compound.getInteger("color") : -1);
		//TODO (1.8): Meta? What's meta again?
//		worldObj.setBlockMetadataWithNotify(xCoord, yCoord, zCoord, colorInner, SetBlockFlag.SEND_TO_CLIENT);
	}
	
	@Override
	public boolean onBlockActivated(EntityPlayer player, EnumFacing side, float hitX, float hitY, float hitZ) {
		
		ItemStack holding = player.getCurrentEquippedItem();
		if (holding == null) {
			
			if (breakPause > 0) return false;
			breakPause = 10 - breakProgress / 10;
			if ((nameTag != null) && !player.getName().equalsIgnoreCase(nameTag)) {
				breakPause = 40;
				if (!worldObj.isRemote)
					((EntityPlayerMP)player).addChatMessage(new ChatComponentText(
							EnumChatFormatting.YELLOW + "This present is for " + nameTag + ", not you!"));
				return false;
			}
			if ((breakProgress += 20) > 100)
				destroyed = true;
			if (worldObj.isRemote) return true;
			
			double x = getPos().getX() + 0.5;
			double y = getPos().getY() + 0.5;
			double z = getPos().getZ() + 0.5;
			
			String sound = Block.soundTypeCloth.getBreakSound();
			worldObj.playSoundEffect(x, y, z, sound, 0.75F, 0.8F + breakProgress / 80.0F);
			//TODO (1.8): Maybe an utility function for that? Seen it quite a few times.
//			worldObj.playSoundEffect(xCoord + 0.5, yCoord + 0.5, zCoord + 0.5, sound, 1.0F, 0.4F + breakProgress / 160.0F);
			
			BetterStorage.networkChannel.sendToAllAround(
					new PacketPresentOpen(getPos(), destroyed),
					worldObj, x, y, z, 64);
			
			if (!destroyed)
				return true;
			
			if (BetterStorageTiles.cardboardBox != null) {
				//TODO (1.8): I'm still not sure how that setBlockState thing is working, but setBlock is gone...
				if (worldObj.setBlockState(getPos(), BetterStorageTiles.cardboardBox.getDefaultState())) {
					TileEntityCardboardBox box = WorldUtils.get(worldObj, getPos(), TileEntityCardboardBox.class);
					box.uses = ItemCardboardBox.getUses();
					box.color = color;
					System.arraycopy(contents, 0, box.contents, 0, contents.length);
				} else for (ItemStack stack : contents)
					WorldUtils.dropStackFromBlock(worldObj, getPos(), stack);
			} else if (worldObj.setBlockToAir(getPos()))
				for (ItemStack stack : contents)
					WorldUtils.dropStackFromBlock(worldObj, getPos(), stack);
			return true;
			
		} else if ((holding.getItem() == Items.name_tag) &&
		           (nameTag == null) && holding.hasDisplayName()) {
			if (holding.getDisplayName().matches("^[a-zA-Z0-9_]{2,16}$")) {
				if (!worldObj.isRemote) {
					nameTag = holding.getDisplayName();
					holding.stackSize--;
					markForUpdate();
				}
				return true;
			} else {
				if (!worldObj.isRemote)
					((EntityPlayerMP)player).addChatMessage(new ChatComponentText(
							EnumChatFormatting.YELLOW + "The nametag doesn't seem to contain a valid username."));
				return false;
			}
		} else return false;
	}
	
	@Override
	public void dropContents() {  }
	
	@Override
	@SideOnly(Side.CLIENT)
	public void onBlockRenderAsItem(ItemStack stack) {
		NBTTagCompound compound = stack.getTagCompound();
		colorInner = ((compound != null) ? compound.getByte(TAG_COLOR_INNER) : 14);
		colorOuter = ((compound != null) ? compound.getByte(TAG_COLOR_OUTER) : 0);
		skojanzaMode = ((compound != null) ? compound.getBoolean(TAG_SKOJANZA_MODE) : false);
		nameTag = (((compound != null) && compound.hasKey(TAG_NAMETAG))
				? compound.getString(TAG_NAMETAG) : null);
	}
	
	// Tile entity synchronization
	
	@Override
	public Packet getDescriptionPacket() {
		NBTTagCompound compound = new NBTTagCompound();
		if (color >= 0) compound.setInteger("color", color);
		compound.setByte(TAG_COLOR_INNER, (byte)colorInner);
		compound.setByte(TAG_COLOR_OUTER, (byte)colorOuter);
		compound.setBoolean(TAG_SKOJANZA_MODE, skojanzaMode);
		if (nameTag != null)
			compound.setString(TAG_NAMETAG, nameTag);
		return new S35PacketUpdateTileEntity(getPos(), 0, compound);
	}
	@Override
	public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity packet) {
		super.onDataPacket(net, packet);
		NBTTagCompound compound = packet.getNbtCompound();
		colorInner = compound.getByte(TAG_COLOR_INNER);
		colorOuter = compound.getByte(TAG_COLOR_OUTER);
		skojanzaMode = compound.getBoolean(TAG_SKOJANZA_MODE);
		nameTag = (compound.hasKey(TAG_NAMETAG) ? compound.getString(TAG_NAMETAG) : null);
	}
	
	// Reading from / writing to NBT
	
	@Override
	public void readFromNBT(NBTTagCompound compound) {
		super.readFromNBT(compound);
		colorInner = compound.getByte(TAG_COLOR_INNER);
		colorOuter = compound.getByte(TAG_COLOR_OUTER);
		skojanzaMode = compound.getBoolean(TAG_SKOJANZA_MODE);
		nameTag = (compound.hasKey(TAG_NAMETAG) ? compound.getString(TAG_NAMETAG) : null);
	}
	@Override
	public void writeToNBT(NBTTagCompound compound) {
		super.writeToNBT(compound);
		compound.setByte(TAG_COLOR_INNER, (byte)colorInner);
		compound.setByte(TAG_COLOR_OUTER, (byte)colorOuter);
		compound.setBoolean(TAG_SKOJANZA_MODE, skojanzaMode);
		if (nameTag != null)
			compound.setString(TAG_NAMETAG, nameTag);
	}
	
}
