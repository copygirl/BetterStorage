package net.mcft.copy.betterstorage.client.renderer;

import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.entity.EntityLivingBase;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderArmor extends RenderPlayer {
	
	public RenderArmor(RenderManager renderManager) {
		setRenderManager(renderManager);
	}
	
	@Override
	protected float handleRotationFloat(EntityLivingBase entity, float partialTicks) { return partialTicks; }
	
}
