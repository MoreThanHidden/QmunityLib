package uk.co.qmunity.lib.client.render;

import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import uk.co.qmunity.lib.model.IVertexConsumer;

public interface IQLItemRenderer {

    @SideOnly(Side.CLIENT)
    public void renderItem(ItemStack stack, ItemRenderer type, RenderContext context, IVertexConsumer consumer);

}
