package uk.co.qmunity.lib.util;

import net.minecraft.client.resources.I18n;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import uk.co.qmunity.lib.transform.Rotation;
import uk.co.qmunity.lib.vec.Vector3;

public enum Dir {

    FRONT(EnumFacing.NORTH), RIGHT(EnumFacing.EAST), BACK(EnumFacing.SOUTH), LEFT(EnumFacing.WEST), TOP(EnumFacing.UP), BOTTOM(
            EnumFacing.DOWN);

    private EnumFacing d;

    private Dir(EnumFacing d) {

        this.d = d;
    }

    public EnumFacing toEnumFacing(EnumFacing face, int rotation) {

        return toSide(new Vector3(new BlockPos(0,0,0).offset(d)).//
                apply(Rotation.quarterRotations[rotation % 4]).//
                apply(Rotation.sideRotations[face.ordinal()]).//
                toBlockPos());
    }

    public static EnumFacing toSide(BlockPos pos) {

        if (pos.getY() < 0)
            return EnumFacing.DOWN;
        if (pos.getY() > 0)
            return EnumFacing.UP;
        if (pos.getZ() < 0)
            return EnumFacing.NORTH;
        if (pos.getZ() > 0)
            return EnumFacing.SOUTH;
        if (pos.getX() < 0)
            return EnumFacing.WEST;
        if (pos.getX()> 0)
            return EnumFacing.EAST;

        return null;
    }


    public EnumFacing getFD() {

        return d;
    }

    public static Dir getDirection(EnumFacing direction, EnumFacing face, int rotation) {

        return fromFD(toSide(new Vector3(new BlockPos(0,0,0).offset(direction)).//
                apply(Rotation.sideRotations[face.ordinal()].inverse()).//
                apply(Rotation.quarterRotations[rotation % 4].inverse()).//
                toBlockPos()));
    }

    private static Dir fromFD(EnumFacing forgeDirection) {

        for (Dir d : values())
            if (d.d == forgeDirection)
                return d;

        return null;
    }

    public Dir getOpposite() {

        switch (this) {
        case BACK:
            return FRONT;
        case FRONT:
            return BACK;
        case LEFT:
            return RIGHT;
        case RIGHT:
            return LEFT;
        case TOP:
            return BOTTOM;
        default:
            return TOP;
        }
    }

    @SideOnly(Side.CLIENT)
    public String getLocalizedName() {

        return I18n.format("bluepower:direction." + name().toLowerCase());
    }

}
