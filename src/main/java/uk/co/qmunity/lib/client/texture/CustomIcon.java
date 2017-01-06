package uk.co.qmunity.lib.client.texture;


import net.minecraft.client.renderer.texture.TextureAtlasSprite;

public class CustomIcon extends TextureAtlasSprite {

    public static final TextureAtlasSprite FULL_ICON = new CustomIcon(0, 0, 1, 1, 16, 16);

    private final float minU, minV, maxU, maxV;
    private final int width, height;

    public CustomIcon(float minU, float minV, float maxU, float maxV, int width, int height) {
        super("custom");

        this.minU = minU;
        this.minV = minV;
        this.maxU = maxU;
        this.maxV = maxV;
        this.width = width;
        this.height = height;
    }

    @Override
    public int getIconWidth() {

        return width;
    }

    @Override
    public int getIconHeight() {

        return height;
    }

    @Override
    public float getMinU() {

        return minU;
    }

    @Override
    public float getMaxU() {

        return maxU;
    }

    @Override
    public float getInterpolatedU(double d) {

        return minU + (maxU - minU) * ((float) d / 16F);
    }

    @Override
    public float getMinV() {

        return minV;
    }

    @Override
    public float getMaxV() {

        return maxV;
    }

    @Override
    public float getInterpolatedV(double d) {

        return minV + (maxV - minV) * ((float) d / 16F);
    }

    @Override
    public String getIconName() {

        return "custom";
    }

}
