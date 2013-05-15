package net.mcft.copy.betterstorage.entity;

import net.mcft.copy.betterstorage.BetterStorage;
import net.mcft.copy.betterstorage.block.TileEntityBackpack;
import net.mcft.copy.betterstorage.misc.handlers.PacketHandler;
import net.mcft.copy.betterstorage.utils.PacketUtils;
import net.mcft.copy.betterstorage.utils.RandomUtils;
import net.mcft.copy.betterstorage.utils.WorldUtils;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.packet.Packet;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.DamageSource;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;

public class EntityFrienderman extends EntityEnderman {
	
	private static final boolean[] friendermanCarriable = new boolean[4096];
	static { friendermanCarriable[Block.enderChest.blockID] = true; }
	
	public EntityFrienderman(World world) {
		super(world);
	}
	
	@Override
	public int getMaxHealth() { return 60; }
	
	@Override
	protected Entity findPlayerToAttack() { return null; }
	
	@Override
	public void setScreaming(boolean screaming) { /* Friendermen don't scream. */ }
	
	@Override
	public int getCarried() {
		// Fix vanilla code so it works with block
		// IDs from 0 to 255 instead of -128 to 127.
		// (Needed because ender chests are ID 130.)
		return ((super.getCarried() + 256) % 256);
	}
	
	@Override
	public boolean attackEntityFrom(DamageSource source, int damage) {
		boolean success = super.attackEntityFrom(source, damage);
		if (entityToAttack != null) {
			for (int i = 0; i < 40 - getHealth() / 2; i++)
				if (teleportRandomly()) break;
			entityToAttack = null;
		}
		return success;
	}
	
	@Override
	protected void despawnEntity() {
		EntityPlayer player = worldObj.getClosestPlayerToEntity( this, -1.0D);
		if (player == null) return;
		double distance = player.getDistanceToEntity(player);
		if (distance < 16) entityAge = 0;
		else if (distance < 32) entityAge = Math.max(0, entityAge - 2);
		else if (distance < 48) entityAge = Math.max(0, entityAge - 1);
		else if ((distance > 64) && (entityAge > 20 * 60 * 5) &&
		         RandomUtils.getBoolean(0.001)) setDead();
	}
	
	@Override
	public void onLivingUpdate() {
		
		boolean[] carriable = carriableBlocks;
		carriableBlocks = friendermanCarriable;
		
		int x = (int)Math.floor(posX);
		int y = (int)(posY + 0.1);
		int z = (int)Math.floor(posZ);
		
		GameRules rules = worldObj.getGameRules();
		String ruleBefore = rules.getGameRuleStringValue("mobGriefing");
		boolean ruleChanged = false;
		boolean hadEnderChest = (getCarried() == Block.enderChest.blockID);
		boolean canMoveStuff = ((getCarried() != 0) ^ ((worldObj.getBlockId(x, y, z) == 0) && (getCurrentArmor(2) != null)));
		
		if (hadEnderChest || !canMoveStuff) {
			if (ruleBefore.equalsIgnoreCase("true")) {
				rules.setOrCreateGameRule("mobGriefing", "false");
				ruleChanged = true;
			}
		} else if (canMoveStuff) {
			if (ruleBefore.equalsIgnoreCase("false")) {
				rules.setOrCreateGameRule("mobGriefing", "true");
				ruleChanged = true;
			}
		}
		
		super.onLivingUpdate();
		
		if (ruleChanged)
			rules.setOrCreateGameRule("mobGriefing", ruleBefore);
		
		if (!hadEnderChest && (getCarried() == Block.enderChest.blockID)) {
			setCurrentItemOrArmor(3, null);
			worldObj.setBlock(x, y, z, BetterStorage.enderBackpack.blockID, RandomUtils.getInt(2, 6), 3);
			WorldUtils.get(worldObj, x, y, z, TileEntityBackpack.class).stack =
					new ItemStack(BetterStorage.enderBackpack);
			double px = x + 0.5;
			double py = y + 0.5;
			double pz = z + 0.5;
			Packet packet = PacketUtils.makePacket(PacketHandler.backpackTeleport, posX, posY + 1, posZ, x, y, z);
			MinecraftServer.getServer().getConfigurationManager().sendToAllNear(
					px, py, pz, 512.0, worldObj.getWorldInfo().getDimension(), packet);
			worldObj.playSoundEffect(px, py, pz, "mob.endermen.portal", 1.0F, 1.0F);
		}
		
		carriableBlocks = carriable;
		
	}
	
	@Override
	public void readEntityFromNBT(NBTTagCompound compound) {
		super.readEntityFromNBT(compound);
	}
	
}
