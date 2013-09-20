package net.mcft.copy.betterstorage.client.renderer;

import net.mcft.copy.betterstorage.block.tileentity.TileEntityBackpack;
import net.minecraft.item.ItemStack;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ItemRendererBackpack extends ItemRendererContainer {
	
	public static final ItemRendererBackpack instance = new ItemRendererBackpack();
	
	private ItemRendererBackpack() {
		super(TileEntityBackpack.class);
	}
	
	@Override
	public void renderItem(ItemRenderType type, ItemStack item, Object... data) {
		boolean entity = (type == ItemRenderType.ENTITY);
		boolean equippedFirstPerson = (type == ItemRenderType.EQUIPPED_FIRST_PERSON);
		boolean equippedThirdPerson = (type == ItemRenderType.EQUIPPED);
		boolean equipped = (equippedFirstPerson || equippedThirdPerson);
		if (entity || equippedThirdPerson)
			GL11.glScalef(1.2F, 1.2F, 1.2F);
		if (equipped) {
			GL11.glTranslatef(0.5F, 0.5F, 0.5F);
			GL11.glRotatef((equippedThirdPerson ? 200.0F : 75.0F), 0.0F, 1.0F, 0.0F);
		}
		super.renderItem(type, item, data);
	}
	
}
