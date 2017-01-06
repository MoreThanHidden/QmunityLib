package uk.co.qmunity.lib.part;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;

public interface IWailaProviderPart extends IQLPart {

    @SideOnly(Side.CLIENT)
    public void addWAILABody(List<String> text);
}
