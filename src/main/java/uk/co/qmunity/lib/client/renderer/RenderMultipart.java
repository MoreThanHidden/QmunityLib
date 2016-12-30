package uk.co.qmunity.lib.client.renderer;

import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;
import uk.co.qmunity.lib.part.IPart;
import uk.co.qmunity.lib.tile.TileMultipart;

@SideOnly(Side.CLIENT)
public class RenderMultipart extends TileEntitySpecialRenderer{

    public static int pass = 0;

    @Override
    public void renderTileEntityAt(TileEntity tile, double x, double y, double z, float partialTicks, int destroyStage) {
        TileMultipart te = (TileMultipart) tile;

        GL11.glPushMatrix();
        {
            GL11.glTranslated(x, y, z);
            for (IPart p : te.getParts()) {
                if (p.getParent() != null) {
                    GL11.glPushMatrix();

                    p.renderDynamic(new Vec3d(0, 0, 0), partialTicks, pass);

                    GL11.glPopMatrix();
                }
            }
        }
        GL11.glPopMatrix();
    }


}
