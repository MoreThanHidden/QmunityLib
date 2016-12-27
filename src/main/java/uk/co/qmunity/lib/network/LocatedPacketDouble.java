package uk.co.qmunity.lib.network;

import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import uk.co.qmunity.lib.vec.IWorldLocation;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public abstract class LocatedPacketDouble<T extends LocatedPacket<T>> extends Packet<T> {

    protected double x, y, z;

    public LocatedPacketDouble(IWorldLocation location) {

        this.x = location.getX();
        this.y = location.getY();
        this.z = location.getZ();
    }

    public LocatedPacketDouble(double x, double y, double z) {

        this.x = x;
        this.y = y;
        this.z = z;
    }

    public LocatedPacketDouble() {

    }

    @Override
    public void read(DataInput buffer) throws IOException {

        x = buffer.readDouble();
        y = buffer.readDouble();
        z = buffer.readDouble();
    }

    @Override
    public void write(DataOutput buffer) throws IOException {

        buffer.writeDouble(x);
        buffer.writeDouble(y);
        buffer.writeDouble(z);
    }

    public NetworkRegistry.TargetPoint getTargetPoint(World world) {

        return getTargetPoint(world, 64);
    }

    public NetworkRegistry.TargetPoint getTargetPoint(World world, double updateDistance) {

        return new NetworkRegistry.TargetPoint(world.provider.getDimension(), x, y, z, updateDistance);
    }

}
