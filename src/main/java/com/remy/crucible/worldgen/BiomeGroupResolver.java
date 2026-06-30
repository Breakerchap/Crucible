package com.remy.crucible.worldgen;

import com.remy.crucible.data.ResourceConfigLoader;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectionContext;
import net.fabricmc.fabric.api.tag.convention.v2.ConventionalBiomeTags;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.QuartPos;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.BiomeTags;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.Heightmap;

import java.util.LinkedHashMap;
import java.util.Map;

public final class BiomeGroupResolver {
  private static final Map<Identifier, Identifier> EXACT_BIOME_GROUPS = buildExactBiomeGroups();

  private BiomeGroupResolver() {
  }

  public static boolean isGroupId(Identifier biomeId) {
    return ResourceConfigLoader.getBiomeGroups().containsKey(biomeId);
  }

  public static boolean isOverworld(BiomeSelectionContext context) {
    return isOverworld(context.getBiomeHolder());
  }

  public static Identifier findNearestConfiguredSurfaceGroup(LevelReader level, BlockPos origin, int maxSearchRadius,
      int sampleStep) {
    int step = Math.max(1, sampleStep);
    Identifier centerGroup = surfaceGroupAt(level, origin.getX(), origin.getZ());

    if (centerGroup != null) {
      return centerGroup;
    }

    for (int radius = step; radius <= maxSearchRadius; radius += step) {
      Identifier closestOnRing = findClosestOnRing(level, origin, radius, step);

      if (closestOnRing != null) {
        return closestOnRing;
      }
    }

    return null;
  }

  private static Identifier findClosestOnRing(LevelReader level, BlockPos origin, int radius, int step) {
    Identifier closestGroup = null;
    int closestDistanceSquared = Integer.MAX_VALUE;
    int centerX = origin.getX();
    int centerZ = origin.getZ();

    for (int dx = -radius; dx <= radius; dx += step) {
      closestGroup = closerGroup(level, centerX, centerZ, centerX + dx, centerZ - radius,
          closestGroup, closestDistanceSquared);
      if (closestGroup != null) {
        closestDistanceSquared = distanceSquared(centerX, centerZ, centerX + dx, centerZ - radius);
      }

      closestGroup = closerGroup(level, centerX, centerZ, centerX + dx, centerZ + radius,
          closestGroup, closestDistanceSquared);
      if (closestGroup != null) {
        closestDistanceSquared = Math.min(closestDistanceSquared,
            distanceSquared(centerX, centerZ, centerX + dx, centerZ + radius));
      }
    }

    for (int dz = -radius + step; dz <= radius - step; dz += step) {
      closestGroup = closerGroup(level, centerX, centerZ, centerX - radius, centerZ + dz,
          closestGroup, closestDistanceSquared);
      if (closestGroup != null) {
        closestDistanceSquared = Math.min(closestDistanceSquared,
            distanceSquared(centerX, centerZ, centerX - radius, centerZ + dz));
      }

      closestGroup = closerGroup(level, centerX, centerZ, centerX + radius, centerZ + dz,
          closestGroup, closestDistanceSquared);
      if (closestGroup != null) {
        closestDistanceSquared = Math.min(closestDistanceSquared,
            distanceSquared(centerX, centerZ, centerX + radius, centerZ + dz));
      }
    }

    return closestGroup;
  }

  private static Identifier closerGroup(LevelReader level, int centerX, int centerZ, int sampleX, int sampleZ,
      Identifier currentGroup, int currentDistanceSquared) {
    Identifier sampledGroup = surfaceGroupAt(level, sampleX, sampleZ);

    if (sampledGroup == null) {
      return currentGroup;
    }

    int sampledDistanceSquared = distanceSquared(centerX, centerZ, sampleX, sampleZ);

    if (currentGroup == null || sampledDistanceSquared < currentDistanceSquared) {
      return sampledGroup;
    }

    return currentGroup;
  }

  private static int distanceSquared(int x1, int z1, int x2, int z2) {
    int dx = x2 - x1;
    int dz = z2 - z1;
    return dx * dx + dz * dz;
  }

  private static Identifier surfaceGroupAt(LevelReader level, int x, int z) {
    BlockPos samplePos = new BlockPos(x, level.getMinY(), z);

    if (!level.hasChunkAt(samplePos)) {
      return null;
    }

    int surfaceY = Math.max(level.getMinY(), level.getHeight(Heightmap.Types.WORLD_SURFACE_WG, x, z) - 1);
    Holder<Biome> biomeHolder = level.getUncachedNoiseBiome(
        QuartPos.fromBlock(x),
        QuartPos.fromBlock(surfaceY),
        QuartPos.fromBlock(z));

    if (!isOverworld(biomeHolder) || isCave(biomeHolder)) {
      return null;
    }

    return biomeHolder.unwrapKey()
        .map(key -> EXACT_BIOME_GROUPS.get(key.identifier()))
        .orElse(null);
  }

  private static boolean isOverworld(Holder<Biome> biomeHolder) {
    return biomeHolder.is(BiomeTags.IS_OVERWORLD) || biomeHolder.is(ConventionalBiomeTags.IS_OVERWORLD);
  }

  private static boolean isCave(Holder<Biome> biomeHolder) {
    return biomeHolder.is(ConventionalBiomeTags.IS_CAVE) || biomeHolder.is(ConventionalBiomeTags.IS_UNDERGROUND);
  }

  private static Map<Identifier, Identifier> buildExactBiomeGroups() {
    Map<Identifier, Identifier> exactBiomeGroups = new LinkedHashMap<>();

    for (ResourceConfigLoader.BiomeGroupDefinition group : ResourceConfigLoader.getBiomeGroups().values()) {
      for (Identifier biomeId : group.biomes()) {
        exactBiomeGroups.put(biomeId, group.id());
      }
    }

    return Map.copyOf(exactBiomeGroups);
  }
}
