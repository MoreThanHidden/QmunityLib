package uk.co.qmunity.lib.part;

import net.minecraft.util.EnumFacing;

/**
 * Interface implemented by parts with solid faces.
 *
 * @author amadornes
 */
public interface IPartSolid extends IPart {

    /**
     * Returns whether or not the specified side is solid.
     */
    public boolean isSideSolid(EnumFacing face);

}
