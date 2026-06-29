package com.remy.crucible.worldgen;

import com.remy.crucible.Crucible;
import com.remy.crucible.data.ResourceConfigLoader;
import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

import java.util.List;
import java.util.function.Predicate;

public final class ModOreGeneration {
  private ModOreGeneration() {
  }

  public static void register() {
    for (ResourceConfigLoader.OreDefinition ore : ResourceConfigLoader.getOreDefinitions()) {
      if (!ore.generationEnabled()) {
        continue;
      }

      for (Identifier biomeGroupId : ore.biomeGroups().keySet()) {
        Predicate<net.fabricmc.fabric.api.biome.v1.BiomeSelectionContext> selector = selectorFor(biomeGroupId);
        ResourceKey<PlacedFeature> placedFeatureKey = ResourceKey.create(
            Registries.PLACED_FEATURE,
            Identifier.fromNamespaceAndPath(
                Crucible.MOD_ID,
                ore.orePath() + "__" + biomeGroupId.getNamespace() + "__" + biomeGroupId.getPath()));

        BiomeModifications.addFeature(selector, GenerationStep.Decoration.UNDERGROUND_ORES, placedFeatureKey);
      }
    }
  }

  private static Predicate<net.fabricmc.fabric.api.biome.v1.BiomeSelectionContext> selectorFor(Identifier biomeGroupId) {
    ResourceConfigLoader.BiomeGroupDefinition group = ResourceConfigLoader.getBiomeGroups().get(biomeGroupId);

    if (group != null) {
      List<ResourceKey<Biome>> biomeKeys = group.biomes().stream()
          .map(id -> ResourceKey.create(Registries.BIOME, id))
          .toList();

      return BiomeSelectors.includeByKey(biomeKeys);
    }

    return BiomeSelectors.includeByKey(ResourceKey.create(Registries.BIOME, biomeGroupId));
  }
}
