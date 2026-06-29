package com.remy.crucible.loot;

import com.remy.crucible.item.ModItems;
import net.fabricmc.fabric.api.loot.v3.LootTableEvents;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;

import java.util.List;

public final class ModLoot {
  private ModLoot() {
  }

  public static void registerLootTables() {
    LootTableEvents.MODIFY_DROPS.register((holder, context, drops) -> {
      if (!context.hasParameter(LootContextParams.BLOCK_STATE)) {
        return;
      }

      BlockState blockState = context.getParameter(LootContextParams.BLOCK_STATE);
      Item replacement = getRawReplacement(blockState.getBlock());

      if (replacement == null) {
        return;
      }

      replaceDrop(drops, Items.RAW_COPPER, replacement);
      replaceDrop(drops, Items.RAW_GOLD, replacement);
      replaceDrop(drops, Items.RAW_IRON, replacement);
    });
  }

  private static Item getRawReplacement(Block block) {
    if (block == Blocks.COPPER_ORE || block == Blocks.DEEPSLATE_COPPER_ORE) {
      return ModItems.RAW_COPPER;
    }

    if (block == Blocks.GOLD_ORE || block == Blocks.DEEPSLATE_GOLD_ORE) {
      return ModItems.RAW_GOLD;
    }

    if (block == Blocks.IRON_ORE || block == Blocks.DEEPSLATE_IRON_ORE) {
      return ModItems.RAW_IRON;
    }

    return null;
  }

  private static void replaceDrop(List<ItemStack> drops, Item original, Item replacement) {
    for (int i = 0; i < drops.size(); i++) {
      ItemStack stack = drops.get(i);

      if (stack.is(original)) {
        drops.set(i, new ItemStack(replacement, stack.getCount()));
      }
    }
  }
}
