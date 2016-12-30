package uk.co.qmunity.lib.transform;

import net.minecraft.util.math.Vec3d;
import uk.co.qmunity.lib.vec.Vec3dCube;
import uk.co.qmunity.lib.vec.Vec3dHelper;

public class Scale implements Transformation {

    private double x, y, z;
    private Vec3d center = Vec3dHelper.CENTER;

    public Scale(double x, double y, double z) {

        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Scale(double x, double y, double z, Vec3d center) {

        this(x, y, z);
        this.center = center;
    }

    @Override
    public Vec3d apply(Vec3d point) {
        point = point.subtract(center);
        return new Vec3d(point.xCoord * x, point.yCoord * y, point.zCoord * z).add(center);
    }

    @Override
    public Vec3dCube apply(Vec3dCube cube) {

        return new Vec3dCube(apply(cube.getMin()), apply(cube.getMax()));
    }

}
