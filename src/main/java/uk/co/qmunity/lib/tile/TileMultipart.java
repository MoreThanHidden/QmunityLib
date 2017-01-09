package uk.co.qmunity.lib.tile;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import uk.co.qmunity.lib.client.render.RenderMultipart;
import uk.co.qmunity.lib.network.MCByteBuf;
import uk.co.qmunity.lib.network.packet.PacketCPart;
import uk.co.qmunity.lib.part.*;
import uk.co.qmunity.lib.raytrace.QRayTraceResult;
import uk.co.qmunity.lib.vec.Cuboid;
import uk.co.qmunity.lib.vec.Vector3;

import java.util.*;
import java.util.Map.Entry;

public class TileMultipart extends QLTileBase implements IPartHolder {

    public Map<String, IQLPart> parts = new HashMap<String, IQLPart>();
    private Map<Integer, ISlottedPart> slotMap = new HashMap<Integer, ISlottedPart>();

    @Override
    public World getWorld() {

        return world;
    }

    @Override
    public BlockPos getPos(){
        return pos;
    }

    @Override
    public boolean canAddPart(IQLPart part) {

        if (part instanceof ISlottedPart) {
            int slotMask = ((ISlottedPart) part).getSlotMask();
            for (PartSlot s : PartSlot.values())
                if ((slotMask & s.mask) != 0 && getPartInSlot(s.ordinal()) != null)
                    return false;
        }

        for (IQLPart p : getParts())
            if (!p.occlusionTest(part))
                return false;

        return true;
    }

    @Override
    public void addPart(IQLPart part) {

        String partID;
        do {
            partID = UUID.randomUUID().toString();
        } while (parts.containsKey(partID));
        addPart(partID, part, true);
    }

    @Override
    public void addPart(String partID, IQLPart part, boolean notify) {

        // Set parent
        part.setParent(this);

        // Add to the part map
        parts.put(partID, part);
        // Add to the slot map (if needed)
        if (part instanceof ISlottedPart) {
            int slotMask = ((ISlottedPart) part).getSlotMask();
            for (PartSlot s : PartSlot.values())
                if ((slotMask & s.mask) != 0)
                    slotMap.put(s.ordinal(), (ISlottedPart) part);
        }

        // If we don't want to notify anybody, stop here
        if (!notify)
            return;

        // Notify part
        part.onAdded();
        // Notify other parts
        for (IQLPart p : getParts())
            if (p != part)
                p.onPartChanged(part);

        // Tell the client the part has been placed
        if (getWorld() != null && !getWorld().isRemote)
            PacketCPart.addPart(this, part);

        // Notify neighbors
        notifyBlockChange();
        notifyTileChange();
        // Recalculate lighting
        recalculateLighting();
        // Mark chunk for saving
        markDirty();
        // Call rendering update
        markRender();
    }

    @Override
    public void removePart(IQLPart part) {

        // Tell the client the part has been removed
        if (getWorld() != null && !getWorld().isRemote)
            PacketCPart.removePart(this, part);

        String id = getPartID(part);

        // Remove from the part map
        parts.remove(id);
        // Remove from the slot map (if needed)
        if (part instanceof ISlottedPart) {
            int slotMask = ((ISlottedPart) part).getSlotMask();
            for (PartSlot s : PartSlot.values())
                if ((slotMask & s.mask) != 0 && getPartInSlot(s.ordinal()) == part)
                    slotMap.remove(s.ordinal());
        }

        // Notify part
        part.onRemoved();
        // Notify other parts
        for (IQLPart p : getParts())
            if (p != part)
                p.onPartChanged(part);

        // Remove parent
        part.setParent(null);

        // Remove tile if needed
        if (getParts().isEmpty() && getWorld() != null) {
            getWorld().setBlockToAir(getPos());
            getWorld().removeTileEntity(getPos());
        }

        // Notify neighbors
        notifyBlockChange();
        notifyTileChange();
        // Recalculate lighting
        recalculateLighting();
        // Mark chunk for saving
        markDirty();
        // Call rendering update
        markRender();
    }

    @Override
    public Collection<IQLPart> getParts() {

        return Collections.unmodifiableCollection(parts.values());
    }

    @Override
    public List<IMicroblock> getMicroblocks() {

        List<IMicroblock> microblocks = new ArrayList<IMicroblock>();

        for (IQLPart p : getParts())
            if (p instanceof IMicroblock)
                microblocks.add((IMicroblock) p);

        return microblocks;
    }

    @Override
    public String getPartID(IQLPart part) {

        for (Entry<String, IQLPart> e : parts.entrySet())
            if (e.getValue() == part)
                return e.getKey();
        return null;
    }

    @Override
    public IQLPart findPart(String partID) {

        if (!parts.containsKey(partID))
            return null;
        return parts.get(partID);
    }

