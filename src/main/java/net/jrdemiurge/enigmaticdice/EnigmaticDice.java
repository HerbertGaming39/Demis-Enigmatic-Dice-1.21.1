package net.jrdemiurge.enigmaticdice;

import com.mojang.logging.LogUtils;
import net.jrdemiurge.enigmaticdice.attribute.ModAttributes;
import net.jrdemiurge.enigmaticdice.commands.EnigmaticDiceCommand;
import net.jrdemiurge.enigmaticdice.commands.EnigmaticDiceGetLuckCommand;
import net.jrdemiurge.enigmaticdice.commands.EnigmaticDiceSimulateCommand;
import net.jrdemiurge.enigmaticdice.effect.ModEffects;
import net.jrdemiurge.enigmaticdice.entity.ModEntities;
import net.jrdemiurge.enigmaticdice.entity.client.DragonclawHookRender;
import net.jrdemiurge.enigmaticdice.item.ModCreativeTabs;
import net.jrdemiurge.enigmaticdice.item.ModItems;
import net.jrdemiurge.enigmaticdice.item.custom.Antimatter;
import net.jrdemiurge.enigmaticdice.item.custom.enigmaticdie.RandomEventManager;
import net.jrdemiurge.enigmaticdice.scheduler.Scheduler;
import net.jrdemiurge.enigmaticdice.sound.ModSounds;
import net.jrdemiurge.enigmaticdice.stat.ModStats;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import org.slf4j.Logger;

@Mod(EnigmaticDice.MOD_ID)
public class EnigmaticDice {
    public static final String MOD_ID = "enigmaticdice";
    public static final Logger LOGGER = LogUtils.getLogger();
    public static RandomEventManager eventManager;

    public EnigmaticDice(IEventBus modEventBus, ModContainer modContainer) {
        ModCreativeTabs.register(modEventBus);
        ModItems.register(modEventBus);
        ModSounds.register(modEventBus);
        ModEffects.register(modEventBus);
        ModEntities.register(modEventBus);
        ModAttributes.register(modEventBus);
        ModStats.register(modEventBus);

        modEventBus.addListener(this::commonSetup);

        NeoForge.EVENT_BUS.register(this);
        NeoForge.EVENT_BUS.register(new Scheduler());

        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        event.enqueueWork(Antimatter::init);
        event.enqueueWork(ModStats::registerCustomStats);
    }

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        event.getServer().getCommands().getDispatcher().register(
                EnigmaticDiceCommand.create()
        );
        event.getServer().getCommands().getDispatcher().register(
                EnigmaticDiceSimulateCommand.create()
        );
        event.getServer().getCommands().getDispatcher().register(
                EnigmaticDiceGetLuckCommand.create()
        );
    }

    @EventBusSubscriber(modid = MOD_ID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {
            EntityRenderers.register(ModEntities.DRAGONCLAW_HOOK.get(), DragonclawHookRender::new);
        }
    }
}
