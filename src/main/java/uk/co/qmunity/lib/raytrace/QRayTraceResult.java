package uk.co.qmunity.lib.raytrace;

import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import uk.co.qmunity.lib.part.IPart;
import uk.co.qmunity.lib.vec.Vec3dCube;

public class QRayTraceResult extends RayTraceResult {

    private IPart part;
    private Vec3dCube cube;

    public QRayTraceResult(RayTraceResult mop) {

        super(mop.hitVec, mop.sideHit, mop.getBlockPos());
    }

    public QRayTraceResult(RayTraceResult mop, IPart part) {

        this(mop);
        this.part = part;
    }

    public QRayTraceResult(RayTraceResult mop, Vec3dCube cube) {

        this(mop);
        this.cube = cube;
    }

    public QRayTraceResult(RayTraceResult mop, IPart part, Vec3dCube cube) {

        this(mop, part);
        this.cube = cube;
    }

    public IPart getPart() {

        return part;
    }

    public Vec3dCube getCube() {

        return cube;
    }

    public double distanceTo(Vec3d pos) {

        return pos.distanceTo(new Vec3d(hitVec.xCoord, hitVec.yCoord, hitVec.zCoord));
    }

}
