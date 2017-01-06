package uk.co.qmunity.lib.part;

import java.util.Collection;

import net.minecraft.block.Block;
import net.minecraft.util.EnumFacing;
import uk.co.qmunity.lib.vec.IWorldLocation;

public interface IPartHolder extends IWorldLocation {

    public boolean canAddPart(IQLPart part);

    public void addPart(IQLPart part);

    public void addPart(String partID, IQLPart part, boolean notify);

    public void removePart(IQLPart part);

    public Collection<IQLPart> getParts();

    public String getPartID(IQLPart part);

    public IQLPart findPart(String partID);

    public ISlottedPart getPartInSlot(int slot);

    public void notifyBlockChange();

    public void notifyTileChange();

    public void markDirty();

    public void recalculateLighting();

    public void markRender();

    public boolean canConnectRedstone(EnumFacing face, EnumFacing side);

    public int getWeakRedstoneOutput(EnumFacing face, EnumFacing side);

    public int getStrongRedstoneOutput(EnumFacing face, EnumFacing side);

    public Block getBlockType();

}
