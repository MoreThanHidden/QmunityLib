package uk.co.qmunity.lib.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.client.event.DrawBlockHighlightEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import uk.co.qmunity.lib.QLModInfo;
import uk.co.qmunity.lib.QmunityLib;
import uk.co.qmunity.lib.client.render.RenderMultipart;
import uk.co.qmunity.lib.helper.ItemHelper;
import uk.co.qmunity.lib.part.IQLPart;
import uk.co.qmunity.lib.raytrace.QRayTraceResult;
import uk.co.qmunity.lib.raytrace.RayTracer;
import uk.co.qmunity.lib.tile.TileMultipart;
import uk.co.qmunity.lib.vec.Cuboid;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class BlockMultipart extends BlockContainer {

    AxisAlignedBB boundingBox;

    public BlockMultipart() {

        super(Material.GROUND);
        setUnlocalizedName(QLModInfo.MODID + ".multipart");
        MinecraftForge.EVENT_BUS.register(this);
    }

    @Override
    public TileEntity createNewTileEntity(World world, int meta) {

        return new TileMultipart();
    }

    public static TileMultipart findTile(IBlockAccess world, BlockPos pos) {

        TileEntity te = world.getTileEntity(pos);
        if (te == null || !(te instanceof TileMultipart))
            return null;
        return (TileMultipart) te;
    }

    @Override
    public boolean isNormalCube(IBlockState state, IBlockAccess world, BlockPos pos) {
        return false;
    }

    @Override
    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }

    @Override
    public EnumBlockRenderType getRenderType(IBlockState state) {
        return EnumBlockRenderType.ENTITYBLOCK_ANIMATED;
    }

    @SideOnly(Side.CLIENT)
    public int getRenderType() {

        return RenderMultipart.RENDER_ID;
    }

    @Nullable
    @Override
    public RayTraceResult collisionRayTrace(IBlockState blockState, World worldIn, BlockPos pos, Vec3d start, Vec3d end) {
        return retrace(worldIn, pos, start, end);
    }

    private QRayTraceResult retrace(World world, BlockPos pos, EntityPlayer player) {

        return retrace(world, pos, RayTracer.getStartVec(player), RayTracer.getEndVec(player));
    }

    private QRayTraceResult retrace(World world, BlockPos pos, Vec3d start, Vec3d end) {

        TileMultipart te = findTile(world, pos);
        if (te == null)
            return null;
        QRayTraceResult mop = te.rayTrace(start, end);
        if (mop == null)
            return null;
        return mop;
    }

    @Override
    public boolean addDestroyEffects(World world, BlockPos pos, ParticleManager manager) {
        TileMultipart te = findTile(world, pos);
        if (te == null)
            return true;
        QRayTraceResult mop = retrace(world, pos, QmunityLib.proxy.getPlayer());
        if (mop == null || mop.part == null || mop.part.getParent() != te)
            return true;
        mop.part.addDestroyEffects(mop, manager);
        return true;
    }

    @Override
    public boolean addHitEffects(IBlockState state, World world, RayTraceResult target, ParticleManager manager) {
        TileMultipart te = findTile(world, target.getBlockPos());
        if (te == null)
            return true;
        QRayTraceResult mop = retrace(world, target.getBlockPos(), QmunityLib.proxy.getPlayer());
        if (mop == null || mop.part == null || mop.part.getParent() != te)
            return true;
        mop.part.addHitEffects(mop, manager);
        return true;
    }

    @Override
    public int getLightValue(IBlockState state, IBlockAccess world, BlockPos pos) {
        TileMultipart te = findTile(world, pos);
        if (te == null)
            return 0;

        int light = 0;
        for (IQLPart p : te.getParts())
            light = Math.max(light, p.getLightValue());
        return light;
    }

    @Override
    public boolean removedByPlayer(IBlockState state, World world, BlockPos pos, EntityPlayer player, boolean willHarvest) {
        TileMultipart te = findTile(world, pos);
        QRayTraceResult mop = retrace(world, pos, player);

        if (te == null || mop == null) {
            if (world.isRemote)
                return true;
            for (ItemStack stack : getDrops(world, pos, state, 0))
                ItemHelper.dropItem(world, pos, stack);
            world.setBlockToAir(pos);
            world.removeTileEntity(pos);
            return true;
        }

        if (!world.isRemote)
            mop.part.harvest(player, mop);

        return world.getTileEntity(pos) == null;
    }

    @Override
    public void addCollisionBoxToList(IBlockState state, World worldIn, BlockPos pos, AxisAlignedBB entityBox, List<AxisAlignedBB> collidingBoxes, @Nullable Entity entityIn) {
        TileMultipart te = findTile(worldIn, pos);
        if (te == null)
            return;

        List<Cuboid> boxes = new ArrayList<Cuboid>();
        te.addCollisionBoxesToList(boxes, entityBox);
        for (Cuboid c : boxes)
            collidingBoxes.add(c.toAABB());
    }


    @Override
    public void onNeighborChange(IBlockAccess world, BlockPos pos, BlockPos neighbor) {
        TileMultipart te = findTile(world, pos);
        if (te == null)
            return;
        for (IQLPart p : te.getParts())
            p.onNeighborBlockChange();
    }

    @Override
    public void neighborChanged(IBlockState state, World world, BlockPos pos, Block blockIn, BlockPos fromPos) {
        TileMultipart te = findTile(world, pos);
        if (te == null)
            return;
        for (IQLPart p : te.getParts())
            p.onNeighborTileChange();
    }

    @Override
    public boolean isSideSolid(IBlockState base_state, IBlockAccess world, BlockPos pos, EnumFacing side) {
        TileMultipart te = findTile(world, pos);
        if (te == null)
            return false;

        return te.isSideSolid(side);
    }

    @Override
    public boolean canConnectRedstone(IBlockState state, IBlockAccess world, BlockPos pos, @Nullable EnumFacing side) {
        TileMultipart te = findTile(world, pos);
        if (te == null)
            return false;
        return te.canConnectRedstone(side);
    }

    @Override
    public int getWeakPower(IBlockState blockState, IBlockAccess blockAccess, BlockPos pos, EnumFacing side) {
        TileMultipart te = findTile(blockAccess, pos);
        if (te == null)
            return 0;
        return te.getWeakRedstoneOutput(side);
    }

    @Override
    public int getStrongPower(IBlockState blockState, IBlockAccess blockAccess, BlockPos pos, EnumFacing side) {
        TileMultipart te = findTile(blockAccess, pos);
        if (te == null)
            return 0;
        return te.getStrongRedstoneOutput(side);
    }

    @Override
    public ItemStack getPickBlock(IBlockState state, RayTraceResult target, World world, BlockPos pos, EntityPlayer player) {
        QRayTraceResult mop = retrace(world, pos, QmunityLib.proxy.getPlayer());
        if (mop == null || mop.part == null)
            return null;
        return mop.part.getPickBlock(QmunityLib.proxy.getPlayer(), mop);
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        QRayTraceResult mop = retrace(world, pos, QmunityLib.proxy.getPlayer());
        if (mop == null || mop.part == null)
            return false;
        return mop.part.onActivated(player, mop, player.getHeldItem(hand));
    }

    @Override
    public void onBlockClicked(World world, BlockPos pos, EntityPlayer player) {
        QRayTraceResult mop = retrace(world, pos, QmunityLib.proxy.getPlayer());
        if (mop == null || mop.part == null)
            return;
        mop.part.onClicked(player, mop,  player.getHeldItem(EnumHand.MAIN_HAND));
    }

    @Override
    public float getPlayerRelativeBlockHardness(IBlockState state, EntityPlayer player, World world, BlockPos pos) {
        QRayTraceResult mop = retrace(world, pos, player);
        if (mop == null || mop.part == null)
            return 1 / 100F;
        return 1F / mop.part.getHardness(player, mop);
    }

    @Override
    public List<ItemStack> getDrops(IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {
        ArrayList<ItemStack> l = new ArrayList<ItemStack>();

        TileMultipart te = findTile(world, pos);
        if (te != null)
            for (IQLPart p : te.getParts())
                l.addAll(p.getDrops());

        return l;
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    @SideOnly(Side.CLIENT)
    public void onDrawHighlight(DrawBlockHighlightEvent event) {

        try {
            if (!(event.getPlayer().world.getBlockState(event.getTarget().getBlockPos()).getBlock() instanceof BlockMultipart))
                return;

            QRayTraceResult mop = event.getTarget() instanceof QRayTraceResult ? (QRayTraceResult) event.getTarget() : retrace(
                    event.getPlayer().world, event.getTarget().getBlockPos(), event.getPlayer());
            if (mop == null || mop.part == null)
                return;
            if (mop.part.drawHighlight(mop, event.getPlayer(), event.getPartialTicks()))
                event.setCanceled(true);
        } catch (Exception ex) {
        }
    }

    @Override
    public void randomDisplayTick(IBlockState stateIn, World world, BlockPos pos, Random rand) {
        TileMultipart te = findTile(world, pos);
        if (te != null)
            for (IQLPart part : te.getParts())
                part.randomDisplayTick(rand);
    }

    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        return boundingBox;
    }

    public void setBlockBounds(AxisAlignedBB boundingBox) {
        this.boundingBox = boundingBox;
    }
}
