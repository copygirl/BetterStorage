package net.mcft.copy.betterstorage.attachment;

import net.mcft.copy.betterstorage.utils.StackUtils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public abstract class ItemAttachment extends Attachment {
	
	protected ItemStack item = null;
	public float scale = 1.0F;
	public float scaleDepth = 1.0F;
	
	public ItemAttachment(TileEntity tileEntity) {
		super(tileEntity);
	}
	
	public void setScale(float scale, float scaleDepth) {
		this.scale = scale;
		this.scaleDepth = scaleDepth;
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public IAttachmentRenderer getRenderer() {
		return ItemAttachmentRenderer.instance;
	}
	
}
