package uk.co.qmunity.lib.item;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import uk.co.qmunity.lib.QLModInfo;
import uk.co.qmunity.lib.QmunityLib;

import java.util.List;

public abstract class QLItemBase extends Item {

    public QLItemBase() {

    }

    public QLItemBase(String name) {

        setUnlocalizedName(name);
        setRegistryName(getModId() + ":" + name);
    }

    @Override
    public String getUnlocalizedName(ItemStack stack) {

        return String.format("item.%s:%s", getModId(), getUnwrappedUnlocalizedName(super.getUnlocalizedName(stack)));
    }

    protected abstract String getModId();

    protected String getUnwrappedUnlocalizedName(String name) {

        return name.substring(name.indexOf(".") + 1);
    }

    protected String getUnlocalizedTip(ItemStack stack, EntityPlayer player) {

        return getUnlocalizedName(stack).replace("item.", "tooltip.");
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @SideOnly(Side.CLIENT)
    protected void addTipInformation(ItemStack stack, EntityPlayer player, List l, String tip) {

        l.addAll(Minecraft.getMinecraft().fontRendererObj.listFormattedStringToWidth(tip, 35));
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    public void addInformation(ItemStack stack, EntityPlayer player, List l, boolean unused) {

        String unlocalized = getUnlocalizedTip(stack, player);
        String localized = I18n.format(unlocalized);
        if (!hasTooltip(stack, player, unlocalized, localized))
            return;

        if (!QmunityLib.proxy.isShiftDown()) {
            l.add(I18n.format("tooltip." + QLModInfo.MODID + ":shift", TextFormatting.GRAY.toString(),
                    TextFormatting.YELLOW.toString() + TextFormatting.ITALIC.toString(), TextFormatting.RESET.toString()
                            + TextFormatting.GRAY.toString()));
        } else {
            addTipInformation(stack, player, l, localized);
        }
    }

    protected boolean hasTooltip(ItemStack stack, EntityPlayer player, String unlocalized, String localized) {

        return !localized.equals(unlocalized);
    }
}
