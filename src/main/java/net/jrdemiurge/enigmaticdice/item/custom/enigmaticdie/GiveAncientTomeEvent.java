package net.jrdemiurge.enigmaticdice.item.custom.enigmaticdie;

import net.jrdemiurge.enigmaticdice.EnigmaticDice;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.Level;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.enchantment.ItemEnchantments;

import java.util.ArrayList;
import java.util.List;

public class GiveAncientTomeEvent extends RandomEvent {

    public GiveAncientTomeEvent(int rarity) {
        this.rarity = rarity;
    }

    @Override
    public boolean  execute(Level pLevel, Player pPlayer, boolean guaranteed) {
        if (!guaranteed) {
            if (!rollChance(pLevel, pPlayer, rarity)) return false;
        }

        ResourceLocation resourceLocation = ResourceLocation.parse("quark:ancient_tome");
        Item item = BuiltInRegistries.ITEM.get(resourceLocation);
        if (item == null) {
            EnigmaticDice.LOGGER.error("Item not found: {}", "quark:ancient_tome");
            return false;
        }

        List<Holder.Reference<Enchantment>> allEnchantments = new ArrayList<>(pLevel.registryAccess().registryOrThrow(net.minecraft.core.registries.Registries.ENCHANTMENT).holders().toList());
        if (allEnchantments.isEmpty()) {
            EnigmaticDice.LOGGER.warn("No enchantments found in registry.");
            return false;
        }

        Holder<Enchantment> chosenEnchantment = allEnchantments.get(pLevel.random.nextInt(allEnchantments.size()));
        ItemStack itemStack = new ItemStack(item);

        ItemEnchantments.Mutable enchantments = new ItemEnchantments.Mutable(ItemEnchantments.EMPTY);
        enchantments.set(chosenEnchantment, 1);
        itemStack.set(DataComponents.STORED_ENCHANTMENTS, enchantments.toImmutable());

        ItemEntity keyEntity = new ItemEntity(
                pLevel,
                pPlayer.getX(),
                pPlayer.getY() + 1,
                pPlayer.getZ(),
                itemStack
        );
        pLevel.addFreshEntity(keyEntity);

        MutableComponent message = Component.translatable("enigmaticdice.gift.blue");
        pPlayer.displayClientMessage(message, false);
        return true;
    }
}
