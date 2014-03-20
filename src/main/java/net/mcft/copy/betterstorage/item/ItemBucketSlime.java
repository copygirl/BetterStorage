package net.mcft.copy.betterstorage.item;

import java.util.List;

import net.mcft.copy.betterstorage.content.Items;
import net.mcft.copy.betterstorage.misc.Constants;
import net.mcft.copy.betterstorage.misc.CurrentItem;
import net.mcft.copy.betterstorage.utils.StackUtils;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.monster.EntitySlime;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.Facing;
import net.minecraft.util.Icon;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemBucketSlime extends ItemBetterStorage {
	
	@SideOnly(Side.CLIENT)
	private Icon iconMagmaCube, iconMazeSlime, iconPinkSlime,
	             iconThaumicSlime, iconBlueSlime;
	
	public ItemBucketSlime(int id) {
		super(id);
		setContainerItem(bucketEmpty);
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IconRegister iconRegister) {
		itemIcon = iconRegister.registerIcon(Constants.modId + ":bucketSlime");
		iconMagmaCube = iconRegister.registerIcon(Constants.modId + ":bucketSlime_magmaCube");
		iconMazeSlime = iconRegister.registerIcon(Constants.modId + ":bucketSlime_mazeSlime");
		iconPinkSlime = iconRegister.registerIcon(Constants.modId + ":bucketSlime_pinkSlime");
		iconThaumicSlime = iconRegister.registerIcon(Constants.modId + ":bucketSlime_thaumicSlime");
		iconBlueSlime = iconRegister.registerIcon(Constants.modId + ":bucketSlime_blueSlime");
	}
	
	@Override
	public Icon getIcon(ItemStack stack, int pass) {
		String id = StackUtils.get(stack, "Slime", "Slime", "id");
		if (id.equals("LavaSlime")) return iconMagmaCube;
		else if (id.equals("TwilightForest.Maze Slime")) return iconMazeSlime;
		else if (id.equals("MineFactoryReloaded.mfrEntityPinkSlime")) return iconPinkSlime;
		else if (id.equals("ThaumSlime")) return iconThaumicSlime;
		else if (id.equals("TConstruct.EdibleSlime")) return iconBlueSlime;
		else return itemIcon;
	}
	@Override
	@SideOnly(Side.CLIENT)
	public Icon getIconIndex(ItemStack stack) {
		return getIcon(stack, 0);
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean advancedTooltips) {
		String id = StackUtils.get(stack, "Slime", "Slime", "id");
		String name = StackUtils.get(stack, (String)null, "Slime", "name");
		if ((name != null) || advancedTooltips) 
			list.add("Contains: " + ((name != null) ? ("\"" + name + "\"" +
			                                           (advancedTooltips ? " (" + id + ")" : "")) : id));
	}
	
	@Override
	public boolean onItemUse(ItemStack stack, EntityPlayer player,
	                         World world, int x, int y, int z, int side,
	                         float hitX, float hitY, float hitZ) {
		if (!world.isRemote) {
			
			Block block = Block.blocksList[world.getBlockId(x, y, z)];
			x += Facing.offsetsXForSide[side];
			y += Facing.offsetsYForSide[side];
			z += Facing.offsetsZForSide[side];
			
			String id = StackUtils.get(stack, "Slime", "Slime", "id");
			String name = StackUtils.get(stack, (String)null, "Slime", "name");
			Entity entity = EntityList.createEntityByName(id, world);
			
			if ((entity != null) && (entity instanceof EntityLiving)) {
				EntityLiving slime = (EntityLiving)entity;
				
				float rotation = MathHelper.wrapAngleTo180_float(world.rand.nextFloat() * 360.0F);
				slime.setLocationAndAngles(x + 0.5, y, z + 0.5, rotation, 0.0F);
				slime.rotationYawHead = slime.renderYawOffset = rotation;
				
				if (name != null) slime.setCustomNameTag(name);
				setSlimeSize(slime, 1);
				
				world.spawnEntityInWorld(slime);
				slime.playSound("mob.slime.big", 1.2F, 0.6F);
				
				player.setCurrentItemOrArmor(CurrentItem.HELD, new ItemStack(Item.bucketEmpty));
			}
			
		}
		return true;
	}
	
	/** Called when a player right clicks an entity with an empty bucket.
	 *  If the entity clicked is a small slime, attempts to pick it up and
	 *  sets the held item to a Slime in a Bucket containing that slime. */
	public static void pickUpSlime(EntityPlayer player, EntityLiving slime) {
		if (slime.isDead || (getSlimeSize(slime) != 1)) return;
		
		ItemStack stack = new ItemStack(Items.slimeBucket);
		
		String entityId = EntityList.getEntityString(slime);
		if (!entityId.equals("Slime"))
			StackUtils.set(stack, entityId, "Slime", "id");
		if (slime.hasCustomNameTag())
			StackUtils.set(stack, slime.getCustomNameTag(), "Slime", "name");
		
		if (--player.getCurrentEquippedItem().stackSize <= 0)
			player.setCurrentItemOrArmor(CurrentItem.HELD, stack);
		else player.dropPlayerItem(stack);
		
		slime.playSound("mob.slime.big", 1.2F, 0.8F);
		slime.isDead = true;
	}
	
	public static int getSlimeSize(EntityLiving slime) {
		String name = EntityList.getEntityString(slime);
		if ((slime instanceof EntitySlime) ||
		    name.equals("ThaumSlime") || name.equals("TConstruct.EdibleSlime"))
			return slime.getDataWatcher().getWatchableObjectByte(16);
		else return 0;
	}
	
	public static void setSlimeSize(EntityLiving slime, int size) {
		String name = EntityList.getEntityString(slime);
		NBTTagCompound compound = new NBTTagCompound();
		slime.writeEntityToNBT(compound);
		compound.setInteger("Size", size - 1);
		slime.readFromNBT(compound);
	}
	
}
