package com.remy.crucible.worldgen;

import com.remy.crucible.Crucible;
import com.remy.crucible.data.ResourceConfigLoader;
import net.fabricmc.fabric.api.biome.v1.BiomeModification;
import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectionContext;
import net.fabricmc.fabric.api.biome.v1.ModificationPhase;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public final class ModOreGeneration {
  private static final Map<String, List<ResourceKey<PlacedFeature>>> VANILLA_ORE_FEATURES = Map.of(
      "copper", List.of(
          minecraftPlacedFeature("ore_copper"),
          minecraftPlacedFeature("ore_copper_large")),
      "gold", List.of(
          minecraftPlacedFeature("ore_gold"),
          minecraftPlacedFeature("ore_gold_lower"),
          minecraftPlacedFeature("ore_gold_extra")),
      "iron", List.of(
          minecraftPlacedFeature("ore_iron_upper"),
          minecraftPlacedFeature("ore_iron_middle"),
          minecraftPlacedFeature("ore_iron_small")));

  private ModOreGeneration() {
  }

  public static void register() {
    removeVanillaOreGeneration();

    for (ResourceConfigLoader.OreDefinition ore : ResourceConfigLoader.getOreDefinitions()) {
      if (!ore.generationEnabled()) {
        continue;
      }

      Set<Identifier> directBiomeIds = ore.biomeGroups().keySet().stream()
          .filter(biomeId -> !BiomeGroupResolver.isGroupId(biomeId))
          .collect(java.util.stream.Collectors.toUnmodifiableSet());

      for (Identifier biomeGroupId : ore.biomeGroups().keySet()) {
        Predicate<BiomeSelectionContext> selector = selectorFor(biomeGroupId, directBiomeIds);
        ResourceKey<PlacedFeature> placedFeatureKey = ResourceKey.create(
            Registries.PLACED_FEATURE,
            Identifier.fromNamespaceAndPath(
                Crucible.MOD_ID,
                ore.orePath() + "__" + biomeGroupId.getNamespace() + "__" + biomeGroupId.getPath()));

        BiomeModifications.addFeature(selector, GenerationStep.Decoration.UNDERGROUND_ORES, placedFeatureKey);
      }
    }
  }

  private static void removeVanillaOreGeneration() {
    Set<ResourceKey<PlacedFeature>> featuresToRemove = ResourceConfigLoader.getOreDefinitions().stream()
        .map(ResourceConfigLoader.OreDefinition::materialPath)
        .filter(VANILLA_ORE_FEATURES::containsKey)
        .flatMap(materialPath -> VANILLA_ORE_FEATURES.get(materialPath).stream())
        .collect(Collectors.toUnmodifiableSet());

    if (featuresToRemove.isEmpty()) {
      return;
    }

    BiomeModification vanillaRemoval = BiomeModifications.create(
        Identifier.fromNamespaceAndPath(Crucible.MOD_ID, "remove_vanilla_overworld_ores"));

    vanillaRemoval.add(
        ModificationPhase.REMOVALS,
        BiomeGroupResolver::isOverworld,
        (context, modificationContext) -> {
          for (ResourceKey<PlacedFeature> featureKey : featuresToRemove) {
            modificationContext.getGenerationSettings().removeFeature(featureKey);
          }
        });
  }

  private static Predicate<BiomeSelectionContext> selectorFor(Identifier biomeGroupId, Set<Identifier> directBiomeIds) {
    if (BiomeGroupResolver.isGroupId(biomeGroupId)) {
      return context -> BiomeGroupResolver.isOverworld(context)
          && !directBiomeIds.contains(context.getBiomeKey().identifier());
    }

    return context -> context.getBiomeKey().identifier().equals(biomeGroupId);
  }

  private static ResourceKey<PlacedFeature> minecraftPlacedFeature(String path) {
    return ResourceKey.create(
        Registries.PLACED_FEATURE,
        Identifier.fromNamespaceAndPath("minecraft", path));
  }
}
