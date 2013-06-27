package net.mcft.copy.betterstorage.attachment;

import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemAttachment extends Attachment {
	
	public final ItemStack item;
	public float scale = 1.0F;
	public float scaleDepth = 1.0F;
	
	public ItemAttachment(TileEntity tileEntity, ItemStack item) {
		super(tileEntity);
		this.item = item;
	}
	
	public ItemAttachment setScale(float scale, float scaleDepth) {
		this.scale = scale;
		this.scaleDepth = scaleDepth;
		return this;
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public IAttachmentRenderer getRenderer() {
		return ItemAttachmentRenderer.instance;
	}
	
}
