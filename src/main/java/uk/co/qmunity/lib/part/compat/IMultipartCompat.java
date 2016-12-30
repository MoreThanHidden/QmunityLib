package uk.co.qmunity.lib.part.compat;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import uk.co.qmunity.lib.part.IMicroblock;
import uk.co.qmunity.lib.part.IPart;
import uk.co.qmunity.lib.part.ITilePartHolder;
import uk.co.qmunity.lib.vec.Vec3dCube;

import java.util.List;

public interface IMultipartCompat {

    public boolean addPartToWorld(IPart part, World world, BlockPos pos, boolean simulated);

    public boolean addPartToWorldBruteforce(IPart part, World world, BlockPos pos);

    public boolean placePartInWorld(IPart part, World world, BlockPos pos, EnumFacing clickedFace, EntityPlayer player,
                                    ItemStack item, int pass, boolean simulated);

    public int getPlacementPasses();

    public boolean isMultipart(World world, BlockPos pos);

    public boolean canBeMultipart(World world, BlockPos pos);

    public int getStrongRedstoneOuput(World world, BlockPos pos, EnumFacing side, EnumFacing face);

    public int getWeakRedstoneOuput(World world, BlockPos pos, EnumFacing side, EnumFacing face);

    public boolean canConnectRedstone(World world, BlockPos pos, EnumFacing side, EnumFacing face);

    public ITilePartHolder getPartHolder(World world, BlockPos pos);

    public boolean checkOcclusion(World world,BlockPos pos, Vec3dCube cube);

    public void preInit(FMLPreInitializationEvent event);

    public void init(FMLInitializationEvent event);

    public void postInit(FMLPostInitializationEvent event);

    public List<IMicroblock> getMicroblocks(World world, BlockPos pos);

}
