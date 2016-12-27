package uk.co.qmunity.lib.network;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import uk.co.qmunity.lib.vec.IWorldLocation;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public abstract class LocatedPacket<T extends LocatedPacket<T>> extends Packet<T>{

    protected int x, y, z;

    public LocatedPacket(IWorldLocation location){

        this.x = location.getX();
        this.y = location.getY();
        this.z = location.getZ();
    }

    public LocatedPacket(int x, int y, int z){

        this.x = x;
        this.y = y;
        this.z = z;
    }

    public LocatedPacket(BlockPos pos) {

        this.x = pos.getX();
        this.y = pos.getY();
        this.z = pos.getZ();
    }

    public LocatedPacket(){

    }

    @Override
    public void read(DataInput buffer) throws IOException{

        x = buffer.readInt();
        y = buffer.readInt();
        z = buffer.readInt();
    }

    @Override
    public void write(DataOutput buffer) throws IOException{

        buffer.writeInt(x);
        buffer.writeInt(y);
        buffer.writeInt(z);
    }

    public NetworkRegistry.TargetPoint getTargetPoint(World world, double range){

        return new NetworkRegistry.TargetPoint(world.provider.getDimension(), x, y, z, range);
    }

    protected TileEntity getTileEntity(World world){
        return world.getTileEntity(new BlockPos(x, y, z));
    }

}
