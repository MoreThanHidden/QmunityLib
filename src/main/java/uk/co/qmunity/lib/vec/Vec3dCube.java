package uk.co.qmunity.lib.vec;

import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.Vec3d;
import uk.co.qmunity.lib.misc.Pair;
import uk.co.qmunity.lib.part.IPart;
import uk.co.qmunity.lib.transform.Transformation;
import uk.co.qmunity.lib.transform.Translation;

import java.util.ArrayList;
import java.util.List;

public class Vec3dCube {

    private Vec3d min, max;
    private IPart part;
    private Object data;


    public Vec3dCube(double minX, double minY, double minZ, double maxX, double maxY, double maxZ, IPart part) {

        this(minX, minY, minZ, maxX, maxY, maxZ);
        this.part = part;
    }

    public Vec3dCube(double minX, double minY, double minZ, double maxX, double maxY, double maxZ) {

        this(new Vec3d(minX, minY, minZ), new Vec3d(maxX, maxY, maxZ));
    }

    public Vec3dCube(Vec3d a, Vec3d b) {
        min = a;
        max = b;

        fix();
    }

    public Vec3dCube(Vec3d a, Vec3d b, IPart part) {

        this(a, b);
        this.part = part;
    }

    public Vec3dCube(AxisAlignedBB aabb) {

        this(aabb.minX, aabb.minY, aabb.minZ, aabb.maxX, aabb.maxY, aabb.maxZ);
    }

    public Vec3d getMin() {

        return min;
    }

    public Vec3d getMax() {

        return max;
    }

    public Vec3d getCenter() {

        return new Vec3d((getMinX() + getMaxX()) / 2D, (getMinY() + getMaxY()) / 2D, (getMinZ() + getMaxZ()) / 2D);
    }

    public double getMinX() {

        return min.xCoord;
    }

    public double getMinY() {

        return min.yCoord;
    }

    public double getMinZ() {

        return min.zCoord;
    }

    public double getMaxX() {

        return max.xCoord;
    }

    public double getMaxY() {

        return max.yCoord;
    }

    public double getMaxZ() {

        return max.zCoord;
    }

    public IPart getPart() {

        return part;
    }

    public void setPart(IPart p) {

        part = p;
    }

    public AxisAlignedBB toAABB() {

        return new AxisAlignedBB(getMinX(), getMinY(), getMinZ(), getMaxX(), getMaxY(), getMaxZ());
    }

    @Override
    public Vec3dCube clone() {

        return new Vec3dCube(min, max, part);
    }

    public Vec3dCube expand(double size) {

        min.subtract(size, size, size);
        max.add(new Vec3d(size, size, size));

        return this;
    }

    public Vec3dCube fix() {

        Vec3d a = min;
        Vec3d b = max;

        double minX = Math.min(a.xCoord, b.xCoord);
        double minY = Math.min(a.yCoord, b.yCoord);
        double minZ = Math.min(a.zCoord, b.zCoord);

        double maxX = Math.max(a.xCoord, b.xCoord);
        double maxY = Math.max(a.yCoord, b.yCoord);
        double maxZ = Math.max(a.zCoord, b.zCoord);

        min = new Vec3d(minX, minY, minZ);
        max = new Vec3d(maxX, maxY, maxZ);

        return this;
    }

    public Vec3dCube rotate(int x, int y, int z, Vec3d center) {

        Vec3dHelper.rotate(min.subtract(center), x, y, z).add(center);
        Vec3dHelper.rotate(max.subtract(center), x, y, z).add(center);

        double mul = 10000000;

        fix();

        min = new Vec3d(Math.round(min.xCoord * mul) / mul, Math.round(min.yCoord * mul) / mul, Math.round(min.zCoord * mul) / mul);
        max = new Vec3d(Math.round(max.xCoord * mul) / mul, Math.round(max.yCoord * mul) / mul, Math.round(max.zCoord * mul) / mul);

        return this;
    }

    public Vec3dCube rotate(EnumFacing face, Vec3d center) {

        switch (face) {
        case DOWN:
            return this;
        case UP:
            return rotate(0, 0, 2 * 90, center);
        case WEST:
            return rotate(0, 0, -1 * 90, center);
        case EAST:
            return rotate(0, 0, 1 * 90, center);
        case NORTH:
            return rotate(1 * 90, 0, 0, center);
        case SOUTH:
            return rotate(-1 * 90, 0, 0, center);
        default:
            break;
        }

        return this;
    }

    public Vec3dCube add(double x, double y, double z) {

        min.add(new Vec3d(x, y, z));
        max.add(new Vec3d(x, y, z));

        return this;
    }

