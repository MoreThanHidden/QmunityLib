package uk.co.qmunity.lib.block;

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
import uk.co.qmunity.lib.QmunityLib;
import uk.co.qmunity.lib.part.IPart;
import uk.co.qmunity.lib.part.IPartSelectableCustom;
import uk.co.qmunity.lib.raytrace.QRayTraceResult;
import uk.co.qmunity.lib.raytrace.RayTracer;
import uk.co.qmunity.lib.ref.Names;
import uk.co.qmunity.lib.tile.TileMultipart;
import uk.co.qmunity.lib.vec.Vec3dCube;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class BlockMultipart extends BlockContainer {

    AxisAlignedBB blockbounds = FULL_BLOCK_AABB;

    public BlockMultipart() {

        super(Material.GROUND);

        setRegistryName(Names.Unlocalized.Blocks.MULTIPART);

        MinecraftForge.EVENT_BUS.register(this);
    }

    @Override
    public TileEntity createNewTileEntity(World world, int meta) {

        return new TileMultipart();
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


    public static TileMultipart get(IBlockAccess world, BlockPos pos) {

        TileEntity te = world.getTileEntity(pos);
        if (te == null)
            return null;
        if (!(te instanceof TileMultipart))
            return null;
        return (TileMultipart) te;
    }

    @Nullable
    @Override
    public RayTraceResult collisionRayTrace(IBlockState blockState, World world, BlockPos pos, Vec3d start, Vec3d end) {
        return retrace(world, pos, start, end);
    }


    private QRayTraceResult retrace(World world, BlockPos pos, Vec3d start, Vec3d end) {

        TileMultipart te = get(world, pos);
        if (te == null)
            return null;

        QRayTraceResult mop = te.rayTrace(start, end);
        if (mop == null)
            return null;

        Vec3dCube c = mop.getCube().clone().expand(0.001);
        blockbounds = new AxisAlignedBB((float) c.getMinX(), (float) c.getMinY(), (float) c.getMinZ(), (float) c.getMaxX(), (float) c.getMaxY(),
                (float) c.getMaxZ());

        return mop;
    }

    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        return blockbounds;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean addDestroyEffects(World world, BlockPos pos, ParticleManager manager) {
        return true;
    }

    @Override
    public boolean addHitEffects(IBlockState state, World worldObj, RayTraceResult target, ParticleManager manager) {
        return true;
    }

    @Override
    public int getLightValue(IBlockState state, IBlockAccess world, BlockPos pos) {
        TileMultipart te = get(world, pos);
        if (te == null)
            return 0;

        return te.getLightValue();
    }

    @Override
    public boolean removedByPlayer(IBlockState state, World world, BlockPos pos, EntityPlayer player, boolean willHarvest) {
        TileMultipart te = get(world, pos);
        if (te == null || te.getParts().size() == 0) {
            world.removeTileEntity(pos);
            world.setBlockToAir(pos);
            return false;
        }

        return false;
    }

    @Override
    public void onBlockHarvested(World world, BlockPos pos, IBlockState state, EntityPlayer player) {
        if (!world.isRemote) {
            TileMultipart te = get(world, pos);
            if (te == null)
                return;
            te.removePart(player);
        }
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Override
    public void addCollisionBoxToList(IBlockState state, World world, BlockPos pos, AxisAlignedBB entityBox, List<AxisAlignedBB> collidingBoxes, @Nullable Entity entity) {
        TileMultipart te = get(world, pos);
        if (te == null)
            return;

        List<Vec3dCube> boxes = new ArrayList<Vec3dCube>();
        te.addCollisionBoxesToList(boxes, entityBox, entity);
        for (Vec3dCube c : boxes)
           collidingBoxes.add(c.toAABB());
    }


    @Override
    public void onNeighborChange(IBlockAccess world, BlockPos pos, BlockPos neighbor) {
        TileMultipart te = get(world, pos);
        if (te == null)
            return;

        te.onNeighborBlockChange();
    }

    @Override
    public boolean getWeakChanges(IBlockAccess world, BlockPos pos) {
        return true;
    }

    @Override
    public boolean isSideSolid(IBlockState base_state, IBlockAccess world, BlockPos pos, EnumFacing side) {
        TileMultipart te = get(world, pos);
        if (te == null)
            return false;

        return te.isSideSolid(side);
    }

    @Override
    public int getStrongPower(IBlockState blockState, IBlockAccess blockAccess, BlockPos pos, EnumFacing side) {
        TileMultipart te = get(blockAccess, pos);
        if (te == null)
            return 0;

        return te.getStrongOutput(side.getOpposite());
    }

    @Override
    public int getWeakPower(IBlockState blockState, IBlockAccess blockAccess, BlockPos pos, EnumFacing side) {
        TileMultipart te = get(blockAccess, pos);
        if (te == null)
            return 0;

        return te.getWeakOutput(side.getOpposite());
    }

    @Override
    public boolean canConnectRedstone(IBlockState state, IBlockAccess world, BlockPos pos, @Nullable EnumFacing side) {
        TileMultipart te = get(world, pos);
        if (te == null)
            return false;

        try {
            return te.canConnect(side.getOpposite());
        } catch (Exception ex) {
        }
        return false;
    }

    @Override
    public ItemStack getPickBlock(IBlockState state, RayTraceResult target, World world, BlockPos pos, EntityPlayer player) {
        TileMultipart te = get(world, pos);
        if (te == null)
            return null;

        return te.pickUp(QmunityLib.proxy.getPlayer());
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        TileMultipart te = get(world, pos);
        if (te == null)
            return false;

        return te.onActivated(player);
    }

    @Override
    public void onBlockClicked(World world, BlockPos pos, EntityPlayer player) {
        TileMultipart te = get(world, pos);
        if (te == null)
            return;

        te.onClicked(player);
    }

    @Override
    public float getPlayerRelativeBlockHardness(IBlockState state, EntityPlayer player, World world, BlockPos pos) {
        QRayTraceResult mop = retrace(world, pos, RayTracer.instance().getStartVector(player),
                RayTracer.instance().getEndVector(player));

        if (mop == null || mop.getPart() == null)
            return 1F;

        return (float) mop.getPart().getHardness(player, mop);
    }

    @Override
    public List<ItemStack> getDrops(IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {
        ArrayList<ItemStack> l = new ArrayList<ItemStack>();

        TileMultipart te = get(world, pos);
        if (te != null) {
            for (IPart p : te.getParts()) {
                List<ItemStack> d = p.getDrops();
                if (d != null)
                    l.addAll(d);
            }
        }

        return l;
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    @SideOnly(Side.CLIENT)
    public void onDrawHighlight(DrawBlockHighlightEvent event) {

        try {
            if (!(event.getPlayer().world.getBlockState(event.getTarget().getBlockPos()).getBlock() instanceof BlockMultipart))
                return;

            QRayTraceResult mop = retrace(event.getPlayer().world, event.getTarget().getBlockPos(),
                    RayTracer.instance().getStartVector(event.getPlayer()), RayTracer.instance().getEndVector(event.getPlayer()));
            if (mop == null)
                return;
            if (mop.getPart() == null || !(mop.getPart() instanceof IPartSelectableCustom))
                return;
            if (((IPartSelectableCustom) mop.getPart()).drawHighlight(mop, event.getPlayer(), event.getPartialTicks()))
                event.setCanceled(true);
        } catch (Exception ex) {
        }
    }
}