    @Override
    public ISlottedPart getPartInSlot(int slot) {

        return slotMap.containsKey(slot) ? slotMap.get(slot) : null;
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound tag) {

        super.writeToNBT(tag);

        NBTTagList list = new NBTTagList();
        for (Entry<String, IQLPart> e : parts.entrySet()) {
            NBTTagCompound t = new NBTTagCompound();
            t.setString("type", e.getValue().getType());
            t.setString("id", e.getKey());
            NBTTagCompound data = new NBTTagCompound();
            e.getValue().writeToNBT(data);
            t.setTag("data", data);
            list.appendTag(t);
        }
        tag.setTag("parts", list);
        return tag;
    }

    @Override
    public void readFromNBT(NBTTagCompound tag) {

        super.readFromNBT(tag);

        parts.clear();
        slotMap.clear();

        NBTTagList list = tag.getTagList("parts", new NBTTagCompound().getId());
        for (int i = 0; i < list.tagCount(); i++) {
            NBTTagCompound t = list.getCompoundTagAt(i);
            IQLPart part = PartRegistry.createPart(t.getString("type"), false);
            part.readFromNBT(t.getCompoundTag("data"));
            addPart(t.getString("id"), part, false);
        }
    }

    @Override
    public void writeUpdateData(MCByteBuf buf) {

        super.writeUpdateData(buf);

        buf.writeInt(parts.size());
        for (Entry<String, IQLPart> e : parts.entrySet()) {
            buf.writeString(e.getValue().getType());
            buf.writeString(e.getKey());
            e.getValue().writeUpdateData(buf);
        }
    }

    @Override
    public void readUpdateData(MCByteBuf buf) {

        super.readUpdateData(buf);

        parts.clear();

        int amt = buf.readInt();
        for (int i = 0; i < amt; i++) {
            String type = buf.readString();
            String id = buf.readString();
            IQLPart part = PartRegistry.createPart(type, true);
            part.readUpdateData(buf);
            addPart(id, part, false);
        }
    }

    public QRayTraceResult rayTrace(Vec3d start, Vec3d end) {

        QRayTraceResult closest = null;
        double dist = Double.MAX_VALUE;

        for (IQLPart p : getParts()) {
            QRayTraceResult mop = p.rayTrace(start, end);
            if (mop != null) {
                double d = mop.hitVec.distanceTo(start);
                if (closest == null || d <= dist) {
                    closest = mop;
                    dist = d;
                }
            }
        }
        return closest;
    }

    public void addCollisionBoxesToList(List<Cuboid> boxes, AxisAlignedBB bounds) {

        Vector3 pos = new Vector3(getPos());
        Cuboid bounds_ = new Cuboid(bounds).sub(pos);
        for (IQLPart p : getParts())
            for (Cuboid c : p.getCollisionBoxes())
                if (c.intersects(bounds_))
                    boxes.add(c.copy().add(pos));
    }

    public boolean isSideSolid(EnumFacing side) {

        IQLPart part = getPartInSlot(PartSlot.face(side).mask);
        return part != null && part instanceof ISolidPart ? ((ISolidPart) part).isSideSolid(side) : false;
    }

    @Override
    public boolean canConnectRedstone(EnumFacing side) {

        return canConnectRedstone(null, side);
    }

    @Override
    public int getWeakRedstoneOutput(EnumFacing side) {

        return getWeakRedstoneOutput(null, side);
    }

    @Override
    public int getStrongRedstoneOutput(EnumFacing side) {

        return getStrongRedstoneOutput(null, side);
    }

    @Override
    public boolean canConnectRedstone(EnumFacing face, EnumFacing side) {

        // Side
        IQLPart part = getPartInSlot(PartSlot.face(side).ordinal());
        if (part != null) {
            if (part instanceof IRedstonePart)
                return ((IRedstonePart) part).canConnectRedstone(side);
            return false;
        }

        if (face != null) {
            // Face-side edge
            part = getPartInSlot(PartSlot.edgeBetween(face, side).ordinal());
            if (part != null) {
                if (part instanceof IRedstonePart)
                    return ((IRedstonePart) part).canConnectRedstone(side);
                return false;
            }
            // Face
            part = getPartInSlot(PartSlot.face(face).ordinal());
            if (part != null)
                if (part instanceof IRedstonePart)
                    return ((IRedstonePart) part).canConnectRedstone(side);
            // Non-slotted parts
            for (IQLPart p : getParts())
                if (p != part && p instanceof IRedstonePart && (!(p instanceof ISlottedPart) || ((ISlottedPart) p).getSlotMask() == 0)
                        && ((IRedstonePart) p).canConnectRedstone(side))
                    return true;
            return false;
        } else {
            // Center
            part = getPartInSlot(PartSlot.CENTER.ordinal());
            if (part != null)
                if (part instanceof IRedstonePart && ((IRedstonePart) part).canConnectRedstone(side))
                    return true;
            // Sides
            for (EnumFacing d : EnumFacing.VALUES)
                if (d != side && d != side.getOpposite())
                    if (canConnectRedstone(d, side))
                        return true;
            return false;
        }
    }

