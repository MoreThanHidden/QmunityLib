package uk.co.qmunity.lib.network.packet;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import uk.co.qmunity.lib.network.NetworkHandler;
import uk.co.qmunity.lib.part.IPart;
import uk.co.qmunity.lib.part.ITilePartHolder;

import java.io.*;

public class PacketCUpdatePart extends PacketCPart {

    private byte[] data;
    private int channel;

    public PacketCUpdatePart(ITilePartHolder holder, IPart part, int channel) {

        super(holder, part);
        this.channel = channel;
    }

    public PacketCUpdatePart() {

        super();
    }

    @Override
    public void handle(EntityPlayer player) {

        if (part == null)
            return;

        try {
            part.readUpdateData(new DataInputStream(new ByteArrayInputStream(data)), channel);
        } catch (IOException e) {
            e.printStackTrace();

            holder.getWorld().markBlockRangeForRenderUpdate(pos, pos);
        }
    }

    @Override
    public void writeData(DataOutput buffer) throws IOException {

        buffer.writeInt(channel);

        ByteArrayOutputStream os = new ByteArrayOutputStream();
        part.writeUpdateData(new DataOutputStream(os), channel);
        data = os.toByteArray();

        buffer.writeInt(data.length);
        buffer.write(data);
    }

    @Override
    public void readData(DataInput buffer) throws IOException {

        channel = buffer.readInt();

        data = new byte[buffer.readInt()];
        buffer.readFully(data);
    }

    public void sendTo(EntityPlayer player) {

        NetworkHandler.QLIB.sendTo(this, (EntityPlayerMP) player);
    }

}
