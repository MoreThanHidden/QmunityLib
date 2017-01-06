package uk.co.qmunity.lib.vec;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class WorldPos extends BlockPos implements IWorldLocation {

    public World world;

    public WorldPos(World world, int x, int y, int z) {

        super(x, y, z);
        this.world = world;
    }

    public WorldPos(World world, Vector3 v) {

        super(new BlockPos(v.x, v.y, v.z));
        this.world = world;
    }

    public WorldPos(World world, BlockPos v) {

        super(v);
        this.world = world;
    }

    public WorldPos(WorldPos coord) {

        this(coord.world, coord);
    }

    public WorldPos(TileEntity te) {

        this(te.getWorld(), te.getPos());
    }

    @Override
    public World getWorld() {

        return world;
    }

    public WorldPos copy() {

        return new WorldPos(this);
    }

    public Block getBlock() {

        return world.getBlockState(this).getBlock();
    }

    public IBlockState getState() {

        return world.getBlockState(this);
    }

    public TileEntity getTileEntity() {

        return world.getTileEntity(this);
    }

    @Override
    public WorldPos offset(EnumFacing side) {

        super.offset(side, 1);
        return this;
    }

    @Override
    public WorldPos offset(EnumFacing side, int amount) {

        super.offset(side, amount);
        return this;
    }

    @Override
    public BlockPos getPos() {
        return this;
    }
}
