package uk.co.qmunity.lib.util;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class TileEntityCache extends LocationCache<TileEntity> {

    public TileEntityCache(World world, BlockPos pos) {

        super(world, pos);
    }

    @Override
    protected TileEntity getNewValue(World world, BlockPos pos, Object... extraArgs) {

        return world.getTileEntity(pos);
    }

}
