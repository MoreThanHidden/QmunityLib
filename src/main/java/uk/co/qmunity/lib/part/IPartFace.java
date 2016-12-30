package uk.co.qmunity.lib.part;

import net.minecraft.util.EnumFacing;

/**
 * Interface implemented by parts that are placed on the face of a block, such as some microblocks.
 *
 * @author amadornes
 */
public interface IPartFace extends IPart {

    /**
     * Sets the face this part is placed on. Mainly used when placing it.
     */
    public void setFace(EnumFacing face);

    /**
     * Gets the face this part is placed on.
     */
    public EnumFacing getFace();

}
