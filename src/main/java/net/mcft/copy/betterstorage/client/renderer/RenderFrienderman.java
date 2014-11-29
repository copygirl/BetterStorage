package net.mcft.copy.betterstorage.client.renderer;

import net.mcft.copy.betterstorage.misc.EquipmentSlot;
import net.mcft.copy.betterstorage.utils.RenderUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.entity.RenderBiped;
import net.minecraft.client.renderer.entity.RenderEnderman;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.ForgeHooksClient;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderFrienderman extends RenderEnderman {
	
	@Override
	protected int shouldRenderPass(EntityEnderman entity, int slot, float partialTicks) {
		
		if (slot == 0) {
			setRenderPassModel(mainModel);
			return super.shouldRenderPass(entity, slot, partialTicks);
		} else if (slot != 1) return -1;
		
		ItemStack stack = entity.getEquipmentInSlot(EquipmentSlot.CHEST);
		if (stack == null) return -1;
		
		Item item = stack.getItem();
		if (!(item instanceof ItemArmor)) return -1;
		
		ItemArmor itemarmor = (ItemArmor)item;
		TextureManager textureManager = Minecraft.getMinecraft().getTextureManager();
		textureManager.bindTexture(RenderBiped.getArmorResource(entity, stack, slot, null));
		
		ModelBiped modelBiped = ForgeHooksClient.getArmorModel(entity, stack, slot, null);
		setRenderPassModel(modelBiped);
		if (modelBiped != null) {
			modelBiped.onGround = mainModel.onGround;
			modelBiped.isRiding = mainModel.isRiding;
			modelBiped.isChild = mainModel.isChild;
		}
		
		int color = itemarmor.getColor(stack);
		if (color != -1) {
			RenderUtils.setColorFromInt(color);
			if (stack.isItemEnchanted()) return 31;
			return 16;
		}
		GL11.glColor3f(1.0F, 1.0F, 1.0F);
		
		return (stack.isItemEnchanted() ? 15 : 1);
		
	}
	
}
