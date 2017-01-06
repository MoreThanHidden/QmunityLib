package uk.co.qmunity.lib.tile;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import uk.co.qmunity.lib.network.MCByteBuf;
import uk.co.qmunity.lib.network.NetworkHandler;
import uk.co.qmunity.lib.network.annotation.DescSynced;
import uk.co.qmunity.lib.network.annotation.SyncNetworkUtils;
import uk.co.qmunity.lib.network.annotation.SyncedField;
import uk.co.qmunity.lib.network.packet.PacketCUpdateTile;
import uk.co.qmunity.lib.util.INBTSaveable;
import uk.co.qmunity.lib.util.ISyncable;
import uk.co.qmunity.lib.vec.IWorldLocation;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("rawtypes")
public class QLTileBase extends TileEntity implements IWorldLocation, ISyncable, INBTSaveable {

    /*
     * IWorldLocation
     */

    @Override
    public BlockPos getPos() {

        return pos;
    }

    @Override
    public World getWorld() {

        return world;
    }

    /*
     * ISyncable
     */

    @Override
    public void writeUpdateData(MCByteBuf buf) {

    }

    @Override
    public void readUpdateData(MCByteBuf buf) {

    }

    @Override
    public void sendUpdatePacket() {

        if (getWorld() != null)
            NetworkHandler.QLIB.sendToAllAround(new PacketCUpdateTile(this), getWorld());
    }

    /*
     * Synchronization bridge for description packets (so the client can get a description of the TE when it loads without requesting a packet).
     */

    @Nullable
    @Override
    public SPacketUpdateTileEntity getUpdatePacket() {
        NBTTagCompound tag = new NBTTagCompound();

        ByteBuf buf = Unpooled.buffer();
        writeUpdateData(new MCByteBuf(buf));
        tag.setByteArray("data", buf.array());

        return new SPacketUpdateTileEntity(getPos(), 2, tag);
    }

    @Override
    public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
        ByteBuf buf = Unpooled.copiedBuffer(pkt.getNbtCompound().getByteArray("data"));
        readUpdateData(new MCByteBuf(buf));
        markRender();
    }

    /*
     * Synchronization tags
     */

    private List<SyncedField> descriptionFields;

    public List<SyncedField> getDescriptionFields() {

        if (descriptionFields == null) {
            descriptionFields = SyncNetworkUtils.getSyncedFields(this, DescSynced.class);
            for (SyncedField field : descriptionFields) {
                field.update();
            }
        }
        return descriptionFields;
    }

    /*
     * Helper methods
     */

    public void notifyBlockChange() {

        if (getWorld() == null || getWorld().isRemote)
            return;
        getWorld().notifyNeighborsOfStateChange(getPos(), getBlockType(), false);
    }

    public void notifyTileChange() {

        if (getWorld() == null || getWorld().isRemote)
            return;
        getWorld().notifyNeighborsOfStateChange(getPos(), getBlockType(), false);
    }

    public void recalculateLighting() {

        if (getWorld() == null)
            return;
        getWorld().notifyLightSet(getPos());
    }

    public void markRender() {

        if (getWorld() == null || !getWorld().isRemote)
            return;
        getWorld().markBlockRangeForRenderUpdate(getPos(), getPos());
    }

    public boolean onActivated(EntityPlayer player, RayTraceResult mop, ItemStack stack) {

        return false;
    }

    public void onClicked(EntityPlayer player, ItemStack stack) {

    }

    public void onPlacedBy(EntityLivingBase entity, ItemStack stack) {

    }

    public void onNeighborChange(Block block) {

    }

    public void onNeighborTileChange(TileEntity tile) {

    }

    public void onFirstTick() {

    }

    public ArrayList<ItemStack> getDrops() {

        ArrayList<ItemStack> l = new ArrayList<ItemStack>();
        l.add(new ItemStack(getBlockType()));
        return l;
    }

    public boolean canConnectRedstone(EnumFacing side) {

        return false;
    }

    public int getStrongRedstoneOutput(EnumFacing side) {

        return 0;
    }

    public int getWeakRedstoneOutput(EnumFacing side) {

        return 0;
    }

    protected int ticker = 0;

    public void update() {
        if (ticker == 0)
            onFirstTick();
        ticker++;
    }

}
