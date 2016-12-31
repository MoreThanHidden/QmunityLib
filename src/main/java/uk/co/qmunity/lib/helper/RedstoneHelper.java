package uk.co.qmunity.lib.helper;

import net.minecraft.block.*;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntityComparator;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import uk.co.qmunity.lib.part.compat.IMultipartCompat;
import uk.co.qmunity.lib.part.compat.MultipartSystem;

public class RedstoneHelper {

    @Deprecated
    public static int getVanillaSignalStrength(World world, int x, int y, int z, EnumFacing side, EnumFacing face) {

        return getVanillaSignalStrength(world, new BlockPos(x, y, z), side, face);
    }

    public static int getVanillaSignalStrength(World world, BlockPos pos, EnumFacing side, EnumFacing face) {

        if (face != EnumFacing.DOWN && face != null)
            return 0;

        Block block = world.getBlockState(pos).getBlock();

        if (block == Blocks.REDSTONE_WIRE) {
            if (side == EnumFacing.DOWN)
                return world.getBlockState(pos).getBlock().getMetaFromState(world.getBlockState(pos));
            if (side == EnumFacing.UP)
                return 0;
            //Todo Check this as Movement Direction was removed.
            if (((BlockRedstoneWire)block).canProvidePower(world.getBlockState(pos))
                    || ((BlockRedstoneWire)block).canProvidePower(world.getBlockState(pos.offset(side).offset(side)))) {
                return world.getBlockState(pos).getBlock().getMetaFromState(world.getBlockState(pos));
            }
        }
        if (block instanceof BlockRedstoneComparator)

            if (block == Blocks.UNPOWERED_REPEATER) {
                return 0;
            }
        if (block == Blocks.POWERED_REPEATER) {
            if (side == EnumFacing.DOWN || side == EnumFacing.UP)
                return 0;
            return side.ordinal() == (world.getBlockState(pos).getBlock().getMetaFromState(world.getBlockState(pos)) % 4) ? 15 : 0;
        }
        if (block instanceof BlockRedstoneComparator) {
            if (side == EnumFacing.DOWN || side == EnumFacing.UP)
                return 0;
            //Todo Check this as Movement Direction was removed.
            return side.ordinal() == (world.getBlockState(pos).getBlock().getMetaFromState(world.getBlockState(pos)) % 4) ? ((TileEntityComparator) world.getTileEntity(pos)).getOutputSignal() : 0;
        }
        return 0;
    }

    @Deprecated
    public static boolean canConnectVanilla(World world, int x, int y, int z, EnumFacing side, EnumFacing face) {

        return canConnectVanilla(world, new BlockPos(x, y, z), side, face);
    }

    public static boolean canConnectVanilla(World world, BlockPos pos, EnumFacing side, EnumFacing face) {

        if (side == null)
            return false;

        Block block = world.getBlockState(pos).getBlock();
        IBlockState state = world.getBlockState(pos);
        int meta = state.getBlock().getMetaFromState(state);
        //Todo Check this as Movement Direction was removed.
        if ((block == Blocks.UNPOWERED_REPEATER || block == Blocks.POWERED_REPEATER)
                && (face == EnumFacing.DOWN || face == null))
            if (side.ordinal() % 2 == meta % 2)
                return true;

        if (block instanceof BlockLever) {
            meta = meta % 8;
            EnumFacing leverFace = ((meta == 0 || meta == 7) ? EnumFacing.UP : ((meta == 5 || meta == 6) ? EnumFacing.DOWN
                    : (meta == 1 ? EnumFacing.WEST : (meta == 2 ? EnumFacing.EAST : (meta == 3 ? EnumFacing.NORTH
                            : (meta == 4 ? EnumFacing.SOUTH : null))))));
            if (face != null && face != leverFace)
                return false;
            return side != leverFace.getOpposite();
        }

        if (block instanceof BlockRedstoneComparator && (face == EnumFacing.DOWN || face == null))
            return side != EnumFacing.UP;

        if (block instanceof BlockRedstoneWire)
            return face == null || face == EnumFacing.DOWN;

        return block instanceof BlockDoor || block instanceof BlockRedstoneLight || block instanceof BlockTNT
                || block instanceof BlockDispenser || block instanceof BlockNote
                || block instanceof BlockPistonBase;// true;
    }

    @Deprecated
    private static boolean isVanillaBlock(World world, int x, int y, int z) {

        return isVanillaBlock(world, new BlockPos(x, y, z));
    }

