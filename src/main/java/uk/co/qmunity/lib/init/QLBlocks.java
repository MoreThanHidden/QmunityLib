package uk.co.qmunity.lib.init;

import net.minecraft.block.Block;
import net.minecraftforge.fml.common.registry.GameRegistry;
import uk.co.qmunity.lib.QLModInfo;
import uk.co.qmunity.lib.block.BlockMultipart;
import uk.co.qmunity.lib.ref.Names;
import uk.co.qmunity.lib.tile.TileMultipart;

@GameRegistry.ObjectHolder(QLModInfo.MODID)
public class QLBlocks {

    public static Block multipart;

    public static void init() {

        instantiate();
        register();
    }

    private static void instantiate() {

        multipart = new BlockMultipart();
    }

    private static void register() {

        GameRegistry.register(multipart);
        GameRegistry.registerTileEntity(TileMultipart.class, Names.Registry.Blocks.MULTIPART);
    }
}
