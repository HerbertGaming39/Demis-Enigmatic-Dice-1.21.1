package net.jrdemiurge.enigmaticdice.item.custom.enigmaticdie;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;

import java.util.EnumSet;
import java.util.Optional;


public class CurseBindingEvent extends RandomEvent {

    public CurseBindingEvent(int rarity) {
        this.rarity = rarity;
    }

    @Override
    public boolean execute(Level pLevel, Player pPlayer, boolean guaranteed) {
        if (!guaranteed) {
            if (!rollChance(pLevel, pPlayer, rarity)) return false;
        }

        Optional<Holder.Reference<Enchantment>> bindingCurse =
                pLevel.registryAccess().registryOrThrow(Registries.ENCHANTMENT).getHolder(Enchantments.BINDING_CURSE);
        if (bindingCurse.isEmpty()) return false;

        boolean enchantedAny = false;

        for (EquipmentSlot slot : EnumSet.of(
                EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET)) {

            ItemStack itemStack = pPlayer.getItemBySlot(slot);

            if (!itemStack.isEmpty() && EnchantmentHelper.getItemEnchantmentLevel(bindingCurse.get(), itemStack) <= 0) {
                EnchantmentHelper.updateEnchantments(itemStack, enchantments ->
                        enchantments.set(bindingCurse.get(), 1));
                enchantedAny = true;
            }
        }

        if (enchantedAny) {
            pPlayer.displayClientMessage(Component.translatable("enigmaticdice.event.binding_curse"), false);
            return true;
        }

        return false;
    }
}
