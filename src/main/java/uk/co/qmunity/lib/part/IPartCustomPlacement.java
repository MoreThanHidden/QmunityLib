package uk.co.qmunity.lib.part;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

/**
 * Inteface implemented by parts that require custom placement, such as face parts or parts that need to know the user that placed them.
 *
 * @author amadornes
 */
public interface IPartCustomPlacement extends IPart {

    /**
     * Gets the placement for a part that's been placed at the specified location with the rest of the placement data.
     */
    public IPartPlacement getPlacement(IPart part, World world, BlockPos location, EnumFacing face, RayTraceResult mop,
                                       EntityPlayer player);

}
