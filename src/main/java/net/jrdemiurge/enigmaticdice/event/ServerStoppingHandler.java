package net.jrdemiurge.enigmaticdice.event;

import net.neoforged.fml.common.EventBusSubscriber;

import net.jrdemiurge.enigmaticdice.EnigmaticDice;
import net.jrdemiurge.enigmaticdice.item.custom.enigmaticdie.AccelerateDayCycleEvent;
import net.neoforged.neoforge.event.server.ServerStoppingEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;

@EventBusSubscriber(modid = EnigmaticDice.MOD_ID)
public class ServerStoppingHandler {

    @SubscribeEvent
    public static void onServerStopping(ServerStoppingEvent event) {
        AccelerateDayCycleEvent.active = false;
        EnigmaticDice.eventManager = null;
    }
}
