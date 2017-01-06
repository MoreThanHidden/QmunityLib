package uk.co.qmunity.lib.part;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface IMultipartSystem {

    public int getPriority();

    public boolean canAddPart(World world, BlockPos pos, IQLPart part);

    public void addPart(World world, BlockPos pos, IQLPart part);

    public void addPart(World world, BlockPos pos, IQLPart part, String partID);

    public IPartHolder getHolder(World world, BlockPos pos);

}
