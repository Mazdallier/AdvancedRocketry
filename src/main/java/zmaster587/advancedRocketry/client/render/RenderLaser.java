package zmaster587.advancedRocketry.client.render;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;

public class RenderLaser extends Render {

	private static final ResourceLocation flare = new ResourceLocation("advancedrocketry", "textures/entity/Flare.png");


	@Override
	public void doRender(Entity entity, double x, double y, double z,
			float f, float f1) {

		GL11.glPushMatrix();
		GL11.glTranslated(x, y, z);
		Tessellator tessellator = Tessellator.instance;
		GL11.glDisable(GL11.GL_LIGHTING);
		GL11.glDisable(GL11.GL_FOG);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glDepthMask(false);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

		bindTexture(flare);

		tessellator.startDrawing(7);

		tessellator.setColorRGBA_F(1F, 0.25F, 0.25F, 0.2F);

		for(int i = 0; i < 4; i++) {
			tessellator.addVertexWithUV(-(i*6) - x, -y + 200, (i*6) - z, 0,1);
			tessellator.addVertexWithUV(-(i*6) - x, -y + 200, -(i*6) - z,0,0);
			tessellator.addVertexWithUV((i*6) - x, -y + 200, -(i*6) - z,1,0);
			tessellator.addVertexWithUV((i*6) - x, -y + 200, (i*6) - z,1,1);
		}

		tessellator.draw();

		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);

		tessellator.startDrawing(7);
		tessellator.setColorRGBA_F(0.9F, 0.2F, 0.3F, 0.5F);

		for(float radius = 0.25F; radius < 2; radius += .25F) {

			for(double i = 0; i < 2*Math.PI; i += Math.PI) {
				tessellator.addVertex(- x , -y + 200,  - z);
				tessellator.addVertex(- x, -y + 200, - z);
				tessellator.addVertex(- (radius* Math.cos(i)) + 0.5F, 0,- (radius* Math.sin(i)) + 0.5F);
				tessellator.addVertex(+ (radius* Math.sin(i)) + 0.5F, 0, (radius* Math.cos(i)) + 0.5F);
			}

			for(double i = 0; i < 2*Math.PI; i += Math.PI) {
				tessellator.addVertex(- x, -y + 200,- z);
				tessellator.addVertex(- x, -y + 200, - z);
				tessellator.addVertex(+ (radius* Math.sin(i)) + 0.5F, 0, -(radius* Math.cos(i)) + 0.5F);
				tessellator.addVertex(- (radius* Math.cos(i)) + 0.5F, 0,(radius* Math.sin(i)) + 0.5F);
			}
		}

		tessellator.draw();

		GL11.glDisable(GL11.GL_BLEND);
		GL11.glEnable(GL11.GL_LIGHTING);
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		GL11.glEnable(GL11.GL_FOG);
		GL11.glDepthMask(true);
		GL11.glPopMatrix();

	}

	@Override
	protected ResourceLocation getEntityTexture(Entity entity) {
		return null;
	}

}
