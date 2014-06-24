package net.mcft.copy.betterstorage.client.renderer;

import net.mcft.copy.betterstorage.item.ItemBackpack;
import net.mcft.copy.betterstorage.proxy.ClientProxy;
import net.mcft.copy.betterstorage.tile.entity.TileEntityContainer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.IItemRenderer;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ItemRendererBackpack implements IItemRenderer {
	
	private final Class<? extends TileEntityContainer> tileEntityClass;
	private BetterStorageRenderingHandler renderingHandler = null;
	
	public ItemRendererBackpack(Class<? extends TileEntityContainer> tileEntityClass) {
		this.tileEntityClass = tileEntityClass;
	}
	
	@Override
	public void renderItem(ItemRenderType type, ItemStack item, Object... data) {
		boolean entity = (type == ItemRenderType.ENTITY);
		boolean equippedFirstPerson = (type == ItemRenderType.EQUIPPED_FIRST_PERSON);
		boolean equippedThirdPerson = (type == ItemRenderType.EQUIPPED);
		boolean equipped = (equippedFirstPerson || equippedThirdPerson);
		if (equipped) {
			if (equippedThirdPerson) GL11.glTranslatef(1.3F, 0.0F, 1.0F);
			else GL11.glTranslatef(0.0F, 0.0F, 0.85F);
			GL11.glRotatef((equippedThirdPerson ? 200.0F : 75.0F), 0.0F, 1.0F, 0.0F);
		}
		if (equippedThirdPerson)
			GL11.glScalef(1.2F, 1.2F, 1.2F);
		else if (entity)
			GL11.glScalef(0.75F, 0.75F, 0.75F);
		
		if (renderingHandler == null)
			renderingHandler = ClientProxy.renderingHandlers.get(tileEntityClass);
		((TileEntityContainer)renderingHandler.tileEntity).onBlockRenderAsItem(item);
		GL11.glPushMatrix();
		if ((type == ItemRenderType.EQUIPPED) ||
		    (type == ItemRenderType.EQUIPPED_FIRST_PERSON))
			GL11.glTranslatef(0.5F, 0.5F, 0.5F);
		renderingHandler.renderInventoryBlock(((ItemBackpack)item.getItem()).getBlockType(), 0, 0, null);
		GL11.glPopMatrix();
	}

	@Override
	public boolean handleRenderType(ItemStack arg0, ItemRenderType arg1) {
		return true;
	}

	@Override
	public boolean shouldUseRenderHelper(ItemRenderType arg0, ItemStack arg1,
			ItemRendererHelper arg2) {
		return true;
	}
	
}
