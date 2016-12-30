package uk.co.qmunity.lib.tile;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import uk.co.qmunity.lib.client.renderer.RenderMultipart;
import uk.co.qmunity.lib.part.*;
import uk.co.qmunity.lib.part.compat.OcclusionHelper;
import uk.co.qmunity.lib.part.compat.PartUpdateManager;
import uk.co.qmunity.lib.raytrace.QRayTraceResult;
import uk.co.qmunity.lib.raytrace.RayTracer;
import uk.co.qmunity.lib.util.QLog;
import uk.co.qmunity.lib.vec.Vec3dCube;

import javax.annotation.Nullable;
import java.io.*;
import java.util.*;

public class TileMultipart extends TileEntity implements ITilePartHolder, ITickable {

    private Map<String, IPart> parts = new HashMap<String, IPart>();

    private boolean shouldDieInAFire = false;
    private boolean loaded = false;

    private final boolean simulated;

    public TileMultipart(boolean simulated) {

        this.simulated = simulated;
    }

    public TileMultipart() {

        this(false);
    }

    @Override
    public World getWorld() {

        return getWorld();
    }

    @Override
    public List<IPart> getParts() {

        List<IPart> parts = new ArrayList<IPart>();

        for (String s : this.parts.keySet()) {
            IPart p = this.parts.get(s);
            if (p.getParent() != null)
                parts.add(p);
        }

        return parts;
    }

    @Override
    public boolean canAddPart(IPart part) {

        if (part instanceof IPartCollidable) {
            List<Vec3dCube> cubes = new ArrayList<Vec3dCube>();
            ((IPartCollidable) part).addCollisionBoxesToList(cubes, null);
            for (Vec3dCube c : cubes)
                if (!getWorld().checkNoEntityCollision(c.clone().add(getPos().getX(), getPos().getY(), getPos().getZ()).toAABB()))
                    return false;
        }

        return OcclusionHelper.occlusionTest(this, part);
    }

    @Override
    public void addPart(IPart part) {

        int before = parts.size();

        parts.put(genIdentifier(), part);
        part.setParent(this);

        if (!simulated) {
            if (part instanceof IPartUpdateListener)
                ((IPartUpdateListener) part).onAdded();
            for (IPart p : getParts())
                if (p != part && p instanceof IPartUpdateListener)
                    ((IPartUpdateListener) p).onPartChanged(part);

            if (before > 0)
                PartUpdateManager.addPart(this, part);

            markDirty();
            getWorld().markBlockRangeForRenderUpdate(getPos(), getPos());

            if (!getWorld().isRemote && before > 0)
                getWorld().updateObservingBlocksAt(getPos(), blockType);
        }
    }

    @Override
    public boolean removePart(IPart part) {

        if (part == null)
            return false;
        if (!parts.containsValue(part))
            return false;
        if (part.getParent() == null || part.getParent() != this)
            return false;

        if (!simulated) {
            PartUpdateManager.removePart(this, part);

            if (part instanceof IPartUpdateListener)
                ((IPartUpdateListener) part).onRemoved();
        }

        String id = getIdentifier(part);
        parts.remove(id);
        part.setParent(null);

        if (!simulated) {
            for (IPart p : getParts())
                if (p != part && p instanceof IPartUpdateListener)
                    ((IPartUpdateListener) p).onPartChanged(part);

            markDirty();
            getWorld().markBlockRangeForRenderUpdate(getPos(), getPos());

            if (!getWorld().isRemote)
                getWorld().updateObservingBlocksAt(getPos(), blockType);
        }

        return true;
    }

    private String genIdentifier() {

        String s = null;
        do {
            s = UUID.randomUUID().toString();
        } while (parts.containsKey(s));

        return s;
    }

    private String getIdentifier(IPart part) {

        for (String s : parts.keySet())
            if (parts.get(s).equals(part))
                return s;

        return null;
    }

    private IPart getPart(String id) {

        for (String s : parts.keySet())
            if (s.equals(id))
                return parts.get(s);

        return null;
    }

    public int getLightValue() {

        int val = 0;
        for (IPart p : getParts())
            val = Math.max(val, p.getLightValue());
        return val;
    }

    @Override
    public QRayTraceResult rayTrace(Vec3d start, Vec3d end) {

        QRayTraceResult closest = null;
        double dist = Double.MAX_VALUE;

        for (IPart p : getParts()) {
            if (p instanceof IPartSelectable) {
                QRayTraceResult mop = ((IPartSelectable) p).rayTrace(start, end);
                if (mop == null)
                    continue;
                double d = start.distanceTo(mop.hitVec);
                if (d < dist) {
                    closest = mop;
                    dist = d;
                }
            }
        }

        return closest;
    }

