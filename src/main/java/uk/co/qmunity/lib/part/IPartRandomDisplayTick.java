package uk.co.qmunity.lib.part;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Random;


/**
 * Interface implemented by parts that do something on a random display tick.
 *
 * @author amadornes
 */
public interface IPartRandomDisplayTick extends IPart {

    @SideOnly(Side.CLIENT)
    public void randomDisplayTick(Random rnd);

}
