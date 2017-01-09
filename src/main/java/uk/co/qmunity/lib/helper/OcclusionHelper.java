package uk.co.qmunity.lib.helper;

import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import uk.co.qmunity.lib.part.*;
import uk.co.qmunity.lib.vec.Cuboid;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class OcclusionHelper {

    public static QLPart.QLPartNormallyOccluded getMicroblockPart(MicroblockShape shape, int size, EnumFacing... sides) {

        List<Cuboid> microblock = new ArrayList<>();

        if (shape == MicroblockShape.FACE)
            microblock.add(getFaceMicroblockBox(size, sides[0]));
        if (shape == MicroblockShape.FACE_HOLLOW)
            microblock.add(getHollowFaceMicroblockBox(size, sides[0]));
        if (shape == MicroblockShape.EDGE)
            microblock.add(getEdgeMicroblockBox(size, sides[0], sides[1]));
        if (shape == MicroblockShape.CORNER)
            microblock.add(getCornerMicroblockBox(size, sides[0], sides[1], sides[2]));
        if (microblock.size() == 0)
            microblock.add(new Cuboid(0, 0, 0, 0, 0, 0));
        return new QLPart.QLPartNormallyOccluded(microblock);
    }

    public static boolean microblockOcclusionTest(World world, BlockPos block, MicroblockShape shape, int size, EnumFacing... sides) {

        List<IQLPart> parts = new ArrayList<IQLPart>();
        for (IQLPart part : MultipartCompat.getMicroblocks(world, block))
            parts.add(part);

        return occlusionTest(parts, getMicroblockPart(shape, size, sides));
    }

    public static boolean microblockOcclusionTest(IPartHolder holder, MicroblockShape shape, int size, EnumFacing... sides) {

        return microblockOcclusionTest(holder, false, shape, size, sides);
    }

    public static boolean microblockOcclusionTest(IPartHolder holder, boolean allParts, MicroblockShape shape, int size,
                                                  EnumFacing... sides) {

        List<IQLPart> parts = new ArrayList<IQLPart>();
        if (allParts)
            parts.addAll(holder.getParts());
        for (IQLPart part : holder.getMicroblocks())
            if (!parts.contains(part))
                parts.add(part);

        return occlusionTest(parts, getMicroblockPart(shape, size, sides));
    }

    public static boolean microblockOcclusionTest(IPartHolder holder, IMicroblock microblock) {

        List<IQLPart> parts = new ArrayList<IQLPart>();
        for (IQLPart part : holder.getMicroblocks())
            parts.add(part);

        return occlusionTest(parts, microblock);
    }

    public static Cuboid getFaceMicroblockBox(int size, EnumFacing face) {

        double s = (size * 2) / 16D;

        return new Cuboid(face.getFrontOffsetX() > 0 ? 1 - s : 0, face.getFrontOffsetY() > 0 ? 1 - s : 0, face.getFrontOffsetZ() > 0 ? 1 - s : 0, face.getFrontOffsetX() < 0 ? s
                : 1, face.getFrontOffsetY() < 0 ? s : 1, face.getFrontOffsetZ() < 0 ? s : 1);
    }

    public static Cuboid getHollowFaceMicroblockBox(int size, EnumFacing face) {

        double s = 2 / 16D;
        double d = size / 32D;

        return new Cuboid(face.getFrontOffsetX() > 0 ? 1 - s : (face.getFrontOffsetX() < 0 ? 0 : 0.5 - d), face.getFrontOffsetY() > 0 ? 1 - s : (face.getFrontOffsetY() < 0 ? 0
                : 0.5 - d), face.getFrontOffsetZ() > 0 ? 1 - s : (face.getFrontOffsetZ() < 0 ? 0 : 0.5 - d), face.getFrontOffsetX() < 0 ? s : (face.getFrontOffsetX() > 0 ? 1
                : 0.5 + d), face.getFrontOffsetY() < 0 ? s : (face.getFrontOffsetY() > 0 ? 1 : 0.5 + d), face.getFrontOffsetZ() < 0 ? s
                : (face.getFrontOffsetZ() > 0 ? 1 : 0.5 + d));
    }

    public static Cuboid getEdgeMicroblockBox(int size, EnumFacing side1, EnumFacing side2) {

        boolean x = side1.getFrontOffsetX() > 0 || side2.getFrontOffsetX() > 0;
        boolean y = side1.getFrontOffsetY() > 0 || side2.getFrontOffsetY() > 0;
        boolean z = side1.getFrontOffsetZ() > 0 || side2.getFrontOffsetZ() > 0;

        double s = (size * 2) / 16D;

        return new Cuboid((side1.getFrontOffsetX() == 0 && side2.getFrontOffsetX() == 0) ? s : (x ? 1 - s : 0),
                (side1.getFrontOffsetY() == 0 && side2.getFrontOffsetY() == 0) ? s : (y ? 1 - s : 0), (side1.getFrontOffsetZ() == 0 && side2.getFrontOffsetZ() == 0) ? s
                : (z ? 1 - s : 0), (side1.getFrontOffsetX() == 0 && side2.getFrontOffsetX() == 0) ? 1 - s : (x ? 1 : s),
                (side1.getFrontOffsetY() == 0 && side2.getFrontOffsetY() == 0) ? 1 - s : (y ? 1 : s), (side1.getFrontOffsetZ() == 0 && side2.getFrontOffsetZ() == 0) ? 1 - s
                : (z ? 1 : s));
    }

    public static Cuboid getCornerMicroblockBox(int size, EnumFacing side1, EnumFacing side2, EnumFacing side3) {

        boolean x = side1.getFrontOffsetX() > 0 || side2.getFrontOffsetX() > 0 || side3.getFrontOffsetX() > 0;
        boolean y = side1.getFrontOffsetY()> 0 || side2.getFrontOffsetY() > 0 || side3.getFrontOffsetY() > 0;
        boolean z = side1.getFrontOffsetZ() > 0 || side2.getFrontOffsetZ() > 0 || side3.getFrontOffsetZ() > 0;

        double s = (size * 2) / 16D;

        return new Cuboid(x ? 1 - s : 0, y ? 1 - s : 0, z ? 1 - s : 0, x ? 1 : s, y ? 1 : s, z ? 1 : s);
    }

    public static boolean occlusionTest(World world, BlockPos block, Collection<Cuboid> boxes) {

        IPartHolder holder = MultipartCompat.getHolder(world, block);

        if (holder != null)
            return occlusionTest(holder, boxes);

        return true;
    }

    public static boolean occlusionTest(IPartHolder holder, Collection<Cuboid> boxes) {

        for (Cuboid box : boxes)
            if (!occlusionTest(holder, box))
                return false;

        return true;
    }

    public static boolean occlusionTest(Collection<IQLPart> parts, Collection<Cuboid> boxes) {

        for (IQLPart p : parts)
            for (Cuboid box : boxes)
                if (!occlusionTest(p, box))
                    return false;

        return true;
    }

    public static boolean occlusionTest(IQLPart part, Collection<Cuboid> boxes) {

        for (Cuboid box : boxes)
            if (!occlusionTest(part, box))
                return false;

        return true;
    }

    public static boolean occlusionTest(World world, BlockPos block, Cuboid box) {

        IPartHolder holder = MultipartCompat.getHolder(world, block);

        if (holder != null)
            return occlusionTest(holder, box);

        return true;
    }

    public static boolean occlusionTest(IPartHolder holder, Cuboid box) {
        List<Cuboid> cubes = new ArrayList<>();
        cubes.add(box);
        return occlusionTest(holder, new QLPart.QLPartNormallyOccluded(cubes));
    }

    public static boolean occlusionTest(Collection<IQLPart> parts, Cuboid box) {
        List<Cuboid> cubes = new ArrayList<>();
        cubes.add(box);
        return occlusionTest(parts,new QLPart.QLPartNormallyOccluded(cubes));
    }

    public static boolean occlusionTest(IQLPart part, Cuboid box) {
        List<Cuboid> cubes = new ArrayList<>();
        cubes.add(box);
        return occlusionTest(part, new QLPart.QLPartNormallyOccluded(cubes));
    }

    public static boolean occlusionTest(World world, BlockPos block, IQLPart part) {

        IPartHolder holder = MultipartCompat.getHolder(world, block);

        if (holder != null)
            return occlusionTest(holder, part);

        return true;
    }

    public static boolean occlusionTest(IPartHolder holder, IQLPart part) {

        return occlusionTest(holder.getParts(), part);
    }

    public static boolean occlusionTest(Collection<IQLPart> parts, IQLPart part) {

        for (IQLPart p : parts)
            if (!p.occlusionTest(part) || !part.occlusionTest(p))
                return false;

        return true;
    }

    public static boolean occlusionTest(IQLPart part, IQLPart part2) {

        if (part instanceof IOccludingPart && part2 instanceof IOccludingPart) {
            IOccludingPart p1 = (IOccludingPart) part;
            IOccludingPart p2 = (IOccludingPart) part2;

            for (Cuboid c1 : p1.getOcclusionBoxes())
                for (Cuboid c2 : p2.getOcclusionBoxes())
                    if (!c1.occlusionTest(c2))
                        return false;
        }

        return true;
    }
}