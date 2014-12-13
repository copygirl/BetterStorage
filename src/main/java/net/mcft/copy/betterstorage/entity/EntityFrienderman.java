package net.mcft.copy.betterstorage.entity;

import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.world.World;

// In case anyone reads this: Even though this class is called "Frienderman"
// I would prefer it if you called the mob "friendly enderman". In my opinion,
// "Frienderman" implies friendship, whereas this mob is just friendly.

public class EntityFrienderman extends EntityEnderman {
	
	//TODO (1.8): Probably needs a complete rewrite.
	/*public static IdentityHashMap<Block, Boolean> friendermanCarriable = new IdentityHashMap<Block, Boolean>(1);
	static { friendermanCarriable.put(Blocks.ender_chest, true); }*/
	
	public EntityFrienderman(World world) {
		super(world);
	}
	/*
	@Override
	protected void applyEntityAttributes() {
		super.applyEntityAttributes();
		getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(40.0);
	}
	
	@Override
	protected Entity findPlayerToAttack() { return null; }
	
	@Override
	public void setScreaming(boolean screaming) {
//		 Friendly endermen don't scream.
	}
	
	@Override
	public Block func_146080_bZ() {
		// Fix vanilla code so it works with block
		// IDs from 0 to 255 instead of -128 to 127.
		// (Needed because ender chests are ID 130.)
		return Block.getBlockById((this.dataWatcher.getWatchableObjectByte(16) + 256) % 256);
	}
	
	@Override
	public boolean attackEntityFrom(DamageSource source, float damage) {
		boolean success = super.attackEntityFrom(source, damage);
		if (entityToAttack != null) {
			for (int i = 0; i < 40 - getMaxHealth() / 2; i++)
				if (teleportRandomly()) break;
			entityToAttack = null;
		}
		return success;
	}
	
	@Override
	protected void despawnEntity() {
		if (isNoDespawnRequired()) {
			entityAge = 0;
			return;
		}
		EntityPlayer player = worldObj.getClosestPlayerToEntity(this, -1.0D);
		double distance = ((player != null) ? getDistanceToEntity(player) : 64000);
		if (entityAge < 0) {  } // Do nothing, in case I want to add a feature
		                        // that'll make friendly endermen stay around longer.
		else if (distance < 16) entityAge = 0;
		else if (distance < 32) entityAge = Math.max(0, entityAge - 2);
		else if (distance < 48) entityAge = Math.max(0, entityAge - 1);
		else if ((distance > 64) && (entityAge > 20 * 60 * 5) &&
		         RandomUtils.getBoolean(0.001)) setDead();
	}
	
	@Override
	public void onLivingUpdate() {
		if (worldObj.isRemote) {
			super.onLivingUpdate();
			return;
		}
		
		int x = (int)Math.floor(posX);
		int y = (int)(posY + 0.6);
		int z = (int)Math.floor(posZ);
		
		GameRules rules = worldObj.getGameRules();
		String ruleBefore = rules.getGameRuleStringValue("mobGriefing");
		boolean ruleChanged = false;
		boolean hasEnderChest = (func_146080_bZ() == Blocks.ender_chest);
		boolean hasBackpack = (getEquipmentInSlot(EquipmentSlot.CHEST) != null);
		
		// Temporarily change the blocks which endermen can carry.
		// TODO: Make a pull request to Forge which allows us to do this by overriding a method instead.
		IdentityHashMap<Block, Boolean> carriable = ReflectionUtils.get(EntityEnderman.class, null, "carriable", "");
		ReflectionUtils.set(EntityEnderman.class, null, "carriable", "", friendermanCarriable);
		
		// Temporarily change mobGriefing gamerule to allow pickup and
		// prevent dropping of enderchests regardless of the gamerule.
		if (hasEnderChest) {
			if (ruleBefore.equalsIgnoreCase("true")) {
				rules.setOrCreateGameRule("mobGriefing", "false");
				ruleChanged = true;
			}
		} else if (hasBackpack && worldObj.isAirBlock(x, y, z)) {
			if (ruleBefore.equalsIgnoreCase("false")) {
				rules.setOrCreateGameRule("mobGriefing", "true");
				ruleChanged = true;
			}
		}
		
		super.onLivingUpdate();
		
		// Reset carriable blocks and gamerule.
		ReflectionUtils.set(EntityEnderman.class, null, "carriable", "", carriable);
		if (ruleChanged)
			rules.setOrCreateGameRule("mobGriefing", ruleBefore);
		
		// If ender chest was picked up, remove ender backpack and place it on the ground.
		if (!hasEnderChest && (func_146080_bZ() == Blocks.ender_chest)) {
			setCurrentItemOrArmor(3, null);
			worldObj.setBlock(x, y, z, BetterStorageTiles.enderBackpack, RandomUtils.getInt(2, 6), 3);
			WorldUtils.get(worldObj, x, y, z, TileEntityBackpack.class).stack =
					new ItemStack(BetterStorageItems.itemEnderBackpack);
			double px = x + 0.5;
			double py = y + 0.5;
			double pz = z + 0.5;
			BetterStorage.networkChannel.sendToAllAround(
					new PacketBackpackTeleport(px, py, pz, x, y, z),
					worldObj, px, py, pz, 256);
			worldObj.playSoundEffect(px, py, pz, "mob.endermen.portal", 1.0F, 1.0F);
		}
	}*/
}
