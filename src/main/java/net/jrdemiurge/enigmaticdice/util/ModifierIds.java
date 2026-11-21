package net.jrdemiurge.enigmaticdice.util;

import net.jrdemiurge.enigmaticdice.EnigmaticDice;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;

import java.util.Locale;

/**
 * Stable, resource-location based attribute modifier ids. Minecraft 1.21 replaced UUID modifier
 * ids with {@link ResourceLocation}s, so names are normalised into the mod namespace.
 */
public final class ModifierIds {

    private ModifierIds() {
    }

    public static ResourceLocation id(String name) {
        String path = name.toLowerCase(Locale.ROOT).replaceAll("[^a-z0-9/._-]", "_");
        return ResourceLocation.fromNamespaceAndPath(EnigmaticDice.MOD_ID, path);
    }

    public static AttributeModifier modifier(String name, double amount, AttributeModifier.Operation operation) {
        return new AttributeModifier(id(name), amount, operation);
    }

    public static AttributeModifier modifier(ResourceLocation id, double amount, AttributeModifier.Operation operation) {
        return new AttributeModifier(id, amount, operation);
    }
}
