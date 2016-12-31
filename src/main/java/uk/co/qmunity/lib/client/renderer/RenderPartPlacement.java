package uk.co.qmunity.lib.client.renderer;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;
import uk.co.qmunity.lib.client.render.RenderHelper;
import uk.co.qmunity.lib.item.ItemMultipart;
import uk.co.qmunity.lib.part.IPart;
import uk.co.qmunity.lib.part.IPartRenderPlacement;
import uk.co.qmunity.lib.part.compat.MultipartCompatibility;

@SideOnly(Side.CLIENT)
public class RenderPartPlacement {

    private Framebuffer fb = null;
    private int width = 0, height = 0;

    @SubscribeEvent
    public void onRenderOverlay(RenderGameOverlayEvent.Pre event) {

    }

    @SubscribeEvent
    public void onRenderTick(RenderWorldLastEvent event) {

        Minecraft mc = Minecraft.getMinecraft();
        World world = mc.world;
        EntityPlayer player = mc.player;
        ItemStack item = player.getHeldItem(EnumHand.MAIN_HAND);

        if (item == null)
            return;
        if (!(item.getItem() instanceof ItemMultipart))
            return;
        if (mc.gameSettings.hideGUI && mc.currentScreen == null)
            return;

        RayTraceResult mop = player.rayTrace(player.capabilities.isCreativeMode ? 5 : 4, 0);
        if (mop == null || mop.typeOfHit != RayTraceResult.Type.BLOCK)
            return;

        IPart part = ((ItemMultipart) item.getItem()).createPart(item, player, world, mop);

        if (part == null)
            return;
        if (!(part instanceof IPartRenderPlacement))
            return;

        EnumFacing faceHit = mop.sideHit;
        BlockPos location = mop.getBlockPos();

        if (!MultipartCompatibility.placePartInWorld(part, world, location, faceHit, player, item, true))
            return;

        if (fb == null || width != mc.displayWidth || height != mc.displayHeight) {
            width = mc.displayWidth;
            height = mc.displayHeight;
            fb = new Framebuffer(width, height, true);
        }

        GL11.glPushMatrix();
        {
            mc.getFramebuffer().unbindFramebuffer();
            GL11.glPushMatrix();
            {
                GL11.glLoadIdentity();
                fb.bindFramebuffer(true);

                GL11.glClear(GL11.GL_STENCIL_BUFFER_BIT | GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);

                GL11.glMatrixMode(GL11.GL_MODELVIEW);
                GL11.glLoadIdentity();
                GL11.glClearColor(0, 0, 0, 0);

                net.minecraft.client.renderer.RenderHelper.enableStandardItemLighting();

                GL11.glPushMatrix();
                {
                    BlockPos playerPos = player.getPosition();
                    BlockPos pos = part.getPos().subtract(playerPos);

                    GL11.glRotated(player.rotationPitch, 1, 0, 0);
                    GL11.glRotated(player.rotationYaw - 180, 0, 1, 0);

                    GL11.glTranslated(pos.getX(), pos.getY(), pos.getZ());

                    mc.renderEngine.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
                    GlStateManager.translate(-part.getPos().getX(), -part.getPos().getY(), -part.getPos().getZ());
                    Tessellator t = Tessellator.getInstance();
                    VertexBuffer vb =  t.getBuffer();
                    vb.begin(GL11.GL_QUADS, DefaultVertexFormats.BLOCK);
                    RenderHelper.instance.setRenderCoords(world, part.getPos().getX(),  part.getPos().getY(),  part.getPos().getZ());

                    if (part.shouldRenderOnPass(0))
                        part.renderStatic(part.getPos(), RenderHelper.instance, vb, 0);
                    if (part.shouldRenderOnPass(1))
                        part.renderStatic(part.getPos(), RenderHelper.instance, vb, 1);

                    RenderHelper.instance.reset();
                    t.draw();
                    GlStateManager.translate(part.getPos().getX(), part.getPos().getY(), part.getPos().getZ());

                    if (part.shouldRenderOnPass(0))
                        part.renderDynamic(new Vec3d(0, 0, 0), event.getPartialTicks(), 0);
                    if (part.shouldRenderOnPass(1))
                        part.renderDynamic(new Vec3d(0, 0, 0), event.getPartialTicks(), 1);
                }
                GL11.glPopMatrix();

                net.minecraft.client.renderer.RenderHelper.disableStandardItemLighting();

                fb.unbindFramebuffer();
            }
            GL11.glPopMatrix();

            mc.getFramebuffer().bindFramebuffer(true);
            GL11.glColor4d(1, 1, 1, 1);

            GL11.glPushMatrix();
            {
                ScaledResolution scaledresolution = new ScaledResolution(mc);
                GL11.glClear(GL11.GL_DEPTH_BUFFER_BIT);
                GL11.glMatrixMode(GL11.GL_PROJECTION);
                GL11.glLoadIdentity();
                GL11.glOrtho(0, scaledresolution.getScaledWidth_double(), scaledresolution.getScaledHeight_double(), 0, 0.1, 10000D);
                GL11.glMatrixMode(GL11.GL_MODELVIEW);
                GL11.glLoadIdentity();
                GL11.glTranslatef(0.0F, 0.0F, -2000.0F);

                fb.bindFramebufferTexture();
                {
                    GL11.glDisable(GL11.GL_LIGHTING);
                    GL11.glEnable(GL11.GL_BLEND);
                    GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

                    Tessellator tessellator = Tessellator.getInstance();
                    VertexBuffer vertexBuffer = tessellator.getBuffer();
                    int w = scaledresolution.getScaledWidth();
                    int h = scaledresolution.getScaledHeight();

                    vertexBuffer.begin(GL11.GL_QUADS, DefaultVertexFormats.BLOCK);
                    vertexBuffer.color(1, 1, 1, 0.75F);
                    vertexBuffer.pos(0.0D, 1.0D, 0.0D).tex(w, h).endVertex();
                    vertexBuffer.pos(0.0D, 1.0D, 1.0D).tex(w, 0);
                    vertexBuffer.pos(0.0D, 0.0D, 1.0D).tex(0, 0);
                    vertexBuffer.pos(0.0D, 0.0D, 0.0D).tex(0, h);
                    tessellator.draw();

                    GL11.glDisable(GL11.GL_BLEND);
                    GL11.glEnable(GL11.GL_LIGHTING);
                }
                fb.unbindFramebufferTexture();

                GL11.glDisable(GL11.GL_BLEND);
            }
            GL11.glPopMatrix();

            fb.framebufferClear();

            Minecraft.getMinecraft().getFramebuffer().bindFramebuffer(true);

            GL11.glColor4d(1, 1, 1, 1);
        }
        GL11.glPopMatrix();
    }
}
