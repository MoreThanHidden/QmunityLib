package uk.co.qmunity.lib.vec;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface IWorldLocation {

    public World getWorld();

    public BlockPos getPos();

}
