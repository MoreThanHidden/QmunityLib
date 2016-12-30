package uk.co.qmunity.lib.vec;

import net.minecraft.util.math.Vec3d;

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

}