    private static boolean isVanillaBlock(World world, BlockPos pos) {

        Block b = world.getBlockState(pos).getBlock();
        return b instanceof BlockRedstoneRepeater || b instanceof BlockLever || b instanceof BlockRedstoneWire
                || b instanceof BlockRedstoneComparator || b instanceof BlockDoor || b instanceof BlockRedstoneLight
                || b instanceof BlockTNT || b instanceof BlockDispenser || b instanceof BlockNote
                || b instanceof BlockPistonBase;
    }

    @Deprecated
    public static int getOutputWeak(World world, int x, int y, int z, EnumFacing side, EnumFacing face) {

        return getOutputWeak(world, new BlockPos(x, y, z), side, face);
    }

    public static int getOutputWeak(World world, BlockPos pos, EnumFacing side, EnumFacing face) {

        for (MultipartSystem s : MultipartSystem.getAvailableSystems()) {
            IMultipartCompat compat = s.getCompat();
            if (compat.isMultipart(world, pos))
                return compat.getWeakRedstoneOuput(world, pos, side, face);
        }

        IBlockState state = world.getBlockState(pos);

        int power = state.getWeakPower(world, pos, side.getOpposite());
        if (power > 0)
            return power;

        if (state.isNormalCube() && state.isOpaqueCube()) {
            for (EnumFacing d : EnumFacing.VALUES) {
                if (d == side)
                    continue;
                power = Math.max(power,
                        getOutputStrong(world, pos.getX() + d.getFrontOffsetX(), pos.getY() + d.getFrontOffsetY(), pos.getZ() + d.getFrontOffsetZ(), d.getOpposite(), null));
            }
        }

        return power;
    }

    @Deprecated
    public static int getOutputStrong(World world, int x, int y, int z, EnumFacing side, EnumFacing face) {

        return getOutputStrong(world, new BlockPos(x, y, z), side, face);
    }

    public static int getOutputStrong(World world, BlockPos pos, EnumFacing side, EnumFacing face) {

        for (MultipartSystem s : MultipartSystem.getAvailableSystems()) {
            IMultipartCompat compat = s.getCompat();
            if (compat.isMultipart(world, pos))
                return compat.getStrongRedstoneOuput(world, pos, side, face);
        }

        int power = getVanillaSignalStrength(world, pos, side, face);
        if (power > 0)
            return power;

        return world.getBlockState(pos).getStrongPower(world, pos, side.getOpposite());
    }

    @Deprecated
    public static int getOutputWeak(World world, int x, int y, int z, EnumFacing side) {

        return getOutputWeak(world, new BlockPos(x, y, z), side, null);
    }

    public static int getOutputWeak(World world, BlockPos pos, EnumFacing side) {

        return getOutputWeak(world, pos, side, null);
    }

    @Deprecated
    public static int getOutputStrong(World world, int x, int y, int z, EnumFacing side) {

        return getOutputStrong(world, new BlockPos(x, y, z), side, null);
    }

    public static int getOutputStrong(World world, BlockPos pos, EnumFacing side) {

        return getOutputStrong(world, pos, side, null);
    }

    @Deprecated
    public static int getOutput(World world, int x, int y, int z, EnumFacing side) {

        return Math.max(getOutputWeak(world, new BlockPos(x, y, z), side), getOutputStrong(world, new BlockPos(x, y, z), side));
    }

    public static int getOutput(World world, BlockPos pos, EnumFacing side) {

        return Math.max(getOutputWeak(world, pos, side), getOutputStrong(world, pos, side));
    }

    @Deprecated
    public static int getOutput(World world, int x, int y, int z, EnumFacing side, EnumFacing face) {

        return Math.max(getOutputWeak(world, new BlockPos(x, y, z), side, face), getOutputStrong(world, new BlockPos(x, y, z), side, face));
    }

    public static int getOutput(World world, BlockPos pos, EnumFacing side, EnumFacing face) {

        return Math.max(getOutputWeak(world, pos, side, face), getOutputStrong(world, pos, side, face));
    }

    @Deprecated
    public static int getOutput(World world, int x, int y, int z) {

        return getOutput(world, new BlockPos(x, y, z));
    }

    public static int getOutput(World world, BlockPos pos) {

        int power = 0;
        for (EnumFacing side : EnumFacing.VALUES)
            power = Math.max(power, getOutput(world, pos, side));
        return power;
    }

    @Deprecated
    public static int getInputWeak(World world, int x, int y, int z, EnumFacing side, EnumFacing face) {

        return getOutputWeak(world, new BlockPos(x, y, z).offset(side), side.getOpposite(), face);
    }

