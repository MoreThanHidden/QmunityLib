package uk.co.qmunity.lib;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.*;
import uk.co.qmunity.lib.command.CommandQLib;
import uk.co.qmunity.lib.helper.SystemInfoHelper;
import uk.co.qmunity.lib.network.NetworkHandler;
import uk.co.qmunity.lib.proxy.CommonProxy;
import uk.co.qmunity.lib.util.QLog;

@Mod(modid = QLModInfo.MODID, name = QLModInfo.NAME)
public class QmunityLib {

    @SidedProxy(serverSide = "uk.co.qmunity.lib.proxy.CommonProxy", clientSide = "uk.co.qmunity.lib.proxy.ClientProxy")
    public static CommonProxy proxy;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {

        event.getModMetadata().version = QLModInfo.fullVersionString();
        QLog.logger = event.getModLog();
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {

        NetworkHandler.initQLib();

        proxy.registerRenders();
    }

    @Mod.EventHandler
    public void serverStarting(FMLServerStartingEvent event) {

        event.registerServerCommand(new CommandQLib());
    }

    @Mod.EventHandler
    public void serverStarted(FMLServerStartedEvent event) {

        SystemInfoHelper.startTime = System.currentTimeMillis();
    }
}
