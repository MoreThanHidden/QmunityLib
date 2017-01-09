package uk.co.qmunity.lib.part;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import uk.co.qmunity.lib.QLBlocks;
import uk.co.qmunity.lib.block.BlockMultipart;
import uk.co.qmunity.lib.helper.RedstoneHelper.IQLRedstoneProvider;
import uk.co.qmunity.lib.tile.TileMultipart;
import uk.co.qmunity.lib.vec.Cuboid;

import java.util.ArrayList;
import java.util.List;

public class MultipartSystemStandalone implements IMultipartSystem, IQLRedstoneProvider {

    @Override
    public int getPriority() {

        return 1000;
    }

    @Override
    public boolean canAddPart(World world, BlockPos pos, IQLPart part) {

        for (Cuboid c : part.getCollisionBoxes())
            if (!world.checkNoEntityCollision(c.toAABB().offset(pos)))
                return false;

        TileMultipart te = BlockMultipart.findTile(world, pos);
        if (te != null)
            return te.canAddPart(part);

        IBlockState state = world.getBlockState(pos);
        Block block = state.getBlock();
        return block.isAir(state, world, pos) || block.isReplaceable(world, pos);
    }

    @Override
    public void addPart(World world, BlockPos pos, IQLPart part) {

        addPart(world, pos, part, null);
    }

    @Override
    public void addPart(World world, BlockPos pos, IQLPart part, String partID) {

        TileMultipart te = BlockMultipart.findTile(world, pos);

        if (te == null) {
            te = new TileMultipart();
            te.setWorld(world);
            te.setPos(pos);

            world.setBlockState(pos, QLBlocks.multipart.getDefaultState());
            world.setTileEntity(pos, te);
            te.firstTick = false;
        }

        if (partID != null)
            te.addPart(partID, part, true);
        else
            te.addPart(part);
    }

    @Override
    public TileMultipart getHolder(World world, BlockPos pos) {

        return BlockMultipart.findTile(world, pos);
    }

    @Override
    public List<IMicroblock> getMicroblocks(World world, BlockPos pos) {
        TileMultipart tmp = getHolder(world, pos);
        if (tmp != null)
            return tmp.getMicroblocks();

        return new ArrayList<IMicroblock>();
    }

    @Override
    public boolean canProvideRedstoneFor(World world, BlockPos pos) {

        return getHolder(world, pos) != null;
    }

    @Override
    public boolean canConnectRedstone(World world, BlockPos pos, EnumFacing face, EnumFacing side) {

        TileMultipart te = getHolder(world, pos);
        if (te != null)
            return te.canConnectRedstone(face, side);
        return false;
    }

    @Override
    public int getWeakRedstoneOutput(World world, BlockPos pos, EnumFacing face, EnumFacing side) {

        TileMultipart te = getHolder(world, pos);
        if (te != null)
            return te.getWeakRedstoneOutput(face, side);
        return 0;
    }

    @Override
    public int getStrongRedstoneOutput(World world, BlockPos pos, EnumFacing face, EnumFacing side) {

        TileMultipart te = getHolder(world, pos);
        if (te != null)
            return te.getStrongRedstoneOutput(face, side);
        return 0;
    }
}
