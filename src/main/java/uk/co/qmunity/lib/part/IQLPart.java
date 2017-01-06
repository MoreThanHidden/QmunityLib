package uk.co.qmunity.lib.part;

import net.minecraft.client.particle.ParticleManager;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import uk.co.qmunity.lib.client.render.RenderContext;
import uk.co.qmunity.lib.model.IVertexConsumer;
import uk.co.qmunity.lib.network.MCByteBuf;
import uk.co.qmunity.lib.raytrace.QRayTraceResult;
import uk.co.qmunity.lib.util.ISyncable;
import uk.co.qmunity.lib.vec.Cuboid;
import uk.co.qmunity.lib.vec.IWorldLocation;
import uk.co.qmunity.lib.vec.Vector3;

import java.util.List;
import java.util.Random;

public interface IQLPart extends IWorldLocation, ISyncable {

    public String getType();

    // Parent

    public IPartHolder getParent();

    public void setParent(IPartHolder parent);

    // NBT

    public void writeToNBT(NBTTagCompound tag);

    public void readFromNBT(NBTTagCompound tag);

    // Update packet

    @Override
    public void writeUpdateData(MCByteBuf buf);

    @Override
    public void readUpdateData(MCByteBuf buf);

    @Override
    public void sendUpdatePacket();

    // Rendering

    @SideOnly(Side.CLIENT)
    public boolean renderStatic(RenderContext context, IVertexConsumer consumer, int pass);

    @SideOnly(Side.CLIENT)
    public void renderDynamic(Vector3 translation, int pass, float frame);

    @SideOnly(Side.CLIENT)
    public boolean renderBreaking(RenderContext context, IVertexConsumer consumer, QRayTraceResult hit, TextureAtlasSprite overrideIcon);

    public boolean drawHighlight(QRayTraceResult hit, EntityPlayer player, float partialTicks);

    @SideOnly(Side.CLIENT)
    public Cuboid getRenderBounds();

    // Misc rendering

    @SideOnly(Side.CLIENT)
    public void addDestroyEffects(QRayTraceResult hit, ParticleManager effectRenderer);

    @SideOnly(Side.CLIENT)
    public void addHitEffects(QRayTraceResult hit, ParticleManager effectRenderer);

    // Collision/selection

    public List<Cuboid> getCollisionBoxes();

    public List<Cuboid> getSelectionBoxes();

    public boolean occlusionTest(IQLPart part);

    public QRayTraceResult rayTrace(Vec3d start, Vec3d end);

    // Misc

    public int getLightValue();

    public ItemStack getPickBlock(EntityPlayer player, QRayTraceResult hit);

    public List<ItemStack> getDrops();

    public void harvest(EntityPlayer player, QRayTraceResult hit);

    public float getHardness(EntityPlayer player, QRayTraceResult hit);

    // Events

    public boolean onActivated(EntityPlayer player, QRayTraceResult hit, ItemStack item);

    public void onClicked(EntityPlayer player, QRayTraceResult hit, ItemStack item);

    public void update();

    @SideOnly(Side.CLIENT)
    public void randomDisplayTick(Random rnd);

    public void onPartChanged(IQLPart part);

    public void onNeighborBlockChange();

    public void onNeighborTileChange();

    public void onAdded();

    public void onRemoved();

    public void onLoaded();

    public void onUnloaded();// TODO: Implement

    public void onConverted();
}
