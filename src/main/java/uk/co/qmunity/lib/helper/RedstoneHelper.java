package uk.co.qmunity.lib.helper;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;

public class RedstoneHelper {

    public static interface IQLRedstoneProvider {

        public boolean canProvideRedstoneFor(World world, BlockPos pos);

        public boolean canConnectRedstone(World world, BlockPos pos, EnumFacing face, EnumFacing side);

        public int getWeakRedstoneOutput(World world, BlockPos pos, EnumFacing face, EnumFacing side);

        public int getStrongRedstoneOutput(World world, BlockPos pos, EnumFacing face, EnumFacing side);

    }

    private static List<IQLRedstoneProvider> providers = new ArrayList<IQLRedstoneProvider>();

    public static void registerProvider(IQLRedstoneProvider provider) {

        if (provider == null)
            throw new NullPointerException("Attempted to register a null redstone provider!");
        if (providers.contains(provider))
            throw new IllegalStateException("Attempted to register a redstone provider that was already registered!");

        providers.add(provider);
    }

    public static boolean canConnectRedstone(World world, BlockPos pos, EnumFacing side) {

        return canConnectRedstone(world, pos, null, side);
    }

    public static boolean canConnectRedstone(World world, BlockPos pos, EnumFacing face, EnumFacing side) {

        boolean provided = false;
        for (IQLRedstoneProvider provider : providers) {
            if (provider.canProvideRedstoneFor(world, pos)) {
                if (provider.canConnectRedstone(world, pos, face, side))
                    return true;
                provided = true;
            }
        }
        if (!provided)
            return world.getBlockState(pos).canProvidePower();
        return false;
    }

    public static int getWeakRedstoneOutput(World world, BlockPos pos, EnumFacing side) {

        return getWeakRedstoneOutput(world, pos, null, side);
    }

    public static int getWeakRedstoneOutput(World world, BlockPos pos, EnumFacing face, EnumFacing side) {

        int pow = 0;
        boolean provided = false;
        for (IQLRedstoneProvider provider : providers) {
            if (provider.canProvideRedstoneFor(world, pos)) {
                pow = Math.max(pow, provider.getWeakRedstoneOutput(world, pos, face, side));
                provided = true;
            }
        }
        if (!provided) {
            IBlockState s = world.getBlockState(pos);
            pow = s.getWeakPower(world, pos, side);
            if (s.isBlockNormalCube())
                for (EnumFacing d : EnumFacing.VALUES)
                    pow = Math.max(
                            pow,
                            getStrongRedstoneOutput(world, pos.offset(d), null,
                                    d.getOpposite()));
        }
        return pow;
    }

    public static int getStrongRedstoneOutput(World world, BlockPos pos, EnumFacing side) {

        return getStrongRedstoneOutput(world, pos, null, side);
    }

    public static int getStrongRedstoneOutput(World world, BlockPos pos, EnumFacing face, EnumFacing side) {

        int pow = 0;
        boolean provided = false;
        for (IQLRedstoneProvider provider : providers) {
            if (provider.canProvideRedstoneFor(world, pos)) {
                pow = Math.max(pow, provider.getStrongRedstoneOutput(world, pos, face, side));
                provided = true;
            }
        }
        if (!provided)
            return world.getBlockState(pos).getWeakPower(world, pos, side);
        return pow;
    }

    public static int getWeakRedstoneInput(World world, BlockPos pos) {

        int pow = 0;
        for (EnumFacing side : EnumFacing.VALUES)
            pow = Math.max(pow, getWeakRedstoneInput(world, pos, side));
        return pow;
    }

    public static int getWeakRedstoneInput(World world, BlockPos pos, EnumFacing side) {

        return getWeakRedstoneOutput(world, pos.offset(side), side.getOpposite());
    }

    public static int getWeakRedstoneInput(World world, BlockPos pos, EnumFacing face, EnumFacing side) {

        return getWeakRedstoneOutput(world, pos.offset(side), face, side.getOpposite());
    }

    public static int getStrongRedstoneInput(World world, BlockPos pos) {

        int pow = 0;
        for (EnumFacing side : EnumFacing.VALUES)
            pow = Math.max(pow, getStrongRedstoneInput(world, pos, side));
        return pow;
    }

    public static int getStrongRedstoneInput(World world, BlockPos pos, EnumFacing side) {

        return getStrongRedstoneOutput(world, pos.offset(side), side.getOpposite());
    }

    public static int getStrongRedstoneInput(World world, BlockPos pos, EnumFacing face, EnumFacing side) {

        return getStrongRedstoneOutput(world, pos.offset(side), face, side.getOpposite());
    }

}
