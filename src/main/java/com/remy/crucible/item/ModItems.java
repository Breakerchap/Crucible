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
  public static final Item RAW_ALUMINIUM = registerItem(
      "raw_aluminium",
      Item::new,
      new Item.Properties());
  public static final Item COPPER_INGOT = registerItem(
      "copper_ingot",
      Item::new,
      new Item.Properties());
  public static final Item RAW_COPPER = registerItem(
      "raw_copper",
      Item::new,
      new Item.Properties());
  public static final Item GOLD_INGOT = registerItem(
      "gold_ingot",
      Item::new,
      new Item.Properties());
  public static final Item RAW_GOLD = registerItem(
      "raw_gold",
      Item::new,
      new Item.Properties());
  public static final Item IRON_INGOT = registerItem(
      "iron_ingot",
      Item::new,
      new Item.Properties());
  public static final Item RAW_IRON = registerItem(
      "raw_iron",
      Item::new,
      new Item.Properties());
  public static final Item LEAD_INGOT = registerItem(
      "lead_ingot",
      Item::new,
      new Item.Properties());
  public static final Item RAW_LEAD = registerItem(
      "raw_lead",
      Item::new,
      new Item.Properties());
  public static final Item NICKEL_INGOT = registerItem(
      "nickel_ingot",
      Item::new,
      new Item.Properties());
  public static final Item RAW_NICKEL = registerItem(
      "raw_nickel",
      Item::new,
      new Item.Properties());
  public static final Item SILVER_INGOT = registerItem(
      "silver_ingot",
      Item::new,
      new Item.Properties());
  public static final Item RAW_SILVER = registerItem(
      "raw_silver",
      Item::new,
      new Item.Properties());
  public static final Item TIN_INGOT = registerItem(
      "tin_ingot",
      Item::new,
      new Item.Properties());
  public static final Item RAW_TIN = registerItem(
      "raw_tin",
      Item::new,
      new Item.Properties());
  public static final Item ZINC_INGOT = registerItem(
      "zinc_ingot",
      Item::new,
      new Item.Properties());
  public static final Item RAW_ZINC = registerItem(
      "raw_zinc",
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
  public static final List<Item> RAW_MATERIALS = List.of(
      RAW_ALUMINIUM,
      RAW_COPPER,
      RAW_GOLD,
      RAW_IRON,
      RAW_LEAD,
      RAW_NICKEL,
      RAW_SILVER,
      RAW_TIN,
      RAW_ZINC);

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
      for (int i = RAW_MATERIALS.size() - 1; i >= 0; i--) {
        output.prepend(RAW_MATERIALS.get(i));
      }

      for (int i = INGOTS.size() - 1; i >= 0; i--) {
        output.prepend(INGOTS.get(i));
      }
    });
  }

  public static Item getIngotForMaterial(String materialPath) {
    return switch (materialPath) {
      case "aluminium" -> ALIMINIUM_INGOT;
      case "copper" -> COPPER_INGOT;
      case "gold" -> GOLD_INGOT;
      case "iron" -> IRON_INGOT;
      case "lead" -> LEAD_INGOT;
      case "nickel" -> NICKEL_INGOT;
      case "silver" -> SILVER_INGOT;
      case "tin" -> TIN_INGOT;
      case "zinc" -> ZINC_INGOT;
      default -> throw new IllegalArgumentException("Unknown ingot material: " + materialPath);
    };
  }

  public static Item getRawForMaterial(String materialPath) {
    return switch (materialPath) {
      case "aluminium" -> RAW_ALUMINIUM;
      case "copper" -> RAW_COPPER;
      case "gold" -> RAW_GOLD;
      case "iron" -> RAW_IRON;
      case "lead" -> RAW_LEAD;
      case "nickel" -> RAW_NICKEL;
      case "silver" -> RAW_SILVER;
      case "tin" -> RAW_TIN;
      case "zinc" -> RAW_ZINC;
      default -> throw new IllegalArgumentException("Unknown raw material: " + materialPath);
    };
  }
}
