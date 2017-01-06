package uk.co.qmunity.lib.util;

import net.minecraft.util.EnumFacing;

public interface IRotatable {

    public boolean rotate(EnumFacing axis);

    public static interface IRotatableFace {

        public void setFaceRotation(EnumFacing face, int rotation);

        public int getFaceRotation(EnumFacing face);

        public boolean canRotateFace(EnumFacing face);

    }

    public static interface IFacing {

        public void setFacingDirection(EnumFacing dir);

        public EnumFacing getFacingDirection();

        public boolean canFaceDirection(EnumFacing dir);

    }

    public static interface IAxial {

        public void setAxis(EnumAxis axis);

        public EnumAxis getAxis();

        public boolean isValidAxis(EnumAxis axis);

    }

    public static enum EnumAxis {
        X(5), Y(1), Z(3);

        private int positiveDir;

        private EnumAxis(int positiveDir) {

            this.positiveDir = positiveDir;
        }

        public static EnumAxis getAxis(EnumFacing direction) {

            if (direction == EnumFacing.WEST || direction == EnumFacing.EAST) {
                return X;
            } else if (direction == EnumFacing.DOWN || direction == EnumFacing.UP) {
                return Y;
            } else if (direction == EnumFacing.NORTH || direction == EnumFacing.SOUTH) {
                return Z;
            }
            return null;
        }
    }

}