    @Override
    public int getWeakRedstoneOutput(EnumFacing face, EnumFacing side) {

        // Side
        IQLPart part = getPartInSlot(PartSlot.face(side).ordinal());
        if (part != null) {
            if (part instanceof IRedstonePart)
                return ((IRedstonePart) part).getWeakPower(side);
            return 0;
        }

        if (face != null) {
            // Face-side edge
            part = getPartInSlot(PartSlot.edgeBetween(face, side).ordinal());
            if (part != null) {
                if (part instanceof IRedstonePart)
                    return ((IRedstonePart) part).getWeakPower(side);
                return 0;
            }
            int pow = 0;
            // Face
            part = getPartInSlot(PartSlot.face(face).ordinal());
            if (part != null)
                if (part instanceof IRedstonePart)
                    pow = Math.max(pow, ((IRedstonePart) part).getWeakPower(side));
            // Non-slotted parts
            for (IQLPart p : getParts())
                if (p != part && p instanceof IRedstonePart && (!(p instanceof ISlottedPart) || ((ISlottedPart) p).getSlotMask() == 0))
                    pow = Math.max(pow, ((IRedstonePart) p).getWeakPower(side));
            return pow;
        } else {
            int pow = 0;
            // Center
            part = getPartInSlot(PartSlot.CENTER.ordinal());
            if (part != null)
                if (part instanceof IRedstonePart)
                    pow = Math.max(pow, ((IRedstonePart) part).getWeakPower(side));
            // Sides
            for (EnumFacing d : EnumFacing.VALUES)
                if (d != side && d != side.getOpposite())
                    pow = Math.max(pow, getWeakRedstoneOutput(d, side));
            // Non-slotted parts
            for (IQLPart p : getParts())
                if (p != part && p instanceof IRedstonePart && (!(p instanceof ISlottedPart) || ((ISlottedPart) p).getSlotMask() == 0))
                    pow = Math.max(pow, ((IRedstonePart) p).getWeakPower(side));
            return pow;
        }
    }

    @Override
    public int getStrongRedstoneOutput(EnumFacing face, EnumFacing side) {

        // Side
        IQLPart part = getPartInSlot(PartSlot.face(side).ordinal());
        if (part != null) {
            if (part instanceof IRedstonePart)
                return ((IRedstonePart) part).getStrongPower(side);
            return 0;
        }

        if (face != null) {
            // Face-side edge
            part = getPartInSlot(PartSlot.edgeBetween(face, side).ordinal());
            if (part != null) {
                if (part instanceof IRedstonePart)
                    return ((IRedstonePart) part).getStrongPower(side);
                return 0;
            }
            int pow = 0;
            // Face
            part = getPartInSlot(PartSlot.face(face).ordinal());
            if (part != null)
                if (part instanceof IRedstonePart)
                    pow = Math.max(pow, ((IRedstonePart) part).getStrongPower(side));
            // Non-slotted parts
            for (IQLPart p : getParts())
                if (p != part && p instanceof IRedstonePart && (!(p instanceof ISlottedPart) || ((ISlottedPart) p).getSlotMask() == 0))
                    pow = Math.max(pow, ((IRedstonePart) p).getStrongPower(side));
            return pow;
        } else {
            int pow = 0;
            // Center
            part = getPartInSlot(PartSlot.CENTER.ordinal());
            if (part != null)
                if (part instanceof IRedstonePart)
                    pow = Math.max(pow, ((IRedstonePart) part).getStrongPower(side));
            // Sides
            for (EnumFacing d : EnumFacing.VALUES)
                if (d != side && d != side.getOpposite())
                    pow = Math.max(pow, getStrongRedstoneOutput(d, side));
            return pow;
        }
    }

    @Override
    public AxisAlignedBB getRenderBoundingBox() {

        AxisAlignedBB box = null;
        for (IQLPart p : getParts()) {
            AxisAlignedBB b = p.getRenderBounds().toAABB();
            if (box != null)
                box = box.addCoord(b.minX, b.minY, b.minZ).addCoord(b.maxX, b.maxY, b.maxZ);
            else
                box = b;
        }
        return box != null ? box.offset(getPos()) : super.getRenderBoundingBox();
    }

    @Override
    public boolean shouldRenderInPass(int pass) {

        RenderMultipart.pass = pass;

        return true;
    }

    public boolean firstTick = true;

    @Override
    public void update() {

        if (firstTick == true) {
            for (IQLPart p : getParts())
                p.onLoaded();
            firstTick = false;
        }

        for (IQLPart p : getParts())
            p.update();
    }

    @Override
    public void validate() {

        super.validate();

        for (IQLPart p : getParts())
            p.onLoaded();
    }

    @Override
    public void invalidate() {

        super.invalidate();

        for (IQLPart p : getParts())
            p.onUnloaded();
    }

    @Override
    public void onChunkUnload() {

        super.onChunkUnload();

        for (IQLPart p : getParts())
            p.onUnloaded();
    }

}