    public static final Vec3dCube merge(List<Vec3dCube> cubes) {

        double minx = Double.MAX_VALUE;
        double miny = Double.MAX_VALUE;
        double minz = Double.MAX_VALUE;
        double maxx = Double.MIN_VALUE;
        double maxy = Double.MIN_VALUE;
        double maxz = Double.MIN_VALUE;

        for (Vec3dCube c : cubes) {
            minx = Math.min(minx, c.getMinX());
            miny = Math.min(miny, c.getMinY());
            minz = Math.min(minz, c.getMinZ());
            maxx = Math.max(maxx, c.getMaxX());
            maxy = Math.max(maxy, c.getMaxY());
            maxz = Math.max(maxz, c.getMaxZ());
        }

        if (cubes.size() == 0)
            return new Vec3dCube(0, 0, 0, 0, 0, 0);

        return new Vec3dCube(minx, miny, minz, maxx, maxy, maxz);
    }

    @Override
    public boolean equals(Object o) {

        if (!(o instanceof Vec3dCube))
            return false;
        Vec3dCube other = (Vec3dCube) o;
        return other.min.equals(min) && other.max.equals(max) && other.part == part;
    }

    @Override
    public int hashCode() {

        return min.hashCode() << 8 + max.hashCode();
    }

    public Vec2dRect getFace(EnumFacing face) {

        switch (face) {
        case DOWN:
        case UP:
            return new Vec2dRect(getMinX(), getMinZ(), getMaxX(), getMaxZ());
        case WEST:
        case EAST:
            return new Vec2dRect(getMinY(), getMinZ(), getMaxY(), getMaxZ());
        case NORTH:
        case SOUTH:
            return new Vec2dRect(getMinX(), getMinY(), getMaxX(), getMaxY());
        default:
            break;
        }

        return null;
    }

    public Vec3dCube transform(Transformation transformation) {

        return transformation.apply(this);
    }

    public boolean occlusionTest(Vec3dCube cube) {

        return !toAABB().intersectsWith(cube.toAABB());
    }

    public List<Pair<Pair<Vec3dCube, Translation>, boolean[]>> splitInto1x1() {

        List<Pair<Pair<Vec3dCube, Translation>, boolean[]>> cubes = new ArrayList<Pair<Pair<Vec3dCube, Translation>, boolean[]>>();

        int minx = (int) Math.floor(getMinX());
        int miny = (int) Math.floor(getMinY());
        int minz = (int) Math.floor(getMinZ());
        int maxx = (int) Math.ceil(getMaxX());
        int maxy = (int) Math.ceil(getMaxY());
        int maxz = (int) Math.ceil(getMaxZ());

        for (int x = minx; x < maxx; x++) {
            for (int y = miny; y < maxy; y++) {
                for (int z = minz; z < maxz; z++) {
                    Translation t = new Translation(x, y, z);
                    Vec3dCube cube = new Vec3dCube(0, 0, 0, 1, 1, 1);
                    boolean[] sides = new boolean[6];

                    if (x == minx) {
                        cube.min = new Vec3d(getMinX() - minx, cube.getMin().yCoord, cube.getMin().zCoord);
                        sides[4] = true;
                    }
                    if (y == miny) {
                        cube.min = new Vec3d(cube.getMin().xCoord, getMinY() - miny, cube.getMin().zCoord);
                        sides[0] = true;
                    }
                    if (z == minz) {
                        cube.min = new Vec3d(cube.getMin().xCoord, cube.getMin().yCoord, getMinZ() - minz);
                        sides[2] = true;
                    }

                    if (x == maxx - 1) {
                        cube.max = new Vec3d(getMaxX() - (maxx - 1), cube.getMax().yCoord, cube.getMax().zCoord);
                        sides[5] = true;
                    }
                    if (y == maxy - 1) {
                        cube.max = new Vec3d(cube.getMax().xCoord, getMaxY() - (maxy - 1), cube.getMax().zCoord);
                        sides[1] = true;
                    }
                    if (z == maxz - 1) {
                        cube.max = new Vec3d(cube.getMax().xCoord, cube.getMax().yCoord, getMaxZ() - (maxz - 1));
                        sides[3] = true;
                    }

                    cube.fix();

                    cubes.add(new Pair<Pair<Vec3dCube, Translation>, boolean[]>(new Pair<Vec3dCube, Translation>(cube, t), sides));
                }
            }
        }

        return cubes;
    }

    public Vec3dCube setData(Object data) {

        this.data = data;

        return this;
    }

    public Object getData() {

        return data;
    }
}
