package uk.co.qmunity.lib.part.compat.standalone;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import uk.co.qmunity.lib.block.BlockMultipart;
import uk.co.qmunity.lib.init.QLBlocks;
import uk.co.qmunity.lib.part.*;
import uk.co.qmunity.lib.part.compat.IMultipartCompat;
import uk.co.qmunity.lib.part.compat.MultipartCompatibility;
import uk.co.qmunity.lib.raytrace.RayTracer;
import uk.co.qmunity.lib.tile.TileMultipart;
import uk.co.qmunity.lib.vec.Vec3dCube;

import java.util.ArrayList;
import java.util.List;

public class StandaloneCompat implements IMultipartCompat {

    @Override
    public boolean addPartToWorld(IPart part, World world, BlockPos location, boolean simulated) {

        TileMultipart te = BlockMultipart.get(world, location);
        boolean newTe = false;
        if (te == null) {
            te = new TileMultipart(simulated);
            newTe = true;
        }
        te.setPos(location);
        te.setWorld(world);

        if (!te.canAddPart(part))
            return false;

        if (!simulated) {
            if (!world.isRemote) {
                if (newTe) {
                    world.setBlockState(location, QLBlocks.multipart.getDefaultState());
                    world.setTileEntity(location, te);
                }
                te.addPart(part);
            }
        } else {
            part.setParent(te);
        }

        return true;
    }

    @Override
    public boolean addPartToWorldBruteforce(IPart part, World world, BlockPos location) {

        TileMultipart te = BlockMultipart.get(world, location);
        boolean newTe = false;
        if (te == null) {
            te = new TileMultipart();
            te.setPos(location);
            te.setWorld(world);
            newTe = true;
        }

        if (!world.isRemote) {
            if (newTe) {
                world.setBlockState(location, QLBlocks.multipart.getDefaultState());
                world.setTileEntity(location, te);
            }
        }
        te.addPart(part);

        return true;
    }

    @Override
    public boolean placePartInWorld(IPart part, World world, BlockPos location, EnumFacing clickedFace, EntityPlayer player, ItemStack item,
                                    int pass, boolean simulated) {

        if (pass == 0 && player.isSneaking())
            return false;

        RayTraceResult mop = world.getBlockState(location).collisionRayTrace(world, location, RayTracer.getStartVector(player), RayTracer.getEndVector(player));
        if (mop == null)
            return false;

        boolean solidFace = false;
        double x = mop.hitVec.xCoord - mop.getBlockPos().getX();
        double y = mop.hitVec.yCoord - mop.getBlockPos().getY();
        double z = mop.hitVec.zCoord - mop.getBlockPos().getZ();
        if (x < 0)
            x += 1;
        if (y < 0)
            y += 1;
        if (z < 0)
            z += 1;

        switch (clickedFace) {
        case DOWN:
            if (y <= 0)
                solidFace = true;
            break;
        case UP:
            if (y >= 1)
                solidFace = true;
            break;
        case WEST:
            if (x <= 0)
                solidFace = true;
            break;
        case EAST:
            if (x >= 1)
                solidFace = true;
            break;
        case NORTH:
            if (z <= 0)
                solidFace = true;
            break;
        case SOUTH:
            if (z >= 1)
                solidFace = true;
            break;
        default:
            break;
        }

        if (pass == 1 || solidFace)
            location.offset(clickedFace);

        if (canBeMultipart(world, location)) {
            IPartPlacement placement = MultipartCompatibility.getPlacementForPart(part, world, location, clickedFace, mop, player);
            if (placement == null)
                return false;
            if (!simulated && !placement.placePart(part, world, location, this, true))
                return false;
            return placement.placePart(part, world, location, this, simulated);
        }

        return false;
    }

    @Override
    public int getPlacementPasses() {

        return 2;
    }

    @Override
    public boolean isMultipart(World world, BlockPos location) {

        return BlockMultipart.get(world, location) != null;
    }

    @Override
    public boolean canBeMultipart(World world, BlockPos location) {

        return world.getBlockState(location).getMaterial().isReplaceable() || isMultipart(world, location);
    }

    @Override
    public int getStrongRedstoneOuput(World world, BlockPos location, EnumFacing side, EnumFacing face) {

        TileMultipart te = BlockMultipart.get(world, location);
        if (te == null)
            return 0;

        if (face == null)
            return te.getStrongOutput(side);

        return te.getStrongOutput(side, face);
    }

    @Override
    public int getWeakRedstoneOuput(World world, BlockPos location, EnumFacing side, EnumFacing face) {

        TileMultipart te = BlockMultipart.get(world, location);
        if (te == null)
            return 0;

        if (face == null)
            return te.getWeakOutput(side);

        return te.getWeakOutput(side, face);
    }

    @Override
    public boolean canConnectRedstone(World world, BlockPos location, EnumFacing side, EnumFacing face) {

        TileMultipart te = BlockMultipart.get(world, location);
        if (te == null)
            return false;

        if (face == null)
            return te.canConnect(side);

        return te.canConnect(side, face);
    }

    @Override
    public ITilePartHolder getPartHolder(World world, BlockPos location) {

        return BlockMultipart.get(world, location);
    }

    @Override
    public boolean checkOcclusion(World world, BlockPos location, Vec3dCube cube) {

        TileMultipart te = BlockMultipart.get(world, location);
        if (te == null)
            return false;

        return !te.canAddPart(new PartNormallyOccluded(cube));
    }

    @Override
    public void preInit(FMLPreInitializationEvent event) {

    }

    @Override
    public void init(FMLInitializationEvent event) {

    }

    @Override
    public void postInit(FMLPostInitializationEvent event) {

    }

    @Override
    public List<IMicroblock> getMicroblocks(World world, BlockPos location) {

        TileMultipart tmp = (TileMultipart) getPartHolder(world, location);
        if (tmp != null)
            return tmp.getMicroblocks();

        return new ArrayList<IMicroblock>();
    }
}
