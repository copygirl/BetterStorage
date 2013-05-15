package net.mcft.copy.betterstorage.client.renderer;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.mcft.copy.betterstorage.utils.RenderUtils;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.entity.RenderBiped;
import net.minecraft.client.renderer.entity.RenderEnderman;
import net.minecraft.entity.EntityLiving;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.ForgeHooksClient;

@SideOnly(Side.CLIENT)
public class RenderFrienderman extends RenderEnderman {
	
	@Override
	protected int shouldRenderPass(EntityLiving entity, int slot, float partialTicks) {
		
		if (slot == 0) {
			setRenderPassModel(mainModel);
			return super.shouldRenderPass(entity, slot, partialTicks);
		} else if (slot != 1) return -1;
		
		ItemStack stack = entity.getCurrentArmor(2);
		if (stack == null) return -1;
		
		Item item = stack.getItem();
		if (!(item instanceof ItemArmor)) return -1;
		
		ItemArmor itemarmor = (ItemArmor)item;
		String _default = "/armor/" + RenderBiped.bipedArmorFilenamePrefix[itemarmor.renderIndex] + "_" + (slot == 2 ? 2 : 1) + ".png";
		loadTexture(ForgeHooksClient.getArmorTexture(entity, stack, _default, slot, 1));
		
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
