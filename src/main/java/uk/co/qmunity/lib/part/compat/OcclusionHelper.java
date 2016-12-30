package uk.co.qmunity.lib.part.compat;

import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import uk.co.qmunity.lib.part.*;
import uk.co.qmunity.lib.vec.Vec3dCube;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class OcclusionHelper {

    public static PartNormallyOccluded getMicroblockPart(MicroblockShape shape, int size, EnumFacing... sides) {

        if (shape == MicroblockShape.FACE)
            return new PartNormallyOccluded(getFaceMicroblockBox(size, sides[0]));
        if (shape == MicroblockShape.FACE_HOLLOW)
            return new PartNormallyOccluded(getHollowFaceMicroblockBox(size, sides[0]));
        if (shape == MicroblockShape.EDGE)
            return new PartNormallyOccluded(getEdgeMicroblockBox(size, sides[0], sides[1]));
        if (shape == MicroblockShape.CORNER)
            return new PartNormallyOccluded(getCornerMicroblockBox(size, sides[0], sides[1], sides[2]));

        return new PartNormallyOccluded(new Vec3dCube(0, 0, 0, 0, 0, 0));
    }


    public static boolean microblockOcclusionTest(World world, BlockPos pos, MicroblockShape shape, int size, EnumFacing... sides) {

        List<IPart> parts = new ArrayList<IPart>();
        for (IPart part : MultipartCompatibility.getMicroblocks(world, pos))
            parts.add(part);

        return occlusionTest(parts, getMicroblockPart(shape, size, sides));
    }

    public static boolean microblockOcclusionTest(ITilePartHolder holder, MicroblockShape shape, int size, EnumFacing... sides) {

        return microblockOcclusionTest(holder, false, shape, size, sides);
    }

    public static boolean microblockOcclusionTest(ITilePartHolder holder, boolean allParts, MicroblockShape shape, int size,
            EnumFacing... sides) {

        List<IPart> parts = new ArrayList<IPart>();
        if (allParts)
            parts.addAll(holder.getParts());
        for (IPart part : holder.getMicroblocks())
            if (!parts.contains(part))
                parts.add(part);

        return occlusionTest(parts, getMicroblockPart(shape, size, sides));
    }

    public static boolean microblockOcclusionTest(ITilePartHolder holder, IMicroblock microblock) {

        List<IPart> parts = new ArrayList<IPart>();
        for (IPart part : holder.getMicroblocks())
            parts.add(part);

        return occlusionTest(parts, microblock);
    }

    public static Vec3dCube getFaceMicroblockBox(int size, EnumFacing face) {

        double s = (size * 2) / 16D;

        return new Vec3dCube(face.getFrontOffsetX() > 0 ? 1 - s : 0, face.getFrontOffsetY() > 0 ? 1 - s : 0, face.getFrontOffsetZ() > 0 ? 1 - s : 0, face.getFrontOffsetX() < 0 ? s
                : 1, face.getFrontOffsetY() < 0 ? s : 1, face.getFrontOffsetZ() < 0 ? s : 1);
    }

    public static Vec3dCube getHollowFaceMicroblockBox(int size, EnumFacing face) {

        double s = 2 / 16D;
        double d = size / 32D;

        return new Vec3dCube(face.getFrontOffsetX() > 0 ? 1 - s : (face.getFrontOffsetX() < 0 ? 0 : 0.5 - d), face.getFrontOffsetY() > 0 ? 1 - s : (face.getFrontOffsetY() < 0 ? 0
                : 0.5 - d), face.getFrontOffsetZ() > 0 ? 1 - s : (face.getFrontOffsetZ() < 0 ? 0 : 0.5 - d), face.getFrontOffsetX() < 0 ? s : (face.getFrontOffsetX() > 0 ? 1
                : 0.5 + d), face.getFrontOffsetY() < 0 ? s : (face.getFrontOffsetY() > 0 ? 1 : 0.5 + d), face.getFrontOffsetZ() < 0 ? s
                : (face.getFrontOffsetZ() > 0 ? 1 : 0.5 + d));
    }

    public static Vec3dCube getEdgeMicroblockBox(int size, EnumFacing side1, EnumFacing side2) {

        boolean x = side1.getFrontOffsetX() > 0 || side2.getFrontOffsetX() > 0;
        boolean y = side1.getFrontOffsetY() > 0 || side2.getFrontOffsetY() > 0;
        boolean z = side1.getFrontOffsetZ() > 0 || side2.getFrontOffsetZ() > 0;

        double s = (size * 2) / 16D;

        return new Vec3dCube((side1.getFrontOffsetX() == 0 && side2.getFrontOffsetX() == 0) ? s : (x ? 1 - s : 0),
                (side1.getFrontOffsetY() == 0 && side2.getFrontOffsetY() == 0) ? s : (y ? 1 - s : 0), (side1.getFrontOffsetZ() == 0 && side2.getFrontOffsetZ() == 0) ? s
                        : (z ? 1 - s : 0), (side1.getFrontOffsetX() == 0 && side2.getFrontOffsetX() == 0) ? 1 - s : (x ? 1 : s),
                (side1.getFrontOffsetY() == 0 && side2.getFrontOffsetY() == 0) ? 1 - s : (y ? 1 : s), (side1.getFrontOffsetZ() == 0 && side2.getFrontOffsetZ() == 0) ? 1 - s
                        : (z ? 1 : s));
    }

    public static Vec3dCube getCornerMicroblockBox(int size, EnumFacing side1, EnumFacing side2, EnumFacing side3) {

        boolean x = side1.getFrontOffsetX() > 0 || side2.getFrontOffsetX() > 0 || side3.getFrontOffsetX() > 0;
        boolean y = side1.getFrontOffsetY() > 0 || side2.getFrontOffsetY() > 0 || side3.getFrontOffsetY() > 0;
        boolean z = side1.getFrontOffsetZ() > 0 || side2.getFrontOffsetZ() > 0 || side3.getFrontOffsetZ() > 0;

        double s = (size * 2) / 16D;

        return new Vec3dCube(x ? 1 - s : 0, y ? 1 - s : 0, z ? 1 - s : 0, x ? 1 : s, y ? 1 : s, z ? 1 : s);
    }


    public static boolean occlusionTest(World world, BlockPos block, Collection<Vec3dCube> boxes) {

        ITilePartHolder holder = MultipartCompatibility.getPartHolder(world, block);

        if (holder != null)
            return occlusionTest(holder, boxes);

        return true;
    }

    public static boolean occlusionTest(ITilePartHolder holder, Collection<Vec3dCube> boxes) {

        for (Vec3dCube box : boxes)
            if (!occlusionTest(holder, box))
                return false;

        return true;
    }

    public static boolean occlusionTest(Collection<IPart> parts, Collection<Vec3dCube> boxes) {

        for (IPart p : parts)
            for (Vec3dCube box : boxes)
                if (!occlusionTest(p, box))
                    return false;

        return true;
    }

    public static boolean occlusionTest(IPart part, Collection<Vec3dCube> boxes) {

        for (Vec3dCube box : boxes)
            if (!occlusionTest(part, box))
                return false;

        return true;
    }

    public static boolean occlusionTest(World world, BlockPos block, Vec3dCube box) {

        ITilePartHolder holder = MultipartCompatibility.getPartHolder(world, block);

        if (holder != null)
            return occlusionTest(holder, box);

        return true;
    }

    public static boolean occlusionTest(ITilePartHolder holder, Vec3dCube box) {

        return occlusionTest(holder, new PartNormallyOccluded(box));
    }

    public static boolean occlusionTest(Collection<IPart> parts, Vec3dCube box) {

        return occlusionTest(parts, new PartNormallyOccluded(box));
    }

    public static boolean occlusionTest(IPart part, Vec3dCube box) {

        return occlusionTest(part, new PartNormallyOccluded(box));
    }

    public static boolean occlusionTest(World world, BlockPos block, IPart part) {

        ITilePartHolder holder = MultipartCompatibility.getPartHolder(world, block);

        if (holder != null)
            return occlusionTest(holder, part);

        return true;
    }

    public static boolean occlusionTest(ITilePartHolder holder, IPart part) {

        return occlusionTest(holder.getParts(), part);
    }

    public static boolean occlusionTest(Collection<IPart> parts, IPart part) {

        for (IPart p : parts)
            if (!p.occlusionTest(part) || !part.occlusionTest(p))
                return false;

        return true;
    }

    public static boolean occlusionTest(IPart part, IPart part2) {

        if (part instanceof IPartOccluding && part2 instanceof IPartOccluding) {
            IPartOccluding p1 = (IPartOccluding) part;
            IPartOccluding p2 = (IPartOccluding) part2;

            for (Vec3dCube c1 : p1.getOcclusionBoxes())
                for (Vec3dCube c2 : p2.getOcclusionBoxes())
                    if (!c1.occlusionTest(c2))
                        return false;
        }

        return true;
    }
}
