package net.mcft.copy.betterstorage.attachment;

import net.mcft.copy.betterstorage.api.IKey;
import net.mcft.copy.betterstorage.api.ILock;
import net.mcft.copy.betterstorage.api.ILockable;
import net.mcft.copy.betterstorage.utils.StackUtils;
import net.mcft.copy.betterstorage.utils.WorldUtils;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;

public class LockAttachment extends ItemAttachment {
	
	public LockAttachment(TileEntity tileEntity, int subId) {
		super(tileEntity, subId);
		if (!(tileEntity instanceof ILockable))
			throw new IllegalArgumentException("tileEntity must be ILockable.");
	}
	
	@Override
	public boolean boxVisible(EntityPlayer player) {
		ItemStack holding = ((player != null) ? player.getCurrentEquippedItem() : null);
		return ((item != null) || StackUtils.isLock(holding));
	}
	
	@Override
	public boolean interact(EntityPlayer player, EnumAttachmentInteraction type) {
		ItemStack holding = player.getCurrentEquippedItem();
		return ((type == EnumAttachmentInteraction.attack)
				? attack(player, holding)
				: use(player, holding));
	}
	
	private boolean attack(EntityPlayer player, ItemStack holding) {
		return true;
	}
	
	private boolean use(EntityPlayer player, ItemStack holding) {
		ILockable lockable = (ILockable)tileEntity;
		ItemStack lock = lockable.getLock();
		if (lock == null) {
			if (StackUtils.isLock(holding) && lockable.isLockValid(holding)) {
				if (!player.worldObj.isRemote) {
					lockable.setLock(holding);
					player.inventory.setInventorySlotContents(player.inventory.currentItem, null);
					return true;
				}
			}
		} else if (StackUtils.isKey(holding)) {
			IKey keyType = (IKey)holding.getItem();
			ILock lockType = (ILock)lock.getItem();
			
			if (!player.worldObj.isRemote) {
				boolean success = keyType.unlock(holding, lock, true);
				lockType.onUnlock(lock, holding, lockable, player, success);
				if (!success) return true;
				
				if (player.isSneaking()) {
					lockable.setLock(null);
					AxisAlignedBB box = getBox();
					double x = tileEntity.xCoord + (box.minX + box.maxX) / 2;
					double y = tileEntity.yCoord + (box.minY + box.maxY) / 2;
					double z = tileEntity.zCoord + (box.minZ + box.maxZ) / 2;
					EntityItem item = WorldUtils.spawnItem(player.worldObj, x, y, z, lock);
				} else lockable.useUnlocked(player);
				
				return true;
			}
		}
		return false;
	}
	
}
