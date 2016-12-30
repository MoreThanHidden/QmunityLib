package uk.co.qmunity.lib.part;

import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import uk.co.qmunity.lib.helper.ItemHelper;
import uk.co.qmunity.lib.part.compat.OcclusionHelper;
import uk.co.qmunity.lib.raytrace.QRayTraceResult;
import uk.co.qmunity.lib.vec.Vec3dCube;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
/**
 * Basic implementation of the {@link IPart} interface.
 *
 * @author amadornes
 */
public abstract class PartBase implements IPart {

    private ITilePartHolder parent;

    @Override
    public World getWorld() {

        return getParent().getWorld();
    }

    @Override
    public BlockPos getPos() {

        return getParent().getPos();
    }

    @Override
    public ITilePartHolder getParent() {

        return parent;
    }

    @Override
    public void setParent(ITilePartHolder parent) {

        this.parent = parent;
    }

    @Override
    public void readFromNBT(NBTTagCompound tag) {

    }

    @Override
    public void writeToNBT(NBTTagCompound tag) {

    }

    @Override
    public void writeUpdateData(DataOutput buffer, int channel) throws IOException {

        if (channel == -1)
            writeUpdateData(buffer);
    }

    @Override
    public void readUpdateData(DataInput buffer, int channel) throws IOException {

        if (channel == -1)
            readUpdateData(buffer);
    }

    @Override
    public void sendUpdatePacket(int channel) {

        if (parent != null && getWorld() != null)
            parent.sendUpdatePacket(this, channel);
    }

    public void writeUpdateData(DataOutput buffer) throws IOException {

    }

    public void readUpdateData(DataInput buffer) throws IOException {

    }

    public void sendUpdatePacket() {

        sendUpdatePacket(-1);
    }

    @Override
    public ItemStack getPickedItem(QRayTraceResult mop) {

        return getItem();
    }

    @Override
    public List<ItemStack> getDrops() {

        List<ItemStack> items = new ArrayList<ItemStack>();

        ItemStack is = getItem();
        if (is != null) {
            is.setCount(1);
            items.add(is);
        }

        return items;
    }

    @Override
    public boolean breakAndDrop(EntityPlayer player, QRayTraceResult mop) {

        List<ItemStack> drops = getDrops();
        if ((player == null || !player.capabilities.isCreativeMode) && drops != null && drops.size() > 0)
            for (ItemStack item : drops)
                ItemHelper.dropItem(getWorld(), getPos(), item);

        return true;
    }

    @Override
    public double getHardness(EntityPlayer player, QRayTraceResult mop) {

        return 0.1;
    }

    @Override
    public int getLightValue() {

        return 0;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean renderBreaking(Vec3i translation, VertexBuffer renderer, int pass, QRayTraceResult mop) {

        return false;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean renderStatic(Vec3i translation, VertexBuffer renderer, int pass) {

        return false;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void renderDynamic(Vec3d translation, double delta, int pass) {

    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean shouldRenderOnPass(int pass) {

        return pass == 0;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public Vec3dCube getRenderBounds() {

        return new Vec3dCube(0, 0, 0, 1, 1, 1);
    }

    @Override
    public boolean occlusionTest(IPart part) {

        return OcclusionHelper.occlusionTest(this, part);
    }

}
