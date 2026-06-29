package com.remy.crucible;

import com.remy.crucible.block.ModBlocks;
import com.remy.crucible.item.ModItems;
import com.remy.crucible.loot.ModLoot;
import com.remy.crucible.worldgen.ModOreGeneration;
import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Crucible implements ModInitializer {
  public static final String MOD_ID = "crucible";
  public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

  @Override
  public void onInitialize() {
    ModBlocks.registerModBlocks();
    ModItems.registerModItems();
    ModLoot.registerLootTables();
    ModOreGeneration.register();
  }
}
