package uk.co.qmunity.lib.util;

import net.minecraft.client.resources.I18n;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import uk.co.qmunity.lib.vec.Vec3dHelper;

public enum Dir {

    FRONT(EnumFacing.NORTH), RIGHT(EnumFacing.EAST), BACK(EnumFacing.SOUTH), LEFT(EnumFacing.WEST), TOP(EnumFacing.UP), BOTTOM(
            EnumFacing.DOWN);

    private EnumFacing d;

    private Dir(EnumFacing d) {

        this.d = d;
    }

    public EnumFacing toEnumFacing(EnumFacing face, int rotation) {
        EnumFacing d = this.d;
        for (int i = 0; i < rotation; i++)
            d = d.rotateAround(EnumFacing.Axis.Y);

        return Vec3dHelper.toEnumFacing(Vec3dHelper.rotate(new Vec3d(new BlockPos(0, 0, 0).offset(d)), face, new Vec3d(0, 0, 0)));
    }

    public EnumFacing getFD() {

        return d;
    }

    public static Dir getDirection(EnumFacing direction, EnumFacing face, int rotation) {

        EnumFacing d = Vec3dHelper.toEnumFacing(Vec3dHelper.rotate(new Vec3d(new BlockPos(0, 0, 0).offset(direction)), face, new Vec3d(0, 0, 0)));

        for (int i = 0; i < rotation; i++)
            d = d.rotateAround(EnumFacing.Axis.Y);

        return fromFD(d);
    }

    private static Dir fromFD(EnumFacing EnumFacing) {

        for (Dir d : values())
            if (d.d == EnumFacing)
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
