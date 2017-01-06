package uk.co.qmunity.lib.part;

import net.minecraft.util.EnumFacing;

public interface ISolidPart extends IQLPart {

    public boolean isSideSolid(EnumFacing face);
}
