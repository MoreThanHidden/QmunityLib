package uk.co.qmunity.lib.part;

import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import uk.co.qmunity.lib.part.compat.IMultipartCompat;

public class PartPlacementFace implements IPartPlacement {

    protected EnumFacing face;

    public PartPlacementFace(EnumFacing face) {

        this.face = face;
    }

    @Override
    public boolean placePart(IPart part, World world, BlockPos location, IMultipartCompat multipartSystem, boolean simulated) {

        if (part instanceof IPartFace)
            ((IPartFace) part).setFace(face);

        return multipartSystem.addPartToWorld(part, world, location, simulated);
    }

}
