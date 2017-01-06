package uk.co.qmunity.lib.raytrace;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import uk.co.qmunity.lib.helper.MathHelper;
import uk.co.qmunity.lib.part.IQLPart;
import uk.co.qmunity.lib.vec.Cuboid;
import uk.co.qmunity.lib.vec.Vector3;

import java.util.ArrayList;
import java.util.List;

/**
 * Most of this class was made by ChickenBones for CodeChickenLib but has been adapted for use in QmunityLib.<br>
 * You can find the original source at http://github.com/Chicken-Bones/CodeChickenLib
 */
public class RayTracer {
    private Vector3 vec = new Vector3();
    private Vector3 vec2 = new Vector3();

    private Vector3 s_vec = new Vector3();
    private double s_dist;
    private int s_side;
    private Cuboid c_cuboid;

    private static ThreadLocal<RayTracer> t_inst = new ThreadLocal<RayTracer>();

    public static RayTracer instance() {

        RayTracer inst = t_inst.get();
        if (inst == null)
            t_inst.set(inst = new RayTracer());
        return inst;
    }

    private void traceSide(int side, Vector3 start, Vector3 end, Cuboid cuboid) {

        vec.set(start);
        Vector3 hit = null;
        switch (side) {
        case 0:
            hit = vec.XZintercept(end, cuboid.min.y);
            break;
        case 1:
            hit = vec.XZintercept(end, cuboid.max.y);
            break;
        case 2:
            hit = vec.XYintercept(end, cuboid.min.z);
            break;
        case 3:
            hit = vec.XYintercept(end, cuboid.max.z);
            break;
        case 4:
            hit = vec.YZintercept(end, cuboid.min.x);
            break;
        case 5:
            hit = vec.YZintercept(end, cuboid.max.x);
            break;
        }
        if (hit == null)
            return;

        switch (side) {
        case 0:
        case 1:
            if (!MathHelper.isBetween(cuboid.min.x, hit.x, cuboid.max.x) || !MathHelper.isBetween(cuboid.min.z, hit.z, cuboid.max.z))
                return;
            break;
        case 2:
        case 3:
            if (!MathHelper.isBetween(cuboid.min.x, hit.x, cuboid.max.x) || !MathHelper.isBetween(cuboid.min.y, hit.y, cuboid.max.y))
                return;
            break;
        case 4:
        case 5:
            if (!MathHelper.isBetween(cuboid.min.y, hit.y, cuboid.max.y) || !MathHelper.isBetween(cuboid.min.z, hit.z, cuboid.max.z))
                return;
            break;
        }

        double dist = vec2.set(hit).sub(start).magSq();
        if (dist < s_dist) {
            s_side = side;
            s_dist = dist;
            s_vec.set(vec);
        }
    }

    public QRayTraceResult rayTraceCuboid(Vector3 start, Vector3 end, Cuboid cuboid) {

        s_dist = Double.MAX_VALUE;
        s_side = -1;

        for (int i = 0; i < 6; i++)
            traceSide(i, start, end, cuboid);

        if (s_side < 0)
            return null;

        QRayTraceResult mop = new QRayTraceResult(new RayTraceResult(s_vec.toVec3(), EnumFacing.getFront(s_side), new BlockPos(0,0,0)), cuboid);
        mop.typeOfHit = null;
        return mop;
    }

    public QRayTraceResult rayTraceCuboid(Vector3 start, Vector3 end, Cuboid cuboid, BlockPos pos) {

        QRayTraceResult mop = rayTraceCuboid(start, end, cuboid);
        if (mop != null) {
            mop.typeOfHit = RayTraceResult.Type.BLOCK;
            mop = new QRayTraceResult(new RayTraceResult(mop.hitVec, mop.sideHit, pos), mop.part, mop.cube);
        }
        return mop;
    }

    public QRayTraceResult rayTraceCuboid(Vector3 start, Vector3 end, Cuboid cuboid, Entity e) {

        QRayTraceResult mop = rayTraceCuboid(start, end, cuboid);
        if (mop != null) {
            mop.typeOfHit = RayTraceResult.Type.ENTITY;
            mop.entityHit = e;
        }
        return mop;
    }

    public QRayTraceResult rayTraceCuboids(Vector3 start, Vector3 end, List<Cuboid> cuboids) {

        double c_dist = Double.MAX_VALUE;
        QRayTraceResult c_hit = null;

        for (Cuboid cuboid : cuboids) {
            QRayTraceResult mop = rayTraceCuboid(start, end, cuboid);
            if (mop != null && s_dist < c_dist) {
                mop = new QRayTraceResult(mop, cuboid);
                c_dist = s_dist;
                c_hit = mop;
                c_cuboid = cuboid;
            }
        }

        return c_hit;
    }

