package uk.co.qmunity.lib.part;

import net.minecraft.item.ItemStack;
import uk.co.qmunity.lib.part.compat.OcclusionHelper;
import uk.co.qmunity.lib.vec.Vec3dCube;

import java.util.Arrays;
import java.util.List;

public final class PartNormallyOccluded extends PartBase implements IPartOccluding {

    private List<Vec3dCube> cubes;

    public PartNormallyOccluded(Vec3dCube cube) {

        cubes = Arrays.asList(cube);
    }

    public PartNormallyOccluded(List<Vec3dCube> cubes) {

        this.cubes = cubes;
    }

    @Override
    public String getType() {

        return null;
    }

    @Override
    public ItemStack getItem() {

        return ItemStack.EMPTY;
    }

    @Override
    public List<Vec3dCube> getOcclusionBoxes() {

        return cubes;
    }

    @Override
    public boolean occlusionTest(IPart part) {

        return OcclusionHelper.occlusionTest(this, part);
    }
}
