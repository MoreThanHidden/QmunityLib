package uk.co.qmunity.lib.util;

import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public abstract class LocationCache<CachedType> {

    private final CachedType[] cachedValue;

    @SuppressWarnings("unchecked")
    public LocationCache(World world, BlockPos pos, Object... extraArgs) {

        if (world == null)
            throw new NullPointerException("World can't be null!");
        cachedValue = (CachedType[]) new Object[6];
        for (EnumFacing d : EnumFacing.VALUES) {
            cachedValue[d.ordinal()] = getNewValue(world, pos.offset(d), extraArgs);
        }
    }

    protected abstract CachedType getNewValue(World world, BlockPos pos, Object... extraArgs);

    public CachedType getValue(EnumFacing side) {

        return cachedValue[side.ordinal()];
    }
}
