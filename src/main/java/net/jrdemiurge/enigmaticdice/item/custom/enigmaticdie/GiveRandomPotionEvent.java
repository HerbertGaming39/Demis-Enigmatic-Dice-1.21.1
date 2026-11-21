package net.jrdemiurge.enigmaticdice.item.custom.enigmaticdie;

import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Random;

// можно сделать чтобы число эффектов, уровни и длительность увеличивались с удачей
public class GiveRandomPotionEvent extends RandomEvent {
    private final boolean onlyVanillaEffects;
    private final Random random = new Random();

    public GiveRandomPotionEvent(int rarity, boolean onlyVanillaEffects) {
        this.rarity = rarity;
        this.onlyVanillaEffects = onlyVanillaEffects;
    }

    @Override
    public boolean execute(Level pLevel, Player pPlayer, boolean guaranteed) {
        if (!guaranteed) {
            if (!rollChance(pLevel, pPlayer, rarity)) return false;
        }

        ItemStack potion = new ItemStack(Items.SPLASH_POTION);

        int effectCount = 3 + random.nextInt(4); // 3 to 6 effects

        List<Holder.Reference<MobEffect>> availableEffects = new ArrayList<>();
        for (Holder.Reference<MobEffect> holder : BuiltInRegistries.MOB_EFFECT.holders().toList()) {
            if (onlyVanillaEffects) {
                if ("minecraft".equals(holder.key().location().getNamespace())) {
                    availableEffects.add(holder);
                }
            } else {
                availableEffects.add(holder);
            }
        }

        Collections.shuffle(availableEffects, random);
        List<MobEffectInstance> selectedEffects = new ArrayList<>();

        for (int i = 0; i < Math.min(effectCount, availableEffects.size()); i++) {
            Holder<MobEffect> effect = availableEffects.get(i);
            int amplifier;

            int duration;
            if (effect.value().isInstantenous()) {
                duration = 1;
                amplifier = random.nextInt(5);
            } else {
                int seconds = 10 + random.nextInt((15 * 60) - 10);
                duration = seconds * 20;
                amplifier = random.nextInt(3);
            }

            selectedEffects.add(new MobEffectInstance(effect, duration, amplifier));
        }

        potion.set(DataComponents.POTION_CONTENTS, new PotionContents(Optional.empty(), Optional.empty(), selectedEffects));

        potion.set(DataComponents.ENCHANTMENT_GLINT_OVERRIDE, true);

        MutableComponent name;
        if (onlyVanillaEffects) {
            name = Component.translatable("item.enigmaticdice.vanilla_random_splash_potion")
                    .withStyle(ChatFormatting.RESET);
        } else {
            name = Component.translatable("item.enigmaticdice.modded_random_splash_potion")
                    .withStyle(ChatFormatting.RESET);
        }
        potion.set(DataComponents.CUSTOM_NAME, name);

        ItemEntity entity = new ItemEntity(pLevel, pPlayer.getX(), pPlayer.getY() + 1, pPlayer.getZ(), potion);
        pLevel.addFreshEntity(entity);

        MutableComponent message;
        if (onlyVanillaEffects) {
            message = Component.translatable("enigmaticdice.event.vanilla_random_potion");
        } else {
            message = Component.translatable("enigmaticdice.event.modded_random_potion");
        }
        pPlayer.displayClientMessage(message, false);
        return true;
    }
}
