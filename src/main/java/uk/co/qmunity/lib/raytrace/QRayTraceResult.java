package uk.co.qmunity.lib.raytrace;

import net.minecraft.util.math.RayTraceResult;
import uk.co.qmunity.lib.part.IQLPart;
import uk.co.qmunity.lib.vec.Cuboid;

public class QRayTraceResult extends RayTraceResult {

    public IQLPart part;
    public Cuboid cube;

    public QRayTraceResult(RayTraceResult mop) {

        super(mop.hitVec, mop.sideHit, mop.getBlockPos());
        hitInfo = mop.hitInfo;
    }

    public QRayTraceResult(RayTraceResult mop, IQLPart part) {

        this(mop);
        this.part = part;
    }

    public QRayTraceResult(RayTraceResult mop, Cuboid cube) {

        this(mop);
        this.cube = cube;
    }

    public QRayTraceResult(RayTraceResult mop, IQLPart part, Cuboid cube) {

        this(mop, part);
        this.cube = cube;
    }

}
