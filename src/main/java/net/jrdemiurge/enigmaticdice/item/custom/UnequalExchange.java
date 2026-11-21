package net.jrdemiurge.enigmaticdice.item.custom;

import net.minecraft.resources.ResourceLocation;

import net.jrdemiurge.enigmaticdice.util.ModifierIds;

import net.minecraft.world.item.Item;

import net.minecraft.world.entity.EquipmentSlotGroup;

import net.minecraft.world.item.component.ItemAttributeModifiers;

import net.minecraft.world.item.Item.TooltipContext;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import net.jrdemiurge.enigmaticdice.Config;
import net.jrdemiurge.enigmaticdice.effect.ModEffects;
import net.jrdemiurge.enigmaticdice.item.custom.unequalexchange.UnequalExchangeData;
import net.jrdemiurge.enigmaticdice.item.custom.unequalexchange.UnequalExchangeDataStorage;
import net.jrdemiurge.enigmaticdice.sound.ModSounds;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.UUID;

// TODO переписать эффекты через планировщик
public class UnequalExchange extends SwordItem {

    public UnequalExchange(Tier pTier, int pAttackDamageModifier, float pAttackSpeedModifier, Properties pProperties) {
        super(pTier, pProperties);
    }

    @Override
    public ItemAttributeModifiers getDefaultAttributeModifiers(ItemStack stack) {
        return ItemAttributeModifiers.builder()
                .add(Attributes.ATTACK_DAMAGE, ModifierIds.modifier(Item.BASE_ATTACK_DAMAGE_ID, Config.UnequalExchangeAttackDamage - 1, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND)
                .add(Attributes.ATTACK_SPEED, ModifierIds.modifier(Item.BASE_ATTACK_SPEED_ID, Config.UnequalExchangeAttackSpeed - 4, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND)
                .build();
    }

    private static final ResourceLocation HEALTH_DEBUFF_UUID = ModifierIds.id("HEALTH_DEBUFF_UUID");
    private static final ResourceLocation ATTACK_SPEED_DEBUFF_UUID = ModifierIds.id("ATTACK_SPEED_DEBUFF_UUID");
    private static final ResourceLocation ARMOR_DEBUFF_UUID = ModifierIds.id("ARMOR_DEBUFF_UUID");
    private static final ResourceLocation ARMOR_TOUGHNESS_DEBUFF_UUID = ModifierIds.id("ARMOR_TOUGHNESS_DEBUFF_UUID");
    private static final ResourceLocation SPEED_DEBUFF_UUID = ModifierIds.id("SPEED_DEBUFF_UUID");

    // добавить чёрный список мобов
    @Override
    public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        if (!attacker.level().isClientSide && attacker instanceof Player player) {
            if (player.getAttackStrengthScale(0.5F) > 0.9F) {
                removeModifierIfExists(player, Attributes.MAX_HEALTH, HEALTH_DEBUFF_UUID);
                float targetMaxHealth = target.getMaxHealth();
                float attackerMaxHealth = attacker.getMaxHealth();

                float targetNewHealth = target.getHealth() - targetMaxHealth * (float) Config.UnequalExchangeTargetHealthReduction;
                target.setHealth(Math.max(targetNewHealth, 0.0F));
                if (target.getHealth() <= 0.0F) {
                    target.die(attacker.damageSources().playerAttack(player));
                }

                float attackerNewHealth = attacker.getHealth() - attackerMaxHealth * (float) Config.UnequalExchangePlayerHealthReduction;
                attacker.setHealth(Math.max(attackerNewHealth, 0.0F));
                if (attacker.getHealth() <= 0.0F) {
                    attacker.die(attacker.damageSources().playerAttack(player));
                }

                UnequalExchangeData data = UnequalExchangeDataStorage.get(player);
                data.onHit();
                updateModifiers(player, data);

                player.level().playSound(null, player.getX(), player.getY(), player.getZ(),
                        ModSounds.UNEQUAL_EXCHANGE_HIT.get(), SoundSource.PLAYERS, 0.3F, 1.0F);

            }
        }
        return super.hurtEnemy(stack, target, attacker);
    }

    public static void updateModifiers(Player player, UnequalExchangeData data) {
        double reductionFactor = - Config.UnequalExchangeStatDebuff * data.getHitCount();
        double healthReduction = Math.max(reductionFactor, -0.8);

        removeModifierIfExists(player, Attributes.MAX_HEALTH, HEALTH_DEBUFF_UUID);
        removeModifierIfExists(player, Attributes.ATTACK_SPEED, ATTACK_SPEED_DEBUFF_UUID);
        removeModifierIfExists(player, Attributes.ARMOR, ARMOR_DEBUFF_UUID);
        removeModifierIfExists(player, Attributes.ARMOR_TOUGHNESS, ARMOR_TOUGHNESS_DEBUFF_UUID);
        removeModifierIfExists(player, Attributes.MOVEMENT_SPEED, SPEED_DEBUFF_UUID);

        player.getAttribute(Attributes.MAX_HEALTH).addTransientModifier(ModifierIds.modifier(HEALTH_DEBUFF_UUID, healthReduction, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));

        player.getAttribute(Attributes.ATTACK_SPEED).addTransientModifier(ModifierIds.modifier(ATTACK_SPEED_DEBUFF_UUID, reductionFactor, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));

        player.getAttribute(Attributes.ARMOR).addTransientModifier(ModifierIds.modifier(ARMOR_DEBUFF_UUID, reductionFactor, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));

        player.getAttribute(Attributes.ARMOR_TOUGHNESS).addTransientModifier(ModifierIds.modifier(ARMOR_TOUGHNESS_DEBUFF_UUID, reductionFactor, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));

        player.getAttribute(Attributes.MOVEMENT_SPEED).addTransientModifier(ModifierIds.modifier(SPEED_DEBUFF_UUID, reductionFactor, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));

        if (player.getHealth() > player.getMaxHealth()) {
            player.setHealth(player.getMaxHealth());
        }

        player.addEffect(new MobEffectInstance(ModEffects.UNEQUAL_EXCHANGE_DEBUFFS, data.getTimeLeftTicks(), data.getHitCount() - 1));
    }

    private static void removeModifierIfExists(Player player, net.minecraft.core.Holder<Attribute> attribute, ResourceLocation uuid) {
        if (player.getAttribute(attribute) != null && player.getAttribute(attribute).getModifier(uuid) != null) {
            player.getAttribute(attribute).removeModifier(uuid);
        }
    }

    @Override
    public boolean isDamageable(ItemStack stack) {
        return false;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(ItemStack pStack, TooltipContext pContext, List<Component> pTooltipComponents, TooltipFlag pIsAdvanced) {
        if (Screen.hasShiftDown()) {
            pTooltipComponents.add(Component.translatable("tooltip.enigmaticdice.unequal_exchange_1"));
            pTooltipComponents.add(Component.translatable("tooltip.enigmaticdice.unequal_exchange_2", String.valueOf(Config.UnequalExchangeTargetHealthReduction   * 100) + "%")
                    .withStyle(ChatFormatting.GOLD));
            pTooltipComponents.add(Component.translatable("tooltip.enigmaticdice.unequal_exchange_3", String.valueOf(Config.UnequalExchangePlayerHealthReduction   * 100) + "%")
                    .withStyle(ChatFormatting.GOLD));

            pTooltipComponents.add(Component.literal(" "));

            pTooltipComponents.add(Component.translatable("tooltip.enigmaticdice.unequal_exchange_4", Config.UnequalExchangeDebuffDuration)
                    .withStyle(ChatFormatting.GOLD));
            pTooltipComponents.add(Component.translatable("tooltip.enigmaticdice.unequal_exchange_5", String.valueOf(Config.UnequalExchangeStatDebuff   * 100) + "%")
                    .withStyle(ChatFormatting.GOLD));
            pTooltipComponents.add(Component.translatable("tooltip.enigmaticdice.unequal_exchange_6", String.valueOf(Config.UnequalExchangeStatDebuff   * 100) + "%")
                    .withStyle(ChatFormatting.GOLD));
            pTooltipComponents.add(Component.translatable("tooltip.enigmaticdice.unequal_exchange_7", String.valueOf(Config.UnequalExchangeStatDebuff   * 100) + "%")
                    .withStyle(ChatFormatting.GOLD));
            pTooltipComponents.add(Component.translatable("tooltip.enigmaticdice.unequal_exchange_8", String.valueOf(Config.UnequalExchangeStatDebuff   * 100) + "%")
                    .withStyle(ChatFormatting.GOLD));
            pTooltipComponents.add(Component.translatable("tooltip.enigmaticdice.unequal_exchange_9", String.valueOf(Config.UnequalExchangeStatDebuff   * 100) + "%")
                    .withStyle(ChatFormatting.GOLD));
        } else {
            pTooltipComponents.add(Component.translatable("tooltip.enigmaticdice.unequal_exchange_0"));
            pTooltipComponents.add(Component.translatable("tooltip.enigmaticdice.holdShift"));
        }
        super.appendHoverText(pStack, pContext, pTooltipComponents, pIsAdvanced);
    }
}
