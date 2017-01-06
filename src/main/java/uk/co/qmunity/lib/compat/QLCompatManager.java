package uk.co.qmunity.lib.compat;

import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import uk.co.qmunity.lib.util.IModule;
import uk.co.qmunity.lib.util.ModularRegistry;

public class QLCompatManager extends ModularRegistry<IModule> {

    public static final QLCompatManager instance = new QLCompatManager();

    public static void preInit(FMLPreInitializationEvent event) {

        for (IModule m : instance)
            m.preInit(event);
    }

    public static void init(FMLInitializationEvent event) {

        for (IModule m : instance)
            m.init(event);
    }

    public static void postInit(FMLPostInitializationEvent event) {

        for (IModule m : instance)
            m.postInit(event);
    }

    static {
        //instance.register(Dependency.MOD.on(QLDependencies.NEI), CompatModuleNEI.class);
        //instance.register(Dependency.MOD.on(QLDependencies.FMP), CompatModuleFMP.class);
    }

}
