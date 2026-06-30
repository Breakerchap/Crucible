package com.remy.crucible.worldgen;

import com.remy.crucible.Crucible;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.levelgen.placement.PlacementModifier;
import net.minecraft.world.level.levelgen.placement.PlacementModifierType;

public final class ModPlacementModifiers {
  public static final PlacementModifierType<NearestSurfaceBiomeGroupFilter> NEAREST_SURFACE_BIOME_GROUP = register(
      "nearest_surface_biome_group",
      NearestSurfaceBiomeGroupFilter.CODEC);

  private ModPlacementModifiers() {
  }

  private static <P extends PlacementModifier> PlacementModifierType<P> register(String path, MapCodec<P> codec) {
    return Registry.register(
        BuiltInRegistries.PLACEMENT_MODIFIER_TYPE,
        Identifier.fromNamespaceAndPath(Crucible.MOD_ID, path),
        () -> codec);
  }

  public static void register() {
    Crucible.LOGGER.info("Registering Placement Modifiers for " + Crucible.MOD_ID);
  }
}
