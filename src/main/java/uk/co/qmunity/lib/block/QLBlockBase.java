package uk.co.qmunity.lib.block;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.ModContainer;

public abstract class QLBlockBase extends Block {

    private Object modInstance;

    public QLBlockBase(Material material) {

        super(material);
        setSoundType(SoundType.STONE);
        setHardness(3.0F);
    }

    public QLBlockBase(Material material, String name) {

        this(material);
        setUnlocalizedName(name);
        setRegistryName(getModId() + ":" + name);
    }

    @Override
    public String getUnlocalizedName() {

        return String.format("tile.%s:%s", getModId(), getUnwrappedUnlocalizedName(super.getUnlocalizedName()));
    }

    protected abstract String getModId();

    protected Object getModInstance() {

        if (modInstance != null)
            return modInstance;

        String modid = getModId();
        for (ModContainer mod : Loader.instance().getActiveModList()) {
            if (mod.getModId().equals(modid)) {
                modInstance = mod.getMod();
                break;
            }
        }

        return modInstance;
    }

    protected String getUnwrappedUnlocalizedName(String name) {

        return name.substring(name.indexOf(".") + 1);
    }

}