    // Saving/loading/syncing parts

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound tag) {

        super.writeToNBT(tag);

        NBTTagList l = new NBTTagList();
        writeParts(l, false);
        tag.setTag("parts", l);

        return tag;
    }

    @Override
    public void readFromNBT(NBTTagCompound tag) {

        super.readFromNBT(tag);

        NBTTagList l = tag.getTagList("parts", new NBTTagCompound().getId());
        readParts(l, false, false);
        loaded = true;

        if (getParts().size() == 0)
            shouldDieInAFire = true;
    }

    public void writeUpdateToNBT(NBTTagCompound tag) {

        NBTTagList l = new NBTTagList();
        writeParts(l, true);
        tag.setTag("parts", l);
    }

    public void readUpdateFromNBT(NBTTagCompound tag) {

        NBTTagList l = tag.getTagList("parts", new NBTTagCompound().getId());
        readParts(l, true, true);

        getWorld().markBlockRangeForRenderUpdate(getPos(), getPos());
    }

    private void writeParts(NBTTagList l, boolean update) {

        for (IPart p : getParts()) {
            String id = getIdentifier(p);

            NBTTagCompound tag = new NBTTagCompound();

            tag.setString("id", id);
            tag.setString("type", p.getType());
            NBTTagCompound data = new NBTTagCompound();
            if (update) {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                DataOutput buffer = new DataOutputStream(baos);
                try {
                    p.writeUpdateData(buffer, -1);
                    baos.flush();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                data.setByteArray("data", baos.toByteArray());
            } else {
                p.writeToNBT(data);
            }
            tag.setTag("data", data);

            l.appendTag(tag);
        }
    }

    private void readParts(NBTTagList l, boolean update, boolean client) {

        for (int i = 0; i < l.tagCount(); i++) {
            NBTTagCompound tag = l.getCompoundTagAt(i);

            String id = tag.getString("id");
            IPart p = getPart(id);
            if (p == null) {
                p = PartRegistry.createPart(tag.getString("type"), client);
                if (p == null)
                    continue;
                p.setParent(this);
                parts.put(id, p);
            }

            NBTTagCompound data = tag.getCompoundTag("data");
            if (update) {
                try {
                    p.readUpdateData(new DataInputStream(new ByteArrayInputStream(data.getByteArray("data"))), -1);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            } else {
                p.readFromNBT(data);
            }
        }
    }

    @Override
    public void sendUpdatePacket(IPart part, int channel) {

        if (getWorld() != null && getParts().contains(part))
            PartUpdateManager.sendPartUpdate(this, part, channel);
    }

    @Nullable
    @Override
    public SPacketUpdateTileEntity getUpdatePacket() {
        NBTTagCompound tag = new NBTTagCompound();
        writeUpdateToNBT(tag);
        return new SPacketUpdateTileEntity(getPos(), 0, tag);
    }

    @Override
    public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {

        readUpdateFromNBT(pkt.getNbtCompound());
    }

    public void removePart(EntityPlayer player) {

        QRayTraceResult mop = rayTrace(RayTracer.getStartVector(player), RayTracer.getEndVector(player));
        if (mop != null)
            if (mop.getPart().breakAndDrop(player, mop))
                mop.getPart().getParent().removePart(mop.getPart());
    }

    @Override
    public void addCollisionBoxesToList(List<Vec3dCube> l, AxisAlignedBB bounds, Entity entity) {

        List<Vec3dCube> boxes = new ArrayList<Vec3dCube>();

        for (IPart p : getParts()) {
            if (p instanceof IPartCollidable) {
                List<Vec3dCube> boxes_ = new ArrayList<Vec3dCube>();
                ((IPartCollidable) p).addCollisionBoxesToList(boxes_, entity);
                for (Vec3dCube c : boxes_) {
                    Vec3dCube cube = c.clone();
                    cube.add(getPos().getX(), getPos().getY(), getPos().getZ());
                    cube.setPart(p);
                    boxes.add(cube);
                }
                boxes_.clear();
            }
        }

        for (Vec3dCube c : boxes) {
            if (c.toAABB().intersectsWith(bounds))
                l.add(c);
        }
    }

    public void onNeighborBlockChange() {

        if (simulated)
            return;

        onUpdate();

        for (IPart p : getParts())
            if (p instanceof IPartUpdateListener)
                ((IPartUpdateListener) p).onNeighborBlockChange();

        onUpdate();
    }

    public void onNeighborChange() {

        if (simulated)
            return;

        onUpdate();

        for (IPart p : getParts())
            if (p instanceof IPartUpdateListener)
                ((IPartUpdateListener) p).onNeighborTileChange();

        onUpdate();
    }

    private void onUpdate() {

        if (simulated)
            return;

        if (!getWorld().isRemote) {
            if (getParts().size() == 0)
                getWorld().setBlockToAir(getPos());
        }
    }

    public boolean isSideSolid(EnumFacing face) {

        for (IPart p : getParts())
            if (p instanceof IPartSolid)
                if (((IPartSolid) p).isSideSolid(face))
                    return true;

        return false;
    }

    private boolean firstTick = true;

    @Override
    public void update() {

        if (firstTick && loaded) {
            for (IPart p : getParts()) {
                if (p instanceof IPartUpdateListener)
                    ((IPartUpdateListener) p).onLoaded();
            }
            firstTick = false;
        }
        for (IPart p : getParts())
            if (p instanceof IPartTicking)
                ((IPartTicking) p).update();

        if(parts.size() > 100) {
            QLog.error("A Qmunitylib part has " + parts.size() + " parts! It has been removed. Dimension: " + getWorld().provider.getDimension() + ", location: " + getPos().toString());
            shouldDieInAFire = true;
        }
        
        if (shouldDieInAFire)
            getWorld().setBlockToAir(getPos());
    }

    public List<Vec3dCube> getOcclusionBoxes() {

        List<Vec3dCube> boxes = new ArrayList<Vec3dCube>();

        for (IPart p : getParts())
            if (p instanceof IPartOccluding)
                boxes.addAll(((IPartOccluding) p).getOcclusionBoxes());

        return boxes;
    }

    public int getStrongOutput(EnumFacing direction, EnumFacing face) {

        int max = 0;

        for (IPart p : getParts()) {
            if (p instanceof IPartRedstone) {
                if (p instanceof IPartFace) {
                    if (((IPartFace) p).getFace() == face)
                        max = Math.max(max, ((IPartRedstone) p).getStrongPower(direction));
                } else {
                    max = Math.max(max, ((IPartRedstone) p).getStrongPower(direction));
                }
            }
        }

        return max;
    }

    public int getStrongOutput(EnumFacing direction) {

        int max = 0;

        for (EnumFacing face : EnumFacing.VALUES)
            max = Math.max(max, getStrongOutput(direction, face));

        return max;
    }

    public int getWeakOutput(EnumFacing direction, EnumFacing face) {

        int max = 0;

        for (IPart p : getParts()) {
            if (p instanceof IPartRedstone) {
                if (p instanceof IPartFace) {
                    if (((IPartFace) p).getFace() == face)
                        max = Math.max(max, ((IPartRedstone) p).getWeakPower(direction));
                } else {
                    max = Math.max(max, ((IPartRedstone) p).getWeakPower(direction));
                }
            }
        }

        return max;
    }

    public int getWeakOutput(EnumFacing direction) {

        int max = 0;

        for (EnumFacing face : EnumFacing.VALUES)
            max = Math.max(max, getWeakOutput(direction, face));

        return max;
    }

    public boolean canConnect(EnumFacing direction, EnumFacing face) {

        for (IPart p : getParts()) {
            if (p instanceof IPartRedstone) {
                if (p instanceof IPartFace) {
                    if (((IPartFace) p).getFace() == face)
                        if (((IPartRedstone) p).canConnectRedstone(direction))
                            return true;
                } else {
                    if (((IPartRedstone) p).canConnectRedstone(direction))
                        return true;
                }
            }
        }

        return false;
    }

    public boolean canConnect(EnumFacing direction) {

        for (EnumFacing face : EnumFacing.VALUES)
            if (canConnect(direction, face))
                return true;

        return false;
    }

    @Override
    public void onChunkUnload() {

        if (simulated)
            return;

        for (IPart p : getParts())
            if (p instanceof IPartUpdateListener)
                ((IPartUpdateListener) p).onUnloaded();
    }

    @Override
    @SideOnly(Side.CLIENT)
    public AxisAlignedBB getRenderBoundingBox() {

        return new AxisAlignedBB(getPos(), getPos().add(1,1,1));
    }

    public ItemStack pickUp(EntityPlayer player) {

        QRayTraceResult mop = rayTrace(RayTracer.getStartVector(player), RayTracer.getEndVector(player));
        if (mop != null) {
            return mop.getPart().getPickedItem(mop);
        }

        return null;
    }

    @Override
    public Map<String, IPart> getPartMap() {

        return parts;
    }

    public void onClicked(EntityPlayer player) {

        QRayTraceResult mop = rayTrace(RayTracer.getStartVector(player), RayTracer.getEndVector(player));
        if (mop != null)
            if (mop.getPart() instanceof IPartInteractable)
                ((IPartInteractable) mop.getPart()).onClicked(player, mop, player.getHeldItem(EnumHand.MAIN_HAND));
    }

    public boolean onActivated(EntityPlayer player) {

        QRayTraceResult mop = rayTrace(RayTracer.getStartVector(player), RayTracer.getEndVector(player));
        if (mop != null)
            if (mop.getPart() instanceof IPartInteractable)
                return ((IPartInteractable) mop.getPart()).onActivated(player, mop, player.getHeldItem(EnumHand.MAIN_HAND));

        return false;
    }

    @Override
    public List<IMicroblock> getMicroblocks() {

        List<IMicroblock> microblocks = new ArrayList<IMicroblock>();

        for (IPart p : getParts())
            if (p instanceof IMicroblock)
                microblocks.add((IMicroblock) p);

        return microblocks;
    }

    @Override
    public boolean isSimulated() {

        return simulated;
    }

    @Override
    public boolean shouldRenderInPass(int pass) {

        RenderMultipart.pass = pass;
        return true;
    }

}