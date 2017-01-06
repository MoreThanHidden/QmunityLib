package uk.co.qmunity.lib.part;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class MultipartCompat {

    private static List<IMultipartSystem> multipartSystems = new ArrayList<IMultipartSystem>();
    private static Comparator<IMultipartSystem> sorter = new Comparator<IMultipartSystem>() {

        @Override
        public int compare(IMultipartSystem o1, IMultipartSystem o2) {

            return Integer.compare(o2.getPriority(), o1.getPriority());
        }
    };

    public static void registerMultipartSystem(IMultipartSystem system) {

        if (system == null)
            throw new NullPointerException("Attempted to register a null multipart system!");
        if (multipartSystems.contains(system))
            throw new IllegalStateException("Attempted to register a multipart system that was already registered!");

        multipartSystems.add(system);
        Collections.sort(multipartSystems, sorter);
    }

    public static boolean canAddPart(World world, BlockPos pos, IQLPart part) {

        for (IMultipartSystem system : multipartSystems)
            if (system.canAddPart(world, pos, part))
                return true;
        return false;
    }

    public static void addPart(World world, BlockPos pos, IQLPart part) {

        for (IMultipartSystem system : multipartSystems) {
            if (system.canAddPart(world, pos, part)) {
                system.addPart(world, pos, part);
                return;
            }
        }
    }

    public static void addPart(World world, BlockPos pos, IQLPart part, String partID) {

        for (IMultipartSystem system : multipartSystems) {
            if (system.canAddPart(world, pos, part)) {
                system.addPart(world, pos, part, partID);
                return;
            }
        }
    }

    public static IPartHolder getHolder(World world, BlockPos pos) {

        for (IMultipartSystem system : multipartSystems) {
            IPartHolder h = system.getHolder(world, pos);
            if (h != null)
                return h;
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    public static <T extends IQLPart> T getPart(World world, BlockPos pos, Class<T> clazz) {

        IPartHolder holder = getHolder(world, pos);
        if (holder == null)
            return null;
        for (IQLPart part : holder.getParts())
            if (clazz.isAssignableFrom(part.getClass()))
                return (T) part;
        return null;
    }

}
