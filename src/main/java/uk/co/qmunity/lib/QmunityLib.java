package uk.co.qmunity.lib;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.*;
import uk.co.qmunity.lib.command.CommandQLib;
import uk.co.qmunity.lib.compat.QLCompatManager;
import uk.co.qmunity.lib.helper.RedstoneHelper;
import uk.co.qmunity.lib.helper.SystemInfoHelper;
import uk.co.qmunity.lib.network.NetworkHandler;
import uk.co.qmunity.lib.part.MultipartCompat;
import uk.co.qmunity.lib.part.MultipartSystemStandalone;
import uk.co.qmunity.lib.util.QLog;

@Mod(modid = QLModInfo.MODID, name = QLModInfo.NAME, dependencies = "after:ForgeMultipart")
public class QmunityLib {

    @SidedProxy(serverSide = "uk.co.qmunity.lib.CommonProxy", clientSide = "uk.co.qmunity.lib.client.ClientProxy")
    public static CommonProxy proxy;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {

        event.getModMetadata().version = QLModInfo.fullVersionString();
        QLog.logger = event.getModLog();

        QLBlocks.init();

        MultipartSystemStandalone standaloneMultiparts = new MultipartSystemStandalone();
        MultipartCompat.registerMultipartSystem(standaloneMultiparts);
        RedstoneHelper.registerProvider(standaloneMultiparts);

        QLCompatManager.preInit(event);
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {

        NetworkHandler.initQLib();

        proxy.registerRenders();

        QLCompatManager.init(event);
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event) {

        QLCompatManager.postInit(event);
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