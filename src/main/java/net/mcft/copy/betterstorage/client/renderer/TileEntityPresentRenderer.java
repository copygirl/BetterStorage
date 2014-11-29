package net.mcft.copy.betterstorage.client.renderer;

import net.mcft.copy.betterstorage.client.model.ModelPresent;
import net.mcft.copy.betterstorage.misc.Resources;
import net.mcft.copy.betterstorage.tile.entity.TileEntityPresent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.util.MovingObjectPosition;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class TileEntityPresentRenderer extends TileEntitySpecialRenderer {
	
	private static final double NAMETAG_RENDER_RANGE = 3.0;
	private static final double NAMETAG_RENDER_RANGE_SQ = NAMETAG_RENDER_RANGE * NAMETAG_RENDER_RANGE;
	
	private ModelPresent model = new ModelPresent();
	
	@Override
	public void renderTileEntityAt(TileEntity entity, double x, double y, double z, float par8) {
		renderTileEntityAt((TileEntityPresent)entity, x, y, z, par8);
	}
	public void renderTileEntityAt(TileEntityPresent present, double x, double y, double z, float partialTicks) {
		
		TextureManager texMan = Minecraft.getMinecraft().getTextureManager();
		
		GL11.glPushMatrix();
		GL11.glEnable(GL12.GL_RESCALE_NORMAL);
		GL11.glTranslated(x + 0.5, y, z + 0.5);
		
		texMan.bindTexture(texMan.getResourceLocation(0));
		
		renderSome(present.colorInner, 1);
		renderSome(present.colorOuter, (present.skojanzaMode
				? new int[]{ 2, 3 } : new int[]{ 2 }));
		
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glDepthFunc(GL11.GL_EQUAL);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		texMan.bindTexture(Resources.texturePresentOverlay);
		model.render(1);
		
		GL11.glDepthFunc(GL11.GL_LEQUAL);
		GL11.glDisable(GL11.GL_BLEND);
		
		EntityPlayer player = Minecraft.getMinecraft().thePlayer;
		if ((present.nameTag != null) && (present.getWorldObj() != null) && (player != null) &&
		    (player.getDistanceSq(present.xCoord + 0.5, present.yCoord + 0.5, present.zCoord + 0.5) < NAMETAG_RENDER_RANGE_SQ)) {
			MovingObjectPosition o = Minecraft.getMinecraft().objectMouseOver;
			renderNameTag(present.nameTag, player.getCommandSenderName().equalsIgnoreCase(present.nameTag),
			              ((o.blockX == present.xCoord) && (o.blockY == present.yCoord) && (o.blockZ == present.zCoord)));
		}
		
		GL11.glDisable(GL12.GL_RESCALE_NORMAL);
		GL11.glPopMatrix();
		
	}
	
	private void renderSome(int color, int... layers) {
		IIcon icon = ((color < 16) ? Blocks.wool.getIcon(0, color) : Blocks.gold_block.getIcon(0, 0));
		
		GL11.glMatrixMode(GL11.GL_TEXTURE);
		GL11.glPushMatrix();
		
		GL11.glTranslatef(icon.getMinU(), icon.getMinV(), 0);
		GL11.glScalef((icon.getMaxU() - icon.getMinU()), (icon.getMaxV() - icon.getMinV()), 1);
		
		for (int i = 0; i < layers.length; i++)
			model.render(layers[i]);
		
		GL11.glPopMatrix();
		GL11.glMatrixMode(GL11.GL_MODELVIEW);
	}
	
	private void renderNameTag(String owner, boolean isOwner, boolean lookingAt) {
		
		FontRenderer fontRen = Minecraft.getMinecraft().fontRenderer;
		RenderManager renMan = RenderManager.instance;
		
		int boxColor = 0;
		int boxAlpha = (lookingAt ? 128 : 48);
		int textColor = ((isOwner ? 0xFFFF55 : 0xAAAAAA) +
		                 (lookingAt ? 0xFF000000 : 0x80000000));
		
        float f1 = 1 / 85.0f;
        GL11.glPushMatrix();
        GL11.glTranslatef(0.0F, 1.1F, 0.0F);
        GL11.glNormal3f(0.0F, 1.0F, 0.0F);
        GL11.glRotatef(-renMan.playerViewY, 0.0F, 1.0F, 0.0F);
        GL11.glRotatef( renMan.playerViewX, 1.0F, 0.0F, 0.0F);
        GL11.glScalef(-f1, -f1, f1);
        
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glDepthMask(false);
        GL11.glEnable(GL11.GL_BLEND);
        OpenGlHelper.glBlendFunc(770, 771, 1, 0);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        
        Tessellator tessellator = Tessellator.instance;
        tessellator.startDrawingQuads();
        int j = fontRen.getStringWidth(owner) / 2;
        tessellator.setColorRGBA_I(boxColor, boxAlpha);
        tessellator.addVertex(-j - 2, -1, 0.0D);
        tessellator.addVertex(-j - 2,  9, 0.0D);
        tessellator.addVertex( j + 2,  9, 0.0D);
        tessellator.addVertex( j + 2, -1, 0.0D);
        tessellator.draw();
        
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glDepthMask(true);
        fontRen.drawString(owner, -fontRen.getStringWidth(owner) / 2, 0, textColor);
        GL11.glEnable(GL11.GL_LIGHTING);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        GL11.glPopMatrix();
		
	}
	
}
