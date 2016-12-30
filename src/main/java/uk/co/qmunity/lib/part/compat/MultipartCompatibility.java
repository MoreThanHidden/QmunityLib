package uk.co.qmunity.lib.part.compat;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import uk.co.qmunity.lib.part.*;
import uk.co.qmunity.lib.vec.Vec3dCube;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class MultipartCompatibility {

    public static boolean addPartToWorld(IPart part, World world, BlockPos location) {

        return addPartToWorld(part, world, location, false);
    }

    public static boolean addPartToWorld(IPart part, World world, BlockPos location, boolean simulated) {

        for (MultipartSystem s : MultipartSystem.getAvailableSystems())
            if (world.isAirBlock(location) || s.getCompat().isMultipart(world, location)
                    || s.getCompat().canBeMultipart(world, location))
                if (s.getCompat().addPartToWorld(part, world, location, simulated))
                    return true;
        return false;
    }

    public static boolean addPartToWorldBruteforce(IPart part, World world, BlockPos location) {

        for (MultipartSystem s : MultipartSystem.getAvailableSystems())
            if (world.isAirBlock(location) || s.getCompat().isMultipart(world, location)
                    || s.getCompat().canBeMultipart(world, location))
                if (s.getCompat().addPartToWorldBruteforce(part, world, location))
                    return true;
        return false;
    }

    public static boolean placePartInWorld(IPart part, World world, BlockPos location, EnumFacing clickedFace, EntityPlayer player,
                                           ItemStack item) {

        return placePartInWorld(part, world, location, clickedFace, player, item, false);
    }

    public static boolean placePartInWorld(IPart part, World world, BlockPos location, EnumFacing clickedFace, EntityPlayer player,
            ItemStack item, boolean simulated) {

        if (simulated)
            PartUpdateManager.setUpdatesEnabled(false);

        Map<IMultipartCompat, Integer> passes = new LinkedHashMap<IMultipartCompat, Integer>();
        int totalPasses = 0;
        for (MultipartSystem s : MultipartSystem.getAvailableSystems()) {
            IMultipartCompat c = s.getCompat();
            int p = c.getPlacementPasses();
            passes.put(c, p);
            totalPasses = Math.max(totalPasses, p);
        }

        for (int pass = 0; pass < totalPasses; pass++) {
            for (IMultipartCompat c : passes.keySet()) {
                if (pass >= passes.get(c))
                    continue;

                if (c.placePartInWorld(part, world, location, clickedFace, player, item, pass, simulated)) {
                    if (!player.capabilities.isCreativeMode && !simulated)
                        item.setCount(item.getCount() - 1);

                    if (simulated)
                        PartUpdateManager.setUpdatesEnabled(true);
                    return true;
                }
            }
        }

        if (simulated)
            PartUpdateManager.setUpdatesEnabled(true);

        return false;
    }

    public static ITilePartHolder getPartHolder(World world, BlockPos location) {

        if (world == null)
            return null;

        for (MultipartSystem s : MultipartSystem.getAvailableSystems())
            if (s.getCompat().isMultipart(world, location))
                return s.getCompat().getPartHolder(world, location);

        return null;
    }

    public static ITilePartHolder getPartHolder(World world, int x, int y, int z) {

        return getPartHolder(world, new BlockPos(x, y, z));
    }

    public static List<IMicroblock> getMicroblocks(World world, int x, int y, int z) {

        return getMicroblocks(world, new BlockPos(x, y, z));
    }

    public static List<IMicroblock> getMicroblocks(World world, BlockPos location) {

        List<IMicroblock> l = new ArrayList<IMicroblock>();

        for (MultipartSystem s : MultipartSystem.getAvailableSystems()) {
            List<IMicroblock> ls = s.getCompat().getMicroblocks(world, location);
            if (ls != null)
                for (IMicroblock m : ls)
                    if (!l.contains(m))
                        l.add(m);
        }

        return l;
    }

    public static IPart getPart(World world, BlockPos location, String type) {

        ITilePartHolder h = getPartHolder(world, location);
        if (h == null)
            return null;

        for (IPart p : h.getParts())
            if (p.getType() == type)
                return p;

        return null;
    }

    public static IPart getPart(World world, int x, int y, int z, String type) {

        return getPart(world, new BlockPos(x, y, z), type);
    }

    @SuppressWarnings("unchecked")
    public static <T> T getPart(World world, BlockPos location, Class<T> type) {

        ITilePartHolder h = getPartHolder(world, location);
        if (h == null)
            return null;

        for (IPart p : h.getParts())
            if (type.isAssignableFrom(p.getClass()))
                return (T) p;

        return null;
    }

    public static <T> T getPart(World world, int x, int y, int z, Class<T> type) {

        return getPart(world, new BlockPos(x, y, z), type);
    }

    public static boolean checkOcclusion(World world, BlockPos location, Vec3dCube cube) {

        for (MultipartSystem s : MultipartSystem.getAvailableSystems())
            if (s.getCompat().isMultipart(world, location) || s.getCompat().canBeMultipart(world, location))
                return s.getCompat().checkOcclusion(world, location, cube);

        return false;
    }

    public static boolean checkOcclusion(World world, int x, int y, int z, Vec3dCube cube) {

        return checkOcclusion(world, new BlockPos(x, y, z), cube);
    }

    public static IPartPlacement getPlacementForPart(IPart part, World world, BlockPos location, EnumFacing face,
                                                     RayTraceResult mop, EntityPlayer player) {

        IPartPlacement placement = null;

        if (!(part instanceof IPartCustomPlacement))
            return new PartPlacementDefault();
        placement = ((IPartCustomPlacement) part).getPlacement(part, world, location, face, mop, player);
        if (placement != null)
            return placement;

        return null;
    }
}
