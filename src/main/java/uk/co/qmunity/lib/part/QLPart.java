package uk.co.qmunity.lib.part;

import net.minecraft.client.particle.ParticleManager;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import uk.co.qmunity.lib.client.render.RenderContext;
import uk.co.qmunity.lib.helper.ItemHelper;
import uk.co.qmunity.lib.model.IVertexConsumer;
import uk.co.qmunity.lib.network.MCByteBuf;
import uk.co.qmunity.lib.network.packet.PacketCPart;
import uk.co.qmunity.lib.raytrace.QRayTraceResult;
import uk.co.qmunity.lib.raytrace.RayTracer;
import uk.co.qmunity.lib.vec.Cuboid;
import uk.co.qmunity.lib.vec.Vector3;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

public abstract class QLPart implements IQLPart {

    private IPartHolder parent;

    @Override
    public World getWorld() {

        return getParent() != null ? getParent().getWorld() : null;
    }

    @Override
    public BlockPos getPos() {

        return getParent() != null ? getParent().getPos() : new BlockPos(0,0,0);
    }

    @Override
    public IPartHolder getParent() {

        return parent;
    }

    @Override
    public void setParent(IPartHolder parent) {

        this.parent = parent;
    }

    @Override
    public void writeToNBT(NBTTagCompound tag) {

    }

    @Override
    public void readFromNBT(NBTTagCompound tag) {

    }

    @Override
    public void writeUpdateData(MCByteBuf buffer) {

    }

    @Override
    public void readUpdateData(MCByteBuf buffer) {

    }

    @Override
    public void sendUpdatePacket() {

        if (getWorld() != null && !getWorld().isRemote)
            PacketCPart.updatePart(getParent(), this);
    }

    @Override
    public boolean renderStatic(RenderContext context, IVertexConsumer consumer, int pass) {

        return false;
    }

    @Override
    public void renderDynamic(Vector3 translation, int pass, float frame) {

    }

    @Override
    public boolean renderBreaking(RenderContext context, IVertexConsumer consumer, QRayTraceResult hit, TextureAtlasSprite overrideIcon) {

        return renderStatic(context, consumer, 0) || renderStatic(context, consumer, 1);
    }

    @Override
    public boolean drawHighlight(QRayTraceResult hit, EntityPlayer player, float partialTicks) {

        return false;
    }

    @Override
    public Cuboid getRenderBounds() {

        return Cuboid.full;
    }

    @Override
    public void addDestroyEffects(QRayTraceResult hit, ParticleManager effectRenderer) {

    }

    @Override
    public void addHitEffects(QRayTraceResult hit, ParticleManager effectRenderer) {

    }

    @Override
    public List<Cuboid> getCollisionBoxes() {

        return Arrays.asList();
    }

    @Override
    public List<Cuboid> getSelectionBoxes() {

        return Arrays.asList();
    }

    @Override
    public boolean occlusionTest(IQLPart part) {

        return true;
    }

    @Override
    public QRayTraceResult rayTrace(Vec3d start, Vec3d end) {

        return RayTracer.instance().rayTracePart(this, new Vector3(start), new Vector3(end));
    }

    @Override
    public int getLightValue() {

        return 0;
    }

    @Override
    public ItemStack getPickBlock(EntityPlayer player, QRayTraceResult hit) {

        return null;
    }

    @Override
    public List<ItemStack> getDrops() {

        return Arrays.asList(new ItemStack(Item.REGISTRY.getObject(new ResourceLocation("qltest", "testpartql"))));
    }

    @Override
    public void harvest(EntityPlayer player, QRayTraceResult hit) {

        if (player == null || !player.capabilities.isCreativeMode)
            for (ItemStack item : getDrops())
                ItemHelper.dropItem(getWorld(), getPos(), item);
        getParent().removePart(this);
    }

    @Override
    public float getHardness(EntityPlayer player, QRayTraceResult hit) {

        return 10;
    }

    @Override
    public boolean onActivated(EntityPlayer player, QRayTraceResult hit, ItemStack item) {

        return false;
    }

    @Override
    public void onClicked(EntityPlayer player, QRayTraceResult hit, ItemStack item) {

    }

    @Override
    public void update() {

    }

    @Override
    public void randomDisplayTick(Random rnd) {

    }

    @Override
    public void onPartChanged(IQLPart part) {

    }

    @Override
    public void onNeighborBlockChange() {

    }

    @Override
    public void onNeighborTileChange() {

    }

    @Override
    public void onAdded() {

    }

    @Override
    public void onRemoved() {

    }

    @Override
    public void onLoaded() {

    }

    @Override
    public void onUnloaded() {

    }

    @Override
    public void onConverted() {

    }

    public void notifyBlockChange() {

        if (getParent() != null)
            getParent().notifyBlockChange();
    }

    public void notifyTileChange() {

        if (getParent() != null)
            getParent().notifyTileChange();
    }

    public void markDirty() {

        if (getParent() != null)
            getParent().markDirty();
    }

    public void recalculateLighting() {

        if (getParent() != null)
            getParent().recalculateLighting();
    }

    public void markRender() {

        if (getParent() != null)
            getParent().markRender();
    }

    public static class QLPartNormallyOccluded extends QLPart implements IOccludingPart {

        private List<Cuboid> boxes;

        public QLPartNormallyOccluded(List<Cuboid> boxes) {

            this.boxes = boxes;
        }

        @Override
        public String getType() {

            return "normally_occluded_qlpart";
        }

        @Override
        public List<Cuboid> getOcclusionBoxes() {

            return boxes;
        }

        @Override
        public boolean occlusionTest(IQLPart part) {

            if (!(part instanceof IOccludingPart))
                return true;

            List<Cuboid> self = getOcclusionBoxes();
            List<Cuboid> p = ((IOccludingPart) part).getOcclusionBoxes();
            for (Cuboid c1 : self)
                for (Cuboid c2 : p)
                    if (c1.intersects(c2))
                        return false;
            return true;
        }
    }

}
