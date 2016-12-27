package uk.co.qmunity.lib.tile;

import net.minecraft.util.EnumFacing;

/**
 * Implemented by BluePower block that can be rotated.
 * @author MineMaarten
 */
public interface IRotatable {
    
    public void setFacingDirection(EnumFacing dir);
    
    public EnumFacing getFacingDirection();
}
