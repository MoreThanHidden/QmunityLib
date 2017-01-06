package uk.co.qmunity.lib.client.helper;

import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.lwjgl.input.Mouse;


public class InputHelper {

    private static int dWheel = 0;

    public static int getDWheel() {

        return dWheel;
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onClientTick(TickEvent.ClientTickEvent event) {

        if (event.phase != TickEvent.Phase.END)
            return;

        dWheel = Mouse.getDWheel();
    }

}
