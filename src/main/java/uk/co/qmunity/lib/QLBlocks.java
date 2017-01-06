package uk.co.qmunity.lib;

import net.minecraft.block.Block;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.GameRegistry;
import uk.co.qmunity.lib.block.BlockMultipart;
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

        GameRegistry.register(multipart, new ResourceLocation(QLModInfo.MODID, QLModInfo.MODID + ".multipart"));
        GameRegistry.registerTileEntity(TileMultipart.class, QLModInfo.MODID + ":" +QLModInfo.MODID + ".multipart");
    }
}
