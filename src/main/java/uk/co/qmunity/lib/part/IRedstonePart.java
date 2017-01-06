package uk.co.qmunity.lib.part;

import net.minecraft.util.EnumFacing;

public interface IRedstonePart extends IQLPart {

    // Redstone
    public boolean canConnectRedstone(EnumFacing side);

    public int getWeakPower(EnumFacing side);

    public int getStrongPower(EnumFacing side);

}
