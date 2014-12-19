package net.mcft.copy.betterstorage.client.renderer;

import net.mcft.copy.betterstorage.client.model.ModelPresent;
import net.mcft.copy.betterstorage.misc.Resources;
import net.mcft.copy.betterstorage.tile.entity.TileEntityPresent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MovingObjectPosition;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import org.lwjgl.opengl.GL11;

@SideOnly(Side.CLIENT)
public class TileEntityPresentRenderer extends TileEntitySpecialRenderer {

	private static final double NAMETAG_RENDER_RANGE = 3.0;
	private static final double NAMETAG_RENDER_RANGE_SQ = NAMETAG_RENDER_RANGE * NAMETAG_RENDER_RANGE;

	private ModelPresent model = new ModelPresent();

	@Override
	public void renderTileEntityAt(TileEntity entity, double x, double y, double z, float par8, int par9) {
		renderTileEntityAt((TileEntityPresent) entity, x, y, z, par8, par9);
	}

	public void renderTileEntityAt(TileEntityPresent present, double x, double y, double z, float partialTicks, int par9) {

		TextureManager texMan = Minecraft.getMinecraft().getTextureManager();

		GlStateManager.pushMatrix();
		GlStateManager.enableRescaleNormal();
		GlStateManager.translate(x + 0.5, y, z + 0.5);

		// TODO (1.8): This was probably used to make it load the terrain
		// texture.
		// texMan.bindTexture(texMan.getResourceLocation(0));

		renderSome(present.colorInner, 1);
		renderSome(present.colorOuter,
		          (present.skojanzaMode ? new int[] { 2, 3 } : new int[] { 2 }));

		GlStateManager.enableBlend();
		GlStateManager.depthFunc(GL11.GL_EQUAL);
		GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		texMan.bindTexture(Resources.texturePresentOverlay);
		model.render(1);

		GlStateManager.depthFunc(GL11.GL_EQUAL);
		GlStateManager.disableBlend();

		EntityPlayer player = Minecraft.getMinecraft().thePlayer;
		if ((present.nameTag != null) && (present.getWorld() != null)
			&& (player != null) && (player.getDistanceSqToCenter(present.getPos()) < NAMETAG_RENDER_RANGE_SQ)) {
			MovingObjectPosition o = Minecraft.getMinecraft().objectMouseOver;
			renderNameTag(present.nameTag,
				player.getName().equalsIgnoreCase(present.nameTag), o
				.func_178782_a().equals(present.getPos()));
		}

		GlStateManager.disableRescaleNormal();
		GlStateManager.popMatrix();

	}

	private void renderSome(int color, int... layers) {
		// TODO (1.8): Icons are dead. Find a replacement. Needle in a haystack, again.
		/*
		IIcon icon = ((color < 16) ? Blocks.wool.getIcon(0, color) :
		Blocks.gold_block.getIcon(0, 0));
		 
		GL11.glMatrixMode(GL11.GL_TEXTURE); GlStateManager.pushMatrix();
		 
		GlStateManager.translate(icon.getMinU(), icon.getMinV(), 0);
		GlStateManager.scale((icon.getMaxU() - icon.getMinU()),
		(icon.getMaxV() - icon.getMinV()), 1);
		
		for (int i = 0; i < layers.length; i++) model.render(layers[i]);
		
		GlStateManager.popMatrix(); GL11.glMatrixMode(GL11.GL_MODELVIEW);
		*/
	}

	private void renderNameTag(String owner, boolean isOwner, boolean lookingAt) {

		FontRenderer fontRen = Minecraft.getMinecraft().fontRendererObj;
		RenderManager renMan = Minecraft.getMinecraft().getRenderManager();

		int boxColor = 0;
		int boxAlpha = (lookingAt ? 128 : 48);
		int textColor = ((isOwner ? 0xFFFF55 : 0xAAAAAA) + (lookingAt ? 0xFF000000
		                : 0x80000000));
		
		float f1 = 1 / 85.0f;
		GlStateManager.pushMatrix();
		GlStateManager.translate(0.0F, 1.1F, 0.0F);
		// TODO (1.8): Not wrapped by the state manager, instead using the
		// Tessellator, see if it causes any issues.
		// GL11.glNormal3f(0.0F, 1.0F, 0.0F);
		GlStateManager.rotate(-renMan.playerViewY, 0.0F, 1.0F, 0.0F);
		GlStateManager.rotate(renMan.playerViewX, 1.0F, 0.0F, 0.0F);
		GlStateManager.scale(-f1, -f1, f1);

		GlStateManager.disableLighting();
		GlStateManager.depthMask(false);
		GlStateManager.enableBlend();
		GlStateManager.tryBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ZERO);
		GL11.glDisable(GL11.GL_TEXTURE_2D);

		WorldRenderer wr = Tessellator.getInstance().getWorldRenderer();
		wr.startDrawingQuads();
		int j = fontRen.getStringWidth(owner) / 2;
		wr.func_178974_a(boxColor, boxAlpha); // setColorRGBA_i
		wr.func_178980_d(0.0F, 1.0F, 0.0F); // setNormal
		wr.addVertex(-j - 2, -1, 0.0D);
		wr.addVertex(-j - 2,  9, 0.0D);
		wr.addVertex( j + 2,  9, 0.0D);
		wr.addVertex( j + 2, -1, 0.0D);
		Tessellator.getInstance().draw();

		GL11.glEnable(GL11.GL_TEXTURE_2D);
		GlStateManager.depthMask(true);
		fontRen.drawString(owner, -fontRen.getStringWidth(owner) / 2, 0, textColor);
		GlStateManager.enableLighting();
		GlStateManager.disableBlend();
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		GlStateManager.popMatrix();

	}

}
