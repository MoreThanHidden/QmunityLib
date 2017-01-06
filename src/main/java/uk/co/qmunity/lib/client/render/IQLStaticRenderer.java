package uk.co.qmunity.lib.client.render;

import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import uk.co.qmunity.lib.model.IVertexConsumer;

public interface IQLStaticRenderer {

    @SideOnly(Side.CLIENT)
    public boolean renderStatic(IBlockAccess world, BlockPos position, RenderContext context, IVertexConsumer consumer);

    @SideOnly(Side.CLIENT)
    public boolean renderBreaking(IBlockAccess world, BlockPos position, RenderContext context, IVertexConsumer consumer, TextureAtlasSprite overrideIcon);

}
