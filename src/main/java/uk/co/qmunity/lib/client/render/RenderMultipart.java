package uk.co.qmunity.lib.client.render;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.IBlockAccess;
import org.lwjgl.opengl.GL11;
import uk.co.qmunity.lib.block.BlockMultipart;
import uk.co.qmunity.lib.model.IVertexConsumer;
import uk.co.qmunity.lib.part.IQLPart;
import uk.co.qmunity.lib.raytrace.QRayTraceResult;
import uk.co.qmunity.lib.tile.TileMultipart;
import uk.co.qmunity.lib.vec.Vector3;

public class RenderMultipart extends TileEntitySpecialRenderer implements IQLStaticRenderer {

    public static int pass = 0;
    public static int RENDER_ID;

    @Override
    public void renderTileEntityAt(TileEntity tile, double x, double y, double z, float partialTicks, int destroyStage) {
        TileMultipart te = (TileMultipart) tile;

        for (IQLPart p : te.getParts()) {
            if (p.getParent() != null) {
                GL11.glPushMatrix();
                p.renderDynamic(new Vector3(x, y, z), pass, partialTicks);
                GL11.glPopMatrix();
            }
        }
    }

    @Override
    public boolean renderStatic(IBlockAccess world, BlockPos position, RenderContext context, IVertexConsumer consumer) {

        TileMultipart te = BlockMultipart.findTile(world, position);
        if (te == null || te.getParts().isEmpty())
            return false;

        boolean rendered = false;
        for (IQLPart p : te.getParts())
            rendered |= p.renderStatic(context, consumer, pass);

        return rendered;
    }

    @Override
    public boolean renderBreaking(IBlockAccess world, BlockPos position, RenderContext context, IVertexConsumer consumer, TextureAtlasSprite overrideIcon) {

        TileMultipart te = BlockMultipart.findTile(world, position);
        if (te == null || te.getParts().isEmpty())
            return false;
        RayTraceResult mop = Minecraft.getMinecraft().objectMouseOver;
        if (mop != null && mop instanceof QRayTraceResult && ((QRayTraceResult) mop).part != null)
            return ((QRayTraceResult) mop).part.renderBreaking(context, consumer, (QRayTraceResult) mop, overrideIcon);
        return false;
    }
}
