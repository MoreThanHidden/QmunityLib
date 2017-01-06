package uk.co.qmunity.lib.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public abstract class Packet<REQ extends Packet<REQ>> implements IMessage, IMessageHandler<REQ, REQ> {

    @Override
    public REQ onMessage(REQ message, MessageContext ctx) {

        if (ctx.side == Side.SERVER) {
            if (message.getClass() == getClass())
                message.handleServerSide(ctx.getServerHandler().playerEntity);
            else
                message.handleServerSide(ctx.getServerHandler().playerEntity);
        } else {
            if (message.getClass() == getClass())
                message.handleClientSide(getPlayerClient());
            else
                message.handleClientSide(getPlayerClient());
        }
        return null;
    }

    @SideOnly(Side.CLIENT)
    public EntityPlayer getPlayerClient() {

        return Minecraft.getMinecraft().player;
    }

    @SideOnly(Side.CLIENT)
    public abstract void handleClientSide(EntityPlayer player);

    public abstract void handleServerSide(EntityPlayer player);

    @Override
    public final void fromBytes(ByteBuf buf) {

        fromBytes(new MCByteBuf(buf));
    }

    @Override
    public final void toBytes(ByteBuf buf) {

        toBytes(new MCByteBuf(buf));
    }

    public abstract void fromBytes(MCByteBuf buf);

    public abstract void toBytes(MCByteBuf buf);
}
