package uk.co.qmunity.lib.raytrace;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import uk.co.qmunity.lib.part.IPart;
import uk.co.qmunity.lib.part.IPartSelectable;
import uk.co.qmunity.lib.util.QLog;
import uk.co.qmunity.lib.vec.Vec3dCube;

import java.util.List;

public class RayTracer {

    private static RayTracer instance = new RayTracer();

    public static RayTracer instance() {

        return instance;
    }

    private RayTracer() {

    }

    public QRayTraceResult rayTraceCubes(IPartSelectable part, Vec3d start, Vec3d end) {

        try {
            QRayTraceResult mop = rayTraceCubes(part.getSelectionBoxes(), start, end,
                    ((IPart) part).getPos());
            if (mop == null)
                return null;

            return new QRayTraceResult(mop, part, mop.getCube());
        } catch (Exception ex) {
            QLog.error(ex.getMessage());
        }
        return null;
    }

    public QRayTraceResult rayTraceCubes(List<Vec3dCube> cubes, Vec3d start, Vec3d end, BlockPos blockPos) {

        if (cubes == null)
            return null;

        Vec3d start_ = start.subtract(blockPos.getX(), blockPos.getY(), blockPos.getZ());
        Vec3d end_ = end.subtract(blockPos.getX(), blockPos.getY(), blockPos.getZ());

        QRayTraceResult mop = rayTraceCubes(cubes, start_, end_);
        if (mop == null)
            return null;

        BlockPos pos = mop.getBlockPos().add(blockPos);
        mop.hitVec = mop.hitVec.addVector(blockPos.getX(), blockPos.getY(), blockPos.getZ());

        return new QRayTraceResult(new RayTraceResult(mop.hitVec, mop.sideHit, pos));
    }

    public QRayTraceResult rayTraceCubes(List<Vec3dCube> cubes, Vec3d start, Vec3d end) {

        QRayTraceResult closest = null;
        double dist = Double.MAX_VALUE;

        for (Vec3dCube c : cubes) {
            QRayTraceResult mop = rayTraceCube(c, start, end);
            if (mop == null)
                continue;
            double d = mop.distanceTo(start);
            if (d < dist) {
                dist = d;
                closest = mop;
            }
        }

        return closest;
    }

    public QRayTraceResult rayTraceCube(Vec3dCube cube, Vec3d start, Vec3d end) {

        Vec3d closest = null;
        double dist = Double.MAX_VALUE;
        EnumFacing f = null;

        for (EnumFacing face : EnumFacing.VALUES) {
            Vec3d v = rayTraceFace(cube, face, start, end);
            if (v == null)
                continue;
            double d = v.distanceTo(start);
            if (d < dist) {
                dist = d;
                closest = v;
                f = face;
            }
        }

        if (closest == null)
            return null;

        return new QRayTraceResult(new RayTraceResult(closest, f, new BlockPos(0,0,0)), cube);
    }

    private Vec3d rayTraceFace(Vec3dCube cube, EnumFacing face, Vec3d start, Vec3d end) {

        Vec3d director = end.subtract(start).normalize();
        Vec3d normal = getNormal(face);
        Vec3d point = getPoint(cube, face);

        if (normal.dotProduct(director) == 0)
            return null;

        double t = (point.dotProduct(normal) - start.dotProduct(normal)) / director.dotProduct(normal);
        double x = start.xCoord + (t * director.xCoord);
        double y = start.yCoord + (t * director.yCoord);
        double z = start.zCoord + (t * director.zCoord);

        Vec3d v = new Vec3d(x, y, z);
        Vec3dCube f = getFace(cube, face);

        if (normal.xCoord != 0) {
            if (v.yCoord < f.getMinY() || v.yCoord > f.getMaxY() || v.zCoord < f.getMinZ() || v.zCoord > f.getMaxZ())
                return null;
        } else if (normal.yCoord != 0) {
            if (v.xCoord < f.getMinX() || v.xCoord > f.getMaxX() || v.zCoord < f.getMinZ() || v.zCoord > f.getMaxZ())
                return null;
        } else if (normal.zCoord != 0) {
            if (v.xCoord < f.getMinX() || v.xCoord > f.getMaxX() || v.yCoord < f.getMinY() || v.yCoord > f.getMaxY())
                return null;
        } else {
            return null;
        }

        return v;
    }

    private Vec3d getPoint(Vec3dCube cube, EnumFacing face) {

        if (face.getFrontOffsetX() + face.getFrontOffsetY() + face.getFrontOffsetZ() < 0) {
            return cube.getMin();
        } else {
            return cube.getMax();
        }
    }

    private Vec3dCube getFace(Vec3dCube cube, EnumFacing face) {

        Vec3d min = cube.getMin();
        Vec3d max = cube.getMax();

        switch (face) {
        case DOWN:
            max = new Vec3d(max.xCoord, min.yCoord, max.zCoord);
            break;
        case UP:
            min = new Vec3d(min.xCoord, max.yCoord, min.zCoord);
            break;
        case WEST:
            max = new Vec3d(min.xCoord, max.yCoord, max.zCoord);
            break;
        case EAST:
            min = new Vec3d(max.xCoord, min.yCoord, min.zCoord);
            break;
        case NORTH:
            max = new Vec3d(max.xCoord, max.yCoord, min.zCoord);
            break;
        case SOUTH:
            min = new Vec3d(min.xCoord, min.yCoord, max.zCoord);
            break;
        default:
            break;
        }

        return new Vec3dCube(min, max);
    }

    private Vec3d getNormal(EnumFacing face) {

        return new Vec3d(face.getFrontOffsetX(), face.getFrontOffsetY(), face.getFrontOffsetZ());
    }

    public static double overrideReachDistance = -1;

    private static double getBlockReachDistance_server(EntityPlayerMP player) {

        return player.interactionManager.getBlockReachDistance();
    }

    @SideOnly(Side.CLIENT)
    private static double getBlockReachDistance_client() {

        return Minecraft.getMinecraft().playerController.getBlockReachDistance();
    }

    public static double getBlockReachDistance(EntityPlayer player) {

        if (overrideReachDistance > 0)
            return overrideReachDistance;

        return player.world.isRemote ? getBlockReachDistance_client()
                : player instanceof EntityPlayerMP ? getBlockReachDistance_server((EntityPlayerMP) player) : 5D;
    }

    public static Vec3d getCorrectedHeadVector(EntityPlayer player) {

        Vec3d v = new Vec3d(player.posX, player.posY, player.posZ);
        if (player.world.isRemote) {
            v.add(new Vec3d(0, player.getEyeHeight() - player.getDefaultEyeHeight(), 0));// compatibility with eye height changing mods
        } else {
            v.add(new Vec3d(0, player.getEyeHeight(), 0));
            if (player instanceof EntityPlayerMP && player.isSneaking())
                v.subtract(0, 0.08, 0);
        }
        return v;
    }

    public static Vec3d getStartVector(EntityPlayer player) {

        return getCorrectedHeadVector(player);
    }

    public static Vec3d getEndVector(EntityPlayer player) {

        Vec3d headVec = getCorrectedHeadVector(player);
        Vec3d lookVec = player.getLook(1.0F);
        double reach = getBlockReachDistance(player);
        return headVec.addVector(lookVec.xCoord * reach, lookVec.yCoord * reach, lookVec.zCoord * reach);
    }

}