    public static int getInputWeak(World world, BlockPos pos, EnumFacing side, EnumFacing face) {

        return getOutputWeak(world, pos.offset(side), side.getOpposite(), face);
    }

    @Deprecated
    public static int getInputStrong(World world, int x, int y, int z, EnumFacing side, EnumFacing face) {

        return getOutputStrong(world, new BlockPos(x, y, z).offset(side), side.getOpposite(), face);
    }

    public static int getInputStrong(World world, BlockPos pos, EnumFacing side, EnumFacing face) {

        return getOutputStrong(world, pos.offset(side), side.getOpposite(), face);
    }

    @Deprecated
    public static int getInputWeak(World world, int x, int y, int z, EnumFacing side) {

        return getOutputWeak(world, new BlockPos(x, y, z).offset(side), side.getOpposite());
    }

    public static int getInputWeak(World world, BlockPos pos, EnumFacing side) {

        return getOutputWeak(world, pos.offset(side), side.getOpposite());
    }

    @Deprecated
    public static int getInputStrong(World world, int x, int y, int z, EnumFacing side) {

        return getOutputStrong(world, new BlockPos(x, y, z).offset(side), side.getOpposite());
    }

    public static int getInputStrong(World world, BlockPos pos, EnumFacing side) {

        return getOutputStrong(world, pos.offset(side), side.getOpposite());
    }

    @Deprecated
    public static int getInput(World world, int x, int y, int z, EnumFacing side) {

        return getOutput(world, new BlockPos(x, y, z).offset(side), side.getOpposite());
    }

    public static int getInput(World world, BlockPos pos, EnumFacing side) {

        return getOutput(world, pos.offset(side), side.getOpposite());
    }

    @Deprecated
    public static int getInput(World world, int x, int y, int z, EnumFacing side, EnumFacing face) {

        return getOutput(world, new BlockPos(x, y, z).offset(side), side.getOpposite(), face);
    }

    public static int getInput(World world, BlockPos pos, EnumFacing side, EnumFacing face) {

        return getOutput(world, pos.offset(side), side.getOpposite(), face);
    }

    @Deprecated
    public static int getInput(World world, int x, int y, int z) {

        return getInput(world, new BlockPos(x, y, z));
    }

    public static int getInput(World world, BlockPos pos) {

        int power = 0;
        for (EnumFacing side : EnumFacing.VALUES)
            power = Math.max(power, getInput(world, pos, side));
        return power;
    }

    @Deprecated
    public static boolean canConnect(World world, int x, int y, int z, EnumFacing side, EnumFacing face) {

        return canConnect(world, new BlockPos(x, y, z), side, face);
    }

    public static boolean canConnect(World world, BlockPos pos, EnumFacing side, EnumFacing face) {

        if (isVanillaBlock(world, pos))
            return canConnectVanilla(world, pos, side, face);

        for (MultipartSystem s : MultipartSystem.getAvailableSystems()) {
            IMultipartCompat compat = s.getCompat();
            if (compat.isMultipart(world, pos))
                return compat.canConnectRedstone(world, pos, side, face);
        }

        try {
            return world.getBlockState(pos).getBlock().canConnectRedstone(world.getBlockState(pos), world, pos, side);
        } catch (Exception ex) {
            // ex.printStackTrace();
        }
        return false;
    }

    @Deprecated
    public static boolean canConnect(World world, int x, int y, int z, EnumFacing side) {

        return canConnect(world, new BlockPos(x, y, z), side, null);
    }

    public static boolean canConnect(World world, BlockPos pos, EnumFacing side) {

        return canConnect(world, pos, side, null);
    }

    @Deprecated
    public static void notifyRedstoneUpdate(World world, int x, int y, int z, EnumFacing direction, boolean strong) {

        notifyRedstoneUpdate(world, new BlockPos(x, y, z), direction, strong);
    }

    public static void notifyRedstoneUpdate(World world, BlockPos pos, EnumFacing direction, boolean strong) {

        int x_ = pos.getX() + direction.getFrontOffsetX();
        int y_ = pos.getY() + direction.getFrontOffsetY();
        int z_ = pos.getZ() + direction.getFrontOffsetZ();

        if (world == null)
            return;

        Block block = world.getBlockState(pos).getBlock();

        // Weak/strong
        world.notifyNeighborsOfStateChange(new BlockPos(x_, y_, z_), block, true);

        // Strong
        if (strong)
            for (EnumFacing d : EnumFacing.VALUES)
                if (d != direction.getOpposite())
                    world.notifyNeighborsOfStateChange(new BlockPos(x_, y_, z_).offset(d), block, true);
    }

}
