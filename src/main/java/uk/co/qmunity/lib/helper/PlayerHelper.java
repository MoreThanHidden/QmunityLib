package uk.co.qmunity.lib.helper;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.FMLCommonHandler;
import uk.co.qmunity.lib.QmunityLib;

public class PlayerHelper {

    public static EntityPlayer getPlayer() {

        return QmunityLib.proxy.getPlayer();
    }

    public static boolean isOpped(String player) {
        for ( String oppedPlayer : FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList().getOppedPlayerNames()) {
            if (oppedPlayer.equalsIgnoreCase(player)) {
                return true;
            }
        }
        return false;
    }
}
