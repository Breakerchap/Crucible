package com.remy.crucible.block;

import com.remy.crucible.Crucible;
import com.remy.crucible.data.ResourceConfigLoader;
import net.fabricmc.fabric.api.creativetab.v1.CreativeModeTabEvents;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class ModBlocks {
  private static final ResourceKey<CreativeModeTab> NATURAL_BLOCKS_TAB = ResourceKey.create(
      Registries.CREATIVE_MODE_TAB,
      Identifier.fromNamespaceAndPath("minecraft", "natural_blocks"));

  public static final Map<Identifier, Block> CUSTOM_ORE_BLOCKS = registerCustomOreBlocks();

  private ModBlocks() {
  }

  private static Map<Identifier, Block> registerCustomOreBlocks() {
    Map<Identifier, Block> blocks = new LinkedHashMap<>();

    for (ResourceConfigLoader.OreDefinition ore : ResourceConfigLoader.getOreDefinitions()) {
      registerIfCustom(blocks, ore.normalBlockId());
      registerIfCustom(blocks, ore.deepslateBlockId());
    }

    return Map.copyOf(blocks);
  }

  private static void registerIfCustom(Map<Identifier, Block> blocks, Identifier blockId) {
    if (!Crucible.MOD_ID.equals(blockId.getNamespace()) || blocks.containsKey(blockId)) {
      return;
    }

    Block block = registerBlock(blockId, createOreProperties(blockId));
    blocks.put(blockId, block);
    registerBlockItem(blockId, block);
  }

  private static BlockBehaviour.Properties createOreProperties(Identifier blockId) {
    Block template = blockId.getPath().startsWith("deepslate_") ? Blocks.DEEPSLATE_IRON_ORE : Blocks.IRON_ORE;
    ResourceKey<Block> blockKey = ResourceKey.create(Registries.BLOCK, blockId);

    return BlockBehaviour.Properties.ofFullCopy(template)
        .requiresCorrectToolForDrops()
        .setId(blockKey);
  }

  private static Block registerBlock(Identifier blockId, BlockBehaviour.Properties properties) {
    return Registry.register(
        BuiltInRegistries.BLOCK,
        blockId,
        new Block(properties));
  }

  private static Item registerBlockItem(Identifier blockId, Block block) {
    ResourceKey<Item> itemKey = ResourceKey.create(Registries.ITEM, blockId);
    Item.Properties properties = new Item.Properties().setId(itemKey);

    return Registry.register(
        BuiltInRegistries.ITEM,
        itemKey,
        new BlockItem(block, properties));
  }

  public static Block getBlock(Identifier blockId) {
    return CUSTOM_ORE_BLOCKS.get(blockId);
  }

  public static List<Block> getCreativeBlocks() {
    return CUSTOM_ORE_BLOCKS.values().stream().toList();
  }

  public static void registerModBlocks() {
    Crucible.LOGGER.info("Registering Mod Blocks for " + Crucible.MOD_ID);

    CreativeModeTabEvents.modifyOutputEvent(NATURAL_BLOCKS_TAB).register(output -> {
      for (Block block : getCreativeBlocks()) {
        output.prepend(block);
      }
    });
  }
}
