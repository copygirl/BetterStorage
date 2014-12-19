package net.mcft.copy.betterstorage.client.model;

import net.mcft.copy.betterstorage.misc.Resources;
import net.minecraft.client.model.ModelBase;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ModelLocker extends ModelBase {
	
//	private IModelCustom model;
	
	protected ResourceLocation modelPath() { return Resources.modelLocker; }
	
	public ModelLocker() {
//		model = AdvancedModelLoader.loadModel(modelPath());
	}
	
	public void renderAll(boolean mirror, float doorAngle) {
		//TODO (1.8): Find a replacement, .obj converter? Or copy-pasting the old loader *shudder*
		/*model.renderPart("box");
		
		if (doorAngle > 0) {
			float rotatePointX = (mirror ? -7 : 7);
			float rotatePointZ = -7;
			if (mirror) doorAngle = -doorAngle;
			
			GlStateManager.translate(-rotatePointX, 0, -rotatePointZ);
			GlStateManager.rotate(-doorAngle, 0, 1, 0);
			GlStateManager.translate(rotatePointX, 0, rotatePointZ);
		}
		model.renderPart("door");
		
		if (mirror)
			GlStateManager.translate(-11, 0, 0);
		model.renderPart("handle");*/
		
	}
	
}
