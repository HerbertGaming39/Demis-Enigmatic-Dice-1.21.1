package net.jrdemiurge.enigmaticdice.item;

import net.jrdemiurge.enigmaticdice.EnigmaticDice;
import net.jrdemiurge.enigmaticdice.item.custom.*;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Tiers;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModItems {
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(BuiltInRegistries.ITEM, EnigmaticDice.MOD_ID);

    public static final DeferredHolder<Item, EnigmaticDie> ENIGAMTIC_DIE = ITEMS.register("enigmatic_die",
            () -> new EnigmaticDie(new Item.Properties()));

    public static final DeferredHolder<Item, UnequalExchange> UNEQUAL_EXCHANGE = ITEMS.register("unequal_exchange",
            () -> new UnequalExchange(Tiers.NETHERITE, -4, -2.4F, new Item.Properties().fireResistant()));

    public static final DeferredHolder<Item, SoulEater> SOUL_EATER = ITEMS.register("soul_eater",
            () -> new SoulEater(Tiers.NETHERITE, 5, -2F, new Item.Properties().fireResistant()));

    public static final DeferredHolder<Item, Antimatter> ANTIMATTER = ITEMS.register("antimatter",
            () -> new Antimatter(new Item.Properties()));

    public static final DeferredHolder<Item, FourLeafClover> FOUR_LEAF_CLEVER = ITEMS.register("four_leaf_clever",
            () -> new FourLeafClover(new Item.Properties()));

    public static final DeferredHolder<Item, GiantsRing> GIANTS_RING = ITEMS.register("giants_ring",
            () -> new GiantsRing(new Item.Properties().stacksTo(1)));

    public static final DeferredHolder<Item, MoonShard> MOON_SHARD = ITEMS.register("moon_shard",
            () -> new MoonShard(new Item.Properties().stacksTo(1)));

    public static final DeferredHolder<Item, GravityCore> GRAVITY_CORE = ITEMS.register("gravity_core",
            () -> new GravityCore(new Item.Properties().stacksTo(1)));

    public static final DeferredHolder<Item, Moon> MOON = ITEMS.register("moon",
            () -> new Moon(new Item.Properties().stacksTo(1)));

    public static final DeferredHolder<Item, RingOfAgility> RING_OF_AGILITY = ITEMS.register("ring_of_agility",
            () -> new RingOfAgility(new Item.Properties().stacksTo(1)));

    public static final DeferredHolder<Item, DivineShield> DIVINE_SHIELD = ITEMS.register("divine_shield",
            () -> new DivineShield(new Item.Properties().stacksTo(1)));

    public static final DeferredHolder<Item, Permafrost> PERMAFROST = ITEMS.register("permafrost",
            () -> new Permafrost(Tiers.NETHERITE, 5, -2F, new Item.Properties().fireResistant()));

    public static final DeferredHolder<Item, UnfinishedWeapon> KYOMU = ITEMS.register("kyomu",
            () -> new UnfinishedWeapon(Tiers.NETHERITE, 5, -2F, new Item.Properties().fireResistant()));

    public static final DeferredHolder<Item, UnfinishedWeapon> PHOENIX = ITEMS.register("phoenix",
            () -> new UnfinishedWeapon(Tiers.NETHERITE, 5, -2F, new Item.Properties().fireResistant()));

    public static final DeferredHolder<Item, CrucibleOfRile> CRUCIBLE_OF_RILE = ITEMS.register("crucible_of_rile",
            () -> new CrucibleOfRile(Tiers.NETHERITE, 11, -2.6F, new Item.Properties().fireResistant()));

    public static final DeferredHolder<Item, UnfinishedWeapon> FIRE_CRYSTAL_GREATSWORD = ITEMS.register("fire_crystal_greatsword",
            () -> new UnfinishedWeapon(Tiers.NETHERITE, 11, -2.6F, new Item.Properties().fireResistant()));

    public static final DeferredHolder<Item, UnfinishedWeapon> FIRE_CRYSTAL_SPEAR = ITEMS.register("fire_crystal_spear",
            () -> new UnfinishedWeapon(Tiers.NETHERITE, 5, -2F, new Item.Properties().fireResistant()));

    public static final DeferredHolder<Item, MoaiCharm> MOAI_CHARM = ITEMS.register("moai_charm",
            () -> new MoaiCharm(new Item.Properties().stacksTo(1)));

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}
