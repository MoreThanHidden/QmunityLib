package uk.co.qmunity.lib.vec;

import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.Vec3d;
import uk.co.qmunity.lib.transform.Transformation;

public class Vec3dHelper {

    public static final Vec3d CENTER = new Vec3d(0.5, 0.5, 0.5);

    public static Vec3d rotate(Vec3d vec, Quat quat) {
        return quat.mul(vec);
    }

    public static Vec3d rotate(Vec3d vec, double x, double y, double z) {

        Quat rx = new Quat(new Vec3d(1, 0, 0), Math.toRadians(x));
        Quat ry = new Quat(new Vec3d(0, 1, 0), Math.toRadians(y));
        Quat rz = new Quat(new Vec3d(0, 0, 1), Math.toRadians(z));

        Quat res = rx.mul(ry.mul(rz));

        return rotate(vec, res);
    }

    public static Vec3d rotate(Vec3d vec,  EnumFacing face, Vec3d center) {

        switch (face) {
            case DOWN:
                return vec;
            case UP:
                return rotate(vec, 0, 0, 2 * 90, center);
            case WEST:
                return rotate(vec, 0, 0, -1 * 90, center);
            case EAST:
                return rotate(vec, 0, 0, 1 * 90, center);
            case NORTH:
                return rotate(vec, 1 * 90, 0, 0, center);
            case SOUTH:
                return rotate(vec, -1 * 90, 0, 0, center);
            default:
                break;
        }

        return vec;
    }

    private static Vec3d rotate(Vec3d vec, int x, int y, int z, Vec3d center) {

        vec = rotate(vec.subtract(center), x, y, z).add(center);
        double mul = 10000000;

        return new Vec3d((Math.round(vec.xCoord * mul) / mul), (Math.round(vec.yCoord * mul) / mul), (Math.round(vec.zCoord * mul) / mul));
    }

    public static Vec3d mul(Vec3d vec, Vec3d vec2){
        return new Vec3d(vec.xCoord * vec2.xCoord, vec.yCoord * vec2.yCoord, vec.zCoord * vec2.zCoord);
    }

    public static Vec3d mul(Vec3d vec, int i){
        return mul(vec, new Vec3d(i, i, i));
    }

    public static Vec3d transform(Vec3d vec, Transformation transformation) {

        return transformation.apply(vec);
    }

    public static final EnumFacing toEnumFacing(Vec3d vec3) {

        if (vec3.zCoord == 1)
            return EnumFacing.SOUTH;
        if (vec3.zCoord == -1)
            return EnumFacing.NORTH;

        if (vec3.xCoord == 1)
            return EnumFacing.EAST;
        if (vec3.xCoord == -1)
            return EnumFacing.WEST;

        if (vec3.yCoord == 1)
            return EnumFacing.UP;
        if (vec3.yCoord == -1)
            return EnumFacing.DOWN;

        return null;
    }

}
