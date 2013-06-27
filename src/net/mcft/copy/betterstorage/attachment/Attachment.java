package net.mcft.copy.betterstorage.attachment;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public abstract class Attachment {
	
	public final TileEntity tileEntity;
	
	public Attachment(TileEntity tileEntity) {
		this.tileEntity = tileEntity;
	}
	
	@SideOnly(Side.CLIENT)
	public abstract IAttachmentRenderer getRenderer();
	
	public void update() {  }
	
	public boolean interact(EntityPlayer player) { return false; }
	
	public void attack(EntityPlayer player) {  }
	
	public ItemStack pick(EntityPlayer player) { return null; }
	
}
