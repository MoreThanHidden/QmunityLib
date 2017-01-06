package uk.co.qmunity.lib.client.render;

import net.minecraftforge.client.event.DrawBlockHighlightEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import uk.co.qmunity.lib.model.IVertexConsumer;

public interface IQLPlacementRenderer {

    @SideOnly(Side.CLIENT)
    public void renderPlacement(DrawBlockHighlightEvent event, RenderContext context, IVertexConsumer consumer);

}
