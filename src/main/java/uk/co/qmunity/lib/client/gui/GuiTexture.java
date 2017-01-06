package uk.co.qmunity.lib.client.gui;

import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.util.ResourceLocation;
import uk.co.qmunity.lib.client.texture.CustomIcon;

import java.awt.*;

public final class GuiTexture {

    public static GuiTexture blockTexture(TextureAtlasSprite itemIcon) {

        return texture(TextureMap.LOCATION_BLOCKS_TEXTURE, itemIcon);
    }

    public static GuiTexture itemTexture(TextureAtlasSprite itemIcon) {

        return texture(TextureMap.LOCATION_BLOCKS_TEXTURE, itemIcon);
    }

    public static GuiTexture texture(String path) {

        return texture(path, CustomIcon.FULL_ICON);
    }

    public static GuiTexture texture(ResourceLocation resource) {

        return texture(resource, CustomIcon.FULL_ICON);
    }

    public static GuiTexture texture(String path, TextureAtlasSprite icon) {

        return texture(new ResourceLocation(path), icon);
    }

    public static GuiTexture texture(ResourceLocation resource, TextureAtlasSprite icon) {

        return new GuiTexture(resource, icon);
    }

    private final ResourceLocation texture;
    private final TextureAtlasSprite icon;
    private final int tint;

    public GuiTexture(ResourceLocation texture, TextureAtlasSprite icon, int tint) {

        this.texture = texture;
        this.icon = icon;
        this.tint = tint;
    }

    private GuiTexture(ResourceLocation texture, TextureAtlasSprite icon) {

        this(texture, icon, 0xFFFFFF);
    }

    public ResourceLocation getTexture() {

        return texture;
    }

    public TextureAtlasSprite getIcon() {

        return icon;
    }

    public int getTint() {

        return tint;
    }

    public GuiTexture withTint(double r, double g, double b) {

        return new GuiTexture(texture, icon, new Color((float) r, (float) g, (float) b).getRGB());
    }

    public GuiTexture withTint(int tint) {

        return new GuiTexture(texture, icon, tint);
    }

}
