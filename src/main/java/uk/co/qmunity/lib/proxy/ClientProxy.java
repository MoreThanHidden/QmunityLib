package uk.co.qmunity.lib.proxy;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.input.Keyboard;
import uk.co.qmunity.lib.client.renderer.RenderParticle;
import uk.co.qmunity.lib.effect.EntityFXParticle;
@SideOnly(Side.CLIENT)
public class ClientProxy extends CommonProxy {

    @Override
    public void registerRenders() {

        RenderingRegistry.registerEntityRenderingHandler(EntityFXParticle.class, new RenderParticle());

    }

    @Override
    public EntityPlayer getPlayer() {

        return Minecraft.getMinecraft().player;
    }

    @Override
    public boolean isSneakingInGui() {

        return Keyboard.isKeyDown(Minecraft.getMinecraft().gameSettings.keyBindSneak.getKeyCode());
    }

    private float frame = 0;

    @Override
    public float getFrame() {

        return frame;
    }

    @Override
    public void setFrame(float frame) {

        this.frame = frame;
    }

}
