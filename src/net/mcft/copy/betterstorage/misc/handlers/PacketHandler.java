package net.mcft.copy.betterstorage.misc.handlers;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import net.mcft.copy.betterstorage.Config;
import net.mcft.copy.betterstorage.block.tileentity.TileEntityLockable;
import net.mcft.copy.betterstorage.item.ItemBackpack;
import net.mcft.copy.betterstorage.item.ItemDrinkingHelmet;
import net.mcft.copy.betterstorage.misc.Constants;
import net.mcft.copy.betterstorage.utils.PlayerUtils;
import net.mcft.copy.betterstorage.utils.RandomUtils;
import net.mcft.copy.betterstorage.utils.WorldUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.network.IPacketHandler;
import cpw.mods.fml.common.network.Player;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class PacketHandler implements IPacketHandler {
	
	public static final byte openGui = 0;
	public static final byte backpackTeleport = 1;
	public static final byte backpackHasItems = 2;
	public static final byte backpackOpen = 3;
	public static final byte backpackKeyEnabled = 4;
	public static final byte drinkingHelmet = 5;
	public static final byte lockHit = 6;
	public static final byte clientSpawn = 7;
	public static final byte backpackIsOpen = 8;
	public static final byte backpackStack = 9;
	
	public static Packet makePacket(Object... args) {
		ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
		DataOutputStream dataStream = new DataOutputStream(byteStream);
		for (Object obj : args) writeObject(dataStream, obj);
		return new Packet250CustomPayload(Constants.modId, byteStream.toByteArray());
	}
	
	private static void writeObject(DataOutputStream stream, Object object) {
		try {
			if (object instanceof Byte) stream.writeByte((Byte)object);
			else if (object instanceof Short) stream.writeShort((Short)object);
			else if (object instanceof Integer) stream.writeInt((Integer)object);
			else if (object instanceof Float) stream.writeFloat((Float)object);
			else if (object instanceof Double) stream.writeDouble((Double)object);
			else if (object instanceof String) stream.writeUTF((String)object);
			else if (object instanceof Boolean) stream.writeBoolean((Boolean)object);
			else if (object instanceof byte[]) stream.write((byte[])object);
			else if (object instanceof ItemStack) writeItemStack(stream, (ItemStack)object);
			else if (object == null) NBTBase.writeNamedTag(new NBTTagCompound(), stream);
		} catch (Exception e) { throw new RuntimeException(e); }
	}
	
	public static void writeItemStack(DataOutputStream stream, ItemStack stack) throws IOException {
		NBTBase.writeNamedTag(stack.writeToNBT(new NBTTagCompound()), stream);
	}
	public static ItemStack readItemStack(DataInputStream stream) throws IOException {
		return ItemStack.loadItemStackFromNBT((NBTTagCompound)NBTBase.readNamedTag(stream));
	}
	
	@Override
	public void onPacketData(INetworkManager manager, Packet250CustomPayload packet, Player pl) {
		EntityPlayer player = (EntityPlayer)pl;
		Side side = FMLCommonHandler.instance().getEffectiveSide();
		DataInputStream stream = new DataInputStream(new ByteArrayInputStream(packet.data));
		try {
			int id = stream.readByte();
			switch (id) {
				case openGui:
					checkSide(id, side, Side.CLIENT);
					handleOpenGui(player, stream);
					break;
				case backpackTeleport:
					checkSide(id, side, Side.CLIENT);
					handleBackpackTeleport(player, stream);
					break;
				case backpackHasItems:
					checkSide(id, side, Side.CLIENT);
					handleBackpackHasItems(player, stream);
					break;
				case backpackOpen:
					checkSide(id, side, Side.SERVER);
					handleBackpackOpen(player, stream);
					break;
				case backpackKeyEnabled:
					checkSide(id, side, Side.CLIENT);
					handleBackpackKeyEnabled(player, stream);
					break;
				case drinkingHelmet:
					checkSide(id, side, Side.SERVER);
					handleDrinkingHelmet(player, stream);
					break;
				case lockHit:
					checkSide(id, side, Side.CLIENT);
					handleLockHit(player, stream);
					break;
				case clientSpawn:
					checkSide(id, side, Side.SERVER);
					handleClientSpawn(player, stream);
					break;
				case backpackIsOpen:
					checkSide(id, side, Side.CLIENT);
					handleBackpackIsOpen(player, stream);
					break;
				case backpackStack:
					checkSide(id, side, Side.CLIENT);
					handleBackpackStack(player, stream);
					break;
				default:
					throw new Exception("Received " + Constants.modName + " packet for unhandled ID " + id + " on side " + side + ".");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void checkSide(int id, Side side, Side allowed) throws Exception {
		if (side == allowed) return;
		throw new Exception("Received " + Constants.modName + " packet for ID " + id + " on invalid side " + side + ".");
	}
	
	@SideOnly(Side.CLIENT)
	private void handleOpenGui(EntityPlayer player, DataInputStream stream) throws IOException {
		int windowId = stream.readInt();
		String name = stream.readUTF();
		int columns = stream.readByte();
		int rows = stream.readByte();
		String title = stream.readUTF();
		PlayerUtils.openGui(player, name, columns, rows, title);
		player.openContainer.windowId = windowId;
	}
	
	@SideOnly(Side.CLIENT)
	private void handleBackpackTeleport(EntityPlayer player, DataInputStream stream) throws IOException {
		double sourceX = stream.readDouble();
		double sourceY = stream.readDouble();
		double sourceZ = stream.readDouble();
		int x = stream.readInt();
		int y = stream.readInt();
		int z = stream.readInt();
		World world = Minecraft.getMinecraft().theWorld;
		int amount = 128;
		for (int i = 0; i < amount; i++) {
			double a = i / (double)(amount - 1);
			double vX = RandomUtils.getDouble(-0.3, 0.3);
			double vY = RandomUtils.getDouble(-0.3, 0.3);
			double vZ = RandomUtils.getDouble(-0.3, 0.3);
			double pX = sourceX + (x - sourceX) * a + RandomUtils.getDouble(0.3, 0.7);
			double pY = sourceY + (y - sourceY) * a + RandomUtils.getDouble(-0.5, 0.0) + a / 2;
			double pZ = sourceZ + (z - sourceZ) * a + RandomUtils.getDouble(0.3, 0.7);
			world.spawnParticle("portal", pX, pY, pZ, vX, vY, vZ);
		}
	}
	
	@SideOnly(Side.CLIENT)
	private void handleBackpackHasItems(EntityPlayer player, DataInputStream stream) throws IOException {
		boolean hasItems = stream.readBoolean();
		ItemBackpack.getBackpackData(player).hasItems = hasItems;
	}
	
	private void handleBackpackOpen(EntityPlayer player, DataInputStream stream) {
		if (Config.enableBackpackOpen)
			ItemBackpack.openBackpack(player, player);
	}
	
	@SideOnly(Side.CLIENT)
	private void handleBackpackKeyEnabled(EntityPlayer player, DataInputStream stream) throws IOException {
		KeyBindingHandler.serverBackpackKeyEnabled = stream.readBoolean();
	}
	
	private void handleDrinkingHelmet(EntityPlayer player, DataInputStream stream) {
		ItemDrinkingHelmet.use(player);
	}
	
	@SideOnly(Side.CLIENT)
	private void handleLockHit(EntityPlayer player, DataInputStream stream) throws IOException {
		int x = stream.readInt();
		int y = stream.readInt();
		int z = stream.readInt();
		boolean damage = stream.readBoolean();
		World world = Minecraft.getMinecraft().theWorld;
		TileEntityLockable lockable = WorldUtils.get(world, x, y, z, TileEntityLockable.class);
		if (lockable != null) lockable.lockAttachment.hit(damage);
	}
	
	private void handleClientSpawn(EntityPlayer player, DataInputStream stream) throws IOException {
		int entityID = stream.readInt();
		Entity entity = player.worldObj.getEntityByID(entityID);
		if ((entity == null) || !(entity instanceof EntityLivingBase)) return;
		ItemBackpack.getBackpackData((EntityLivingBase)entity).sendDataToPlayer((EntityLivingBase)entity, player);
	}
	
	@SideOnly(Side.CLIENT)
	private void handleBackpackIsOpen(EntityPlayer player, DataInputStream stream) throws IOException {
		int entityID = stream.readInt();
		boolean isOpen = stream.readBoolean();
		Entity entity = Minecraft.getMinecraft().theWorld.getEntityByID(entityID);
		if (entity == null) return;
		ItemBackpack.getBackpackData((EntityLivingBase)entity).playersUsing = ((isOpen) ? 1 : 0);
	}
	
	@SideOnly(Side.CLIENT)
	private void handleBackpackStack(EntityPlayer player, DataInputStream stream) throws IOException {
		int entityID = stream.readInt();
		ItemStack stack = readItemStack(stream);
		Entity entity = Minecraft.getMinecraft().theWorld.getEntityByID(entityID);
		if (entity == null) return;
		ItemBackpack.getBackpackData((EntityLivingBase)entity).backpack = stack;
	}
	
	/** Sends a packet to everyone near a certain position in the world. */
	public static void sendToEveryoneNear(World world, double x, double y, double z, double distance, EntityPlayer except, Packet packet) {
		MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();
		server.getConfigurationManager().sendToAllNearExcept(except, x, y, z, distance, world.provider.dimensionId, packet);
	}
	
	/** Sends a packet to everyone tracking this entity. */
	public static void sendToEveryoneTracking(Entity entity, Packet packet) {
		((WorldServer)entity.worldObj).getEntityTracker().sendPacketToAllAssociatedPlayers(entity, packet);
	}
	
}
