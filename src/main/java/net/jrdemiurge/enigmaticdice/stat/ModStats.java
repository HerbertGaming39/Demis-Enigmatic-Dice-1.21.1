package net.jrdemiurge.enigmaticdice.stat;

import net.jrdemiurge.enigmaticdice.EnigmaticDice;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.stats.StatFormatter;
import net.minecraft.stats.Stats;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModStats {
    public static final ResourceLocation OBTAINED_DICE_FROM_BLOCK =
            ResourceLocation.fromNamespaceAndPath(EnigmaticDice.MOD_ID, "obtained_dice_from_block");
    public static final ResourceLocation OBTAINED_DICE_FROM_MOB =
            ResourceLocation.fromNamespaceAndPath(EnigmaticDice.MOD_ID, "obtained_dice_from_mob");

    public static final DeferredRegister<ResourceLocation> STATS =
            DeferredRegister.create(Registries.CUSTOM_STAT, EnigmaticDice.MOD_ID);

    public static void register(IEventBus modEventBus) {
        STATS.register("obtained_dice_from_block", () -> OBTAINED_DICE_FROM_BLOCK);
        STATS.register("obtained_dice_from_mob", () -> OBTAINED_DICE_FROM_MOB);
        STATS.register(modEventBus);
    }

    public static void registerCustomStats() {
        Stats.CUSTOM.get(OBTAINED_DICE_FROM_BLOCK, StatFormatter.DEFAULT);
        Stats.CUSTOM.get(OBTAINED_DICE_FROM_MOB, StatFormatter.DEFAULT);
    }
}
