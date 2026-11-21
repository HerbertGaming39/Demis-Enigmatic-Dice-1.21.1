package net.jrdemiurge.enigmaticdice.event;

import net.neoforged.fml.common.EventBusSubscriber;

import net.jrdemiurge.enigmaticdice.EnigmaticDice;
import net.jrdemiurge.enigmaticdice.item.custom.crucibleofrile.ClientLookController;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;

@EventBusSubscriber(modid = EnigmaticDice.MOD_ID, value = Dist.CLIENT, bus = EventBusSubscriber.Bus.GAME)
public class ComputeCameraAnglesHandler {
    @SubscribeEvent
    public static void onCameraAngles(net.neoforged.neoforge.client.event.ViewportEvent.ComputeCameraAngles event) {
        ClientLookController.onRenderFrame((float) event.getPartialTick());
    }
}
