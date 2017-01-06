package uk.co.qmunity.lib.block;

import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import uk.co.qmunity.lib.tile.QLTileBase;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public abstract class QLBlockContainerBase extends QLBlockBase implements ITileEntityProvider {

    private int guiId = -1;
    private Class<? extends QLTileBase> tileClass;
    private boolean canProvidePower;

    public QLBlockContainerBase(Material material, Class<? extends QLTileBase> tileClass) {

        super(material);
        isBlockContainer = true;
        this.tileClass = tileClass;
    }

    public QLBlockContainerBase(Material material, Class<? extends QLTileBase> tileClass, String name) {

        super(material, name);
        isBlockContainer = true;
        this.tileClass = tileClass;
    }

    public QLBlockContainerBase setGuiId(int guiId) {

        this.guiId = guiId;
        return this;
    }

    public int getGuiId() {

        return guiId;
    }

    @Override
    public TileEntity createNewTileEntity(World world, int meta) {

        try {
            return tileClass.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private QLTileBase get(IBlockAccess world, BlockPos pos) {

        TileEntity tile = world.getTileEntity(pos);
        if (tile instanceof QLTileBase)
            return (QLTileBase) tile;
        return null;
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {

        QLTileBase te = get(world, pos);
        if (te == null)
            return false;

        if (getGuiId() >= 0) {
            if (!world.isRemote)
                player.openGui(getModInstance(), getGuiId(), world, pos.getX(), pos.getY(), pos.getZ());
            return true;
        }

        return te.onActivated(player, new RayTraceResult(new Vec3d(pos.getX() + hitX, pos.getY() + hitY, pos.getZ() + hitZ), side, pos),
                player.getHeldItem(hand));
    }

    @Override
    public void onBlockClicked(World world, BlockPos pos, EntityPlayer player) {
        QLTileBase te = get(world, pos);
        if (te == null)
            return;

        te.onClicked(player, player.getHeldItem(EnumHand.MAIN_HAND));
    }

    @Override
    public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
        QLTileBase te = get(world, pos);
        if (te == null)
            return;

        te.onPlacedBy(placer, stack);
    }

    @Override
    public void onNeighborChange(IBlockAccess world, BlockPos pos, BlockPos neighbor) {
        QLTileBase te = get(world, pos);
        if (te == null)
            return;

        te.onNeighborChange(world.getBlockState(pos).getBlock());
    }

    @Override
    public void neighborChanged(IBlockState state, World world, BlockPos pos, Block blockIn, BlockPos fromPos) {
        QLTileBase te = get(world, pos);
        if (te == null)
            return;

        te.onNeighborTileChange(world.getTileEntity(pos));
    }

    @Override
    public List<ItemStack> getDrops(IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {
        QLTileBase te = get(world, pos);
        if (te == null)
            return new ArrayList<ItemStack>();

        return te.getDrops();
    }

    @Override
    public boolean canProvidePower(IBlockState state) {
        return canProvidePower;
    }

    @Override
    public boolean canConnectRedstone(IBlockState state, IBlockAccess world, BlockPos pos, @Nullable EnumFacing side) {
        if (!canProvidePower(state))
            return false;

        QLTileBase te = get(world, pos);
        if (te != null)
            return te.canConnectRedstone(side);

        return false;
    }

    @Override
    public int getWeakPower(IBlockState state, IBlockAccess blockAccess, BlockPos pos, EnumFacing side) {
        if (!canProvidePower(state))
            return 0;

        QLTileBase te = get(blockAccess, pos);
        if (te != null)
            return te.getWeakRedstoneOutput(side);

        return 0;
    }

    @Override
    public int getStrongPower(IBlockState state, IBlockAccess blockAccess, BlockPos pos, EnumFacing side) {
        if (!canProvidePower(state))
            return 0;

        QLTileBase te = get(blockAccess, pos);
        if (te != null)
            return te.getStrongRedstoneOutput(side);

        return 0;
    }

    public void setCanProvidePower(boolean canProvidePower) {

        this.canProvidePower = canProvidePower;
    }

}
