package com.remy.crucible.item;

import com.remy.crucible.Crucible;
import net.fabricmc.fabric.api.creativetab.v1.CreativeModeTabEvents;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;

import java.util.List;
import java.util.function.Function;

public class ModItems {
  private static final ResourceKey<CreativeModeTab> INGREDIENTS_TAB = ResourceKey.create(
      Registries.CREATIVE_MODE_TAB,
      Identifier.fromNamespaceAndPath("minecraft", "ingredients"));

  public static final Item ALIMINIUM_INGOT = registerItem(
      "aliminium_ingot",
      Item::new,
      new Item.Properties());
  public static final Item COPPER_INGOT = registerItem(
      "copper_ingot",
      Item::new,
      new Item.Properties());
  public static final Item GOLD_INGOT = registerItem(
      "gold_ingot",
      Item::new,
      new Item.Properties());
  public static final Item IRON_INGOT = registerItem(
      "iron_ingot",
      Item::new,
      new Item.Properties());
  public static final Item LEAD_INGOT = registerItem(
      "lead_ingot",
      Item::new,
      new Item.Properties());
  public static final Item NICKEL_INGOT = registerItem(
      "nickel_ingot",
      Item::new,
      new Item.Properties());
  public static final Item SILVER_INGOT = registerItem(
      "silver_ingot",
      Item::new,
      new Item.Properties());
  public static final Item TIN_INGOT = registerItem(
      "tin_ingot",
      Item::new,
      new Item.Properties());
  public static final Item ZINC_INGOT = registerItem(
      "zinc_ingot",
      Item::new,
      new Item.Properties());

  public static final List<Item> INGOTS = List.of(
      ALIMINIUM_INGOT,
      COPPER_INGOT,
      GOLD_INGOT,
      IRON_INGOT,
      LEAD_INGOT,
      NICKEL_INGOT,
      SILVER_INGOT,
      TIN_INGOT,
      ZINC_INGOT);

  private static <T extends Item> T registerItem(String name, Function<Item.Properties, T> itemFactory,
      Item.Properties properties) {
    ResourceKey<Item> itemKey = ResourceKey.create(
        Registries.ITEM,
        Identifier.fromNamespaceAndPath(Crucible.MOD_ID, name));

    T item = itemFactory.apply(properties.setId(itemKey));

    return Registry.register(
        BuiltInRegistries.ITEM,
        itemKey,
        item);
  }

  public static void registerModItems() {
    Crucible.LOGGER.info("Registering Mod Items for " + Crucible.MOD_ID);

    CreativeModeTabEvents.modifyOutputEvent(INGREDIENTS_TAB).register(output -> {
      for (int i = INGOTS.size() - 1; i >= 0; i--) {
        output.prepend(INGOTS.get(i));
      }
    });
  }
}
