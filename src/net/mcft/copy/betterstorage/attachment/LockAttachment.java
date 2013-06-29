package net.mcft.copy.betterstorage.attachment;

import net.mcft.copy.betterstorage.utils.StackUtils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;

public class LockAttachment extends ItemAttachment {
	
	public LockAttachment(TileEntity tileEntity) {
		super(tileEntity);
	}
	
	public void setLock(ItemStack lock) {
		item = lock;
	}
	
	@Override
	public boolean boxVisible(EntityPlayer player) {
		ItemStack holding = player.getCurrentEquippedItem();
		return ((item != null) || StackUtils.isLock(holding));
	}
	
}