    public QRayTraceResult rayTraceCuboids(Vector3 start, Vector3 end, List<Cuboid> cuboids, BlockPos pos, Block block) {

        QRayTraceResult mop = rayTraceCuboids(start, end, cuboids);
        if (mop != null) {
            mop.typeOfHit = RayTraceResult.Type.BLOCK;
            mop = new QRayTraceResult(new RayTraceResult(mop.hitVec, mop.sideHit, pos), mop.part, mop.cube);
            mop.cube = new Cuboid(c_cuboid.toAABB());
            if (block != null)
                c_cuboid.add(new Vector3(-pos.getX(), -pos.getY(), -pos.getZ())).setBlockBounds(block);
        }
        return mop;
    }

    public void rayTraceCuboids(Vector3 start, Vector3 end, List<Cuboid> cuboids, BlockPos pos, Block block,
            List<QRayTraceResult> hitList) {

        for (Cuboid cuboid : cuboids) {
            RayTraceResult mop = rayTraceCuboid(start, end, cuboid);
            if (mop != null) {
                QRayTraceResult qmop = new QRayTraceResult(mop, cuboid);
                qmop.typeOfHit = RayTraceResult.Type.BLOCK;
                qmop = new QRayTraceResult(new RayTraceResult(qmop.hitVec, qmop.sideHit, pos), qmop.part, qmop.cube);
                hitList.add(qmop);
            }
        }
    }

    public QRayTraceResult rayTracePart(IQLPart part, Vector3 start, Vector3 end) {

        Vector3 translation = new Vector3(part.getPos());
        List<Cuboid> cuboids = new ArrayList<Cuboid>();
        for (Cuboid c : part.getSelectionBoxes())
            cuboids.add(c.copy().add(translation));
        QRayTraceResult mop = rayTraceCuboids(start, end, cuboids, part.getPos(), part.getParent() != null ? part.getParent()
                .getBlockType() : null);
        if (mop != null)
            mop.part = part;
        return mop;
    }

    public static RayTraceResult retraceBlock(World world, EntityPlayer player, BlockPos pos) {

        Block block = world.getBlockState(pos).getBlock();
        IBlockState state = world.getBlockState(pos);

        Vec3d headVec = getCorrectedHeadVec(player);
        Vec3d lookVec = player.getLook(1.0F);
        double reach = getBlockReachDistance(player);
        Vec3d endVec = headVec.addVector(lookVec.xCoord * reach, lookVec.yCoord * reach, lookVec.zCoord * reach);
        return block.collisionRayTrace(state, world, pos, headVec, endVec);
    }

    private static double getBlockReachDistance_server(EntityPlayerMP player) {

        return player.interactionManager.getBlockReachDistance();
    }

    @SideOnly(Side.CLIENT)
    private static double getBlockReachDistance_client() {

        return Minecraft.getMinecraft().playerController.getBlockReachDistance();
    }

    public static RayTraceResult reTrace(World world, EntityPlayer player) {

        return reTrace(world, player, getBlockReachDistance(player));
    }

    public static RayTraceResult reTrace(World world, EntityPlayer player, double reach) {

        Vec3d headVec = getCorrectedHeadVec(player);
        Vec3d lookVec = player.getLook(1);
        Vec3d endVec = headVec.addVector(lookVec.xCoord * reach, lookVec.yCoord * reach, lookVec.zCoord * reach);
        return world.rayTraceBlocks(headVec, endVec, true, false, true);
    }

    public static Vec3d getCorrectedHeadVec(EntityPlayer player) {

        double x = player.posX;
        double y = player.posY;
        double z = player.posZ;

        if (player.world.isRemote) {
            y += player.getEyeHeight() - player.getDefaultEyeHeight();// compatibility with eye height changing mods
        } else {
            y += player.getEyeHeight();
            if (player instanceof EntityPlayerMP && player.isSneaking())
                y -= 0.08;
        }
        return new Vec3d(x,y,z);
    }

    public static Vec3d getStartVec(EntityPlayer player) {

        return getCorrectedHeadVec(player);
    }

    public static double getBlockReachDistance(EntityPlayer player) {

        return player.world.isRemote ? getBlockReachDistance_client()
                : player instanceof EntityPlayerMP ? getBlockReachDistance_server((EntityPlayerMP) player) : 5D;
    }

    public static Vec3d getEndVec(EntityPlayer player) {

        Vec3d headVec = getCorrectedHeadVec(player);
        Vec3d lookVec = player.getLook(1.0F);
        double reach = getBlockReachDistance(player);
        return headVec.addVector(lookVec.xCoord * reach, lookVec.yCoord * reach, lookVec.zCoord * reach);
    }

}
