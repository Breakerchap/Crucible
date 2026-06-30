package com.remy.crucible.worldgen;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.placement.PlacementContext;
import net.minecraft.world.level.levelgen.placement.PlacementFilter;
import net.minecraft.world.level.levelgen.placement.PlacementModifierType;

public final class NearestSurfaceBiomeGroupFilter extends PlacementFilter {
  public static final int DEFAULT_MAX_SEARCH_RADIUS = 16;
  public static final int DEFAULT_SAMPLE_STEP = 4;
  public static final MapCodec<NearestSurfaceBiomeGroupFilter> CODEC = RecordCodecBuilder.mapCodec(instance -> instance
      .group(
          Identifier.CODEC.fieldOf("group").forGetter(NearestSurfaceBiomeGroupFilter::groupId),
          Codec.INT.optionalFieldOf("max_search_radius", DEFAULT_MAX_SEARCH_RADIUS)
              .forGetter(NearestSurfaceBiomeGroupFilter::maxSearchRadius),
          Codec.INT.optionalFieldOf("sample_step", DEFAULT_SAMPLE_STEP)
              .forGetter(NearestSurfaceBiomeGroupFilter::sampleStep))
      .apply(instance, NearestSurfaceBiomeGroupFilter::new));

  private final Identifier groupId;
  private final int maxSearchRadius;
  private final int sampleStep;

  public NearestSurfaceBiomeGroupFilter(Identifier groupId, int maxSearchRadius, int sampleStep) {
    this.groupId = groupId;
    this.maxSearchRadius = maxSearchRadius;
    this.sampleStep = sampleStep;
  }

  public Identifier groupId() {
    return groupId;
  }

  public int maxSearchRadius() {
    return maxSearchRadius;
  }

  public int sampleStep() {
    return sampleStep;
  }

  @Override
  protected boolean shouldPlace(PlacementContext context, RandomSource random, BlockPos pos) {
    Identifier resolvedGroup = BiomeGroupResolver.findNearestConfiguredSurfaceGroup(
        context.getLevel(),
        pos,
        maxSearchRadius,
        sampleStep);

    return groupId.equals(resolvedGroup);
  }

  @Override
  public PlacementModifierType<?> type() {
    return ModPlacementModifiers.NEAREST_SURFACE_BIOME_GROUP;
  }
}
