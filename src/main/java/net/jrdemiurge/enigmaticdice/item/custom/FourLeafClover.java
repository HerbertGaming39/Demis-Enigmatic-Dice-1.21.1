package net.jrdemiurge.enigmaticdice.item.custom;

import net.jrdemiurge.enigmaticdice.util.ModifierIds;

import net.minecraft.resources.ResourceLocation;

import net.minecraft.world.item.Item.TooltipContext;

import net.jrdemiurge.enigmaticdice.scheduler.Scheduler;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.UUID;

public class FourLeafClover extends Item {

    private static final ResourceLocation LUCK_MODIFIER_UUID = ModifierIds.id("LUCK_MODIFIER_UUID");

    public FourLeafClover(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public void inventoryTick(ItemStack pStack, Level pLevel, Entity pEntity, int pSlotId, boolean pIsSelected) {
        if (!pLevel.isClientSide && pEntity instanceof Player player) {

            boolean isFirstClover = false;
            for (ItemStack itemStack : player.getInventory().items) {
                if (itemStack.getItem() instanceof FourLeafClover) {
                    if (itemStack == pStack) {
                        isFirstClover = true;
                    }
                    break;
                }
            }

            if (!isFirstClover) return;

            int cloverCount = 0;
            for (ItemStack itemStack : player.getInventory().items) {
                if (itemStack.getItem() instanceof FourLeafClover) {
                    cloverCount += itemStack.getCount();
                }
            }

            AttributeInstance luckAttribute = player.getAttribute(Attributes.LUCK);
            if (luckAttribute != null) {
                AttributeModifier existingModifier = luckAttribute.getModifier(LUCK_MODIFIER_UUID);

                if (existingModifier == null) {
                    luckAttribute.addTransientModifier(ModifierIds.modifier(LUCK_MODIFIER_UUID, cloverCount, AttributeModifier.Operation.ADD_VALUE));
                } else if (existingModifier.amount() != (double) cloverCount) {
                    luckAttribute.removeModifier(LUCK_MODIFIER_UUID);
                    luckAttribute.addTransientModifier(ModifierIds.modifier(LUCK_MODIFIER_UUID, cloverCount, AttributeModifier.Operation.ADD_VALUE));
                }
            }

            Scheduler.schedule(() -> {
                boolean hasClover = false;
                for (ItemStack item : player.getInventory().items) {
                    if (item.getItem() instanceof FourLeafClover) {
                        hasClover = true;
                        break;
                    }
                }

                AttributeInstance luckAttr = player.getAttribute(Attributes.LUCK);
                if (!hasClover && luckAttr != null) {
                    AttributeModifier mod = luckAttr.getModifier(LUCK_MODIFIER_UUID);
                    if (mod != null) {
                        luckAttr.removeModifier(LUCK_MODIFIER_UUID);
                    }
                }
            }, 10);
        }
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(ItemStack pStack, TooltipContext pContext, List<Component> pTooltipComponents, TooltipFlag pIsAdvanced) {
        if (Screen.hasShiftDown()) {
            pTooltipComponents.add(Component.translatable("tooltip.enigmaticdice.four_leaf_clever"));
        } else {
            pTooltipComponents.add(Component.translatable("tooltip.enigmaticdice.holdShift"));
        }
        super.appendHoverText(pStack, pContext, pTooltipComponents, pIsAdvanced);
    }
}
