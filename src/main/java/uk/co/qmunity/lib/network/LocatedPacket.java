package uk.co.qmunity.lib.network;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import uk.co.qmunity.lib.vec.IWorldLocation;
import uk.co.qmunity.lib.vec.WorldPos;

public abstract class LocatedPacket<T extends LocatedPacket<T>> extends Packet<T> {

    protected BlockPos pos;

    public LocatedPacket(IWorldLocation location) {

        this.pos = location.getPos();
    }

    public LocatedPacket(BlockPos pos) {

        this.pos = pos;
    }

    public LocatedPacket() {

    }

    @Override
    public void fromBytes(MCByteBuf buf) {
        pos = new BlockPos(buf.readInt(),buf.readInt(),buf.readInt());
    }

    @Override
    public void toBytes(MCByteBuf buf) {
        buf.writeInt(pos.getX());
        buf.writeInt(pos.getY());
        buf.writeInt(pos.getZ());
    }

    public NetworkRegistry.TargetPoint getTargetPoint(World world, double range) {

        return new NetworkRegistry.TargetPoint(world.provider.getDimension(), pos.getX(), pos.getY(), pos.getZ(), range);
    }

    protected WorldPos getWorldPos(World world) {

        return new WorldPos(world, pos);
    }

}
