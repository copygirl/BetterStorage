package net.mcft.copy.betterstorage.client.renderer;

import net.minecraft.client.renderer.entity.RenderEnderman;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderFrienderman extends RenderEnderman {
	
	public RenderFrienderman(RenderManager manager) {
		super(manager);
	}

	//TODO (1.8): Find a proper replacement. Not sure what this is doing in the first place.
	/*@Override
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
		GlStateManager.color(1.0F, 1.0F, 1.0F);
		
		return (stack.isItemEnchanted() ? 15 : 1);
		
	}*/
	
}
