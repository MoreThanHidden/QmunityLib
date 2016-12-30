package uk.co.qmunity.lib.part;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import uk.co.qmunity.lib.raytrace.QRayTraceResult;

/**
 * Interface implemented by parts that want to draw a custom selection box when their raytrace succeeds.
 *
 * @author amadornes
 */
public interface IPartSelectableCustom extends IPartSelectable {

    /**
     * Draws the custom selection box/es for the specified raytrace. Return false if you want QmunityLib to handle the rendering by itself.
     */
    @SideOnly(Side.CLIENT)
    public boolean drawHighlight(QRayTraceResult mop, EntityPlayer player, float frame);

}
