package uk.co.qmunity.lib.vec;

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

    public static Vec3d mul(Vec3d vec, Vec3d vec2){
        return new Vec3d(vec.xCoord * vec2.xCoord, vec.yCoord * vec2.yCoord, vec.zCoord * vec2.zCoord);
    }

    public static Vec3d mul(Vec3d vec, int i){
        return mul(vec, new Vec3d(i, i, i));
    }

    public static Vec3d transform(Vec3d vec, Transformation transformation) {

        return transformation.apply(vec);
    }

}
