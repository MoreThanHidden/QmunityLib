package uk.co.qmunity.lib.part;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import uk.co.qmunity.lib.part.compat.IMultipartCompat;

public interface IPartPlacement {

    public boolean placePart(IPart part, World world, BlockPos location, IMultipartCompat multipartSystem, boolean simulated);

}
