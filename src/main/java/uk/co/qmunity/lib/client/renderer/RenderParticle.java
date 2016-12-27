package uk.co.qmunity.lib.client.renderer;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import uk.co.qmunity.lib.effect.EntityFXParticle;

@SideOnly(Side.CLIENT)
public class RenderParticle extends Render {

    public RenderParticle(){
        super(Minecraft.getMinecraft().getRenderManager());
    }

    protected RenderParticle(RenderManager renderManager) {
        super(renderManager);
    }

    @Override
    public void doRender(Entity entity, double x, double y, double z, float var8, float partialTickTime) {

        EntityFXParticle e = (EntityFXParticle) entity;

        e.particle.render(x, y, z, partialTickTime);
    }

    @Override
    protected ResourceLocation getEntityTexture(Entity var1) {

        return null;
    }

}
