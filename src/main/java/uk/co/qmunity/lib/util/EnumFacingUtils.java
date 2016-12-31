package uk.co.qmunity.lib.util;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.EnumFacing;

public class EnumFacingUtils {

    /**
     * Returns the EnumFacing of the facing of the entity given.
     *
     * @param entity
     * @param includeUpAndDown
     *            false when UP/DOWN should not be included.
     * @return
     */
    public static EnumFacing getDirectionFacing(EntityLivingBase entity, boolean includeUpAndDown) {

        double yaw = entity.rotationYaw;
        while (yaw < 0)
            yaw += 360;
        yaw = yaw % 360;
        if (includeUpAndDown) {
            if (entity.rotationPitch > 45)
                return EnumFacing.DOWN;
            else if (entity.rotationPitch < -45)
                return EnumFacing.UP;
        }
        if (yaw < 45)
            return EnumFacing.SOUTH;
        else if (yaw < 135)
            return EnumFacing.WEST;
        else if (yaw < 225)
            return EnumFacing.NORTH;
        else if (yaw < 315)
            return EnumFacing.EAST;

        else
            return EnumFacing.SOUTH;
    }

    public static EnumFacing getOnFace(EnumFacing face, EnumFacing dir) {

        switch (face) {
        case DOWN:
            return dir;
        case UP:
            if (dir == EnumFacing.UP || dir == EnumFacing.DOWN)
                return dir.getOpposite();
            return dir;
        case WEST:
            switch (dir) {
            case DOWN:
                return EnumFacing.WEST;
            case UP:
                return EnumFacing.EAST;
            case WEST:
                return EnumFacing.DOWN;
            case EAST:
                return EnumFacing.UP;
            case NORTH:
                return EnumFacing.NORTH;
            case SOUTH:
                return EnumFacing.SOUTH;
            default:
                break;
            }
            break;
        case EAST:
            switch (dir) {
            case DOWN:
                return EnumFacing.EAST;
            case UP:
                return EnumFacing.WEST;
            case WEST:
                return EnumFacing.DOWN;
            case EAST:
                return EnumFacing.UP;
            case NORTH:
                return EnumFacing.NORTH;
            case SOUTH:
                return EnumFacing.SOUTH;
            default:
                break;
            }
            break;
        case NORTH:
            switch (dir) {
            case DOWN:
                return EnumFacing.NORTH;
            case UP:
                return EnumFacing.SOUTH;
            case WEST:
                return EnumFacing.WEST;
            case EAST:
                return EnumFacing.EAST;
            case NORTH:
                return EnumFacing.DOWN;
            case SOUTH:
                return EnumFacing.UP;
            default:
                break;
            }
            break;
        case SOUTH:
            switch (dir) {
            case DOWN:
                return EnumFacing.SOUTH;
            case UP:
                return EnumFacing.NORTH;
            case WEST:
                return EnumFacing.WEST;
            case EAST:
                return EnumFacing.EAST;
            case NORTH:
                return EnumFacing.DOWN;
            case SOUTH:
                return EnumFacing.UP;
            default:
                break;
            }
            break;
        default:
            break;
        }

        return null;
    }

}
