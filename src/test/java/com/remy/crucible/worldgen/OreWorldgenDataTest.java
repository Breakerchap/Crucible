package com.remy.crucible.worldgen;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class OreWorldgenDataTest {
  private static final double ORE_SPAWN_RATE_MULTIPLIER = 3.0d;
  private static final Path ORE_DIR = Path.of("src/main/resources/data/crucible/ores");
  private static final Path BIOME_GROUP_DIR = Path.of("src/main/resources/data/crucible/biome_groups");
  private static final Path GENERATED_PLACED_FEATURE_DIR = Path.of(
      "build/generated/resources/oreWorldgen/data/crucible/worldgen/placed_feature");

  @Test
  void generatedPlacedFeaturesOnlyUseConfiguredBiomeTargets() throws IOException {
    Map<String, OreConfig> ores = loadOreConfigs();
    Set<String> expectedFiles = new TreeSet<>();

    for (OreConfig ore : ores.values()) {
      for (String biomeTarget : ore.biomeGroups().keySet()) {
        expectedFiles.add(placedFeatureFileName(ore.oreName(), biomeTarget));
      }
    }

    Set<String> actualFiles;
    try (Stream<Path> files = Files.list(GENERATED_PLACED_FEATURE_DIR)) {
      actualFiles = files
          .map(path -> path.getFileName().toString())
          .collect(Collectors.toCollection(TreeSet::new));
    }

    assertEquals(expectedFiles, actualFiles);
  }

  @Test
  void generatedPlacedFeaturesRespectConfiguredBiomeSelectors() throws IOException {
    Map<String, OreConfig> ores = loadOreConfigs();
    Set<String> configuredGroups = listConfiguredBiomeGroups();

    for (OreConfig ore : ores.values()) {
      for (String biomeTarget : ore.biomeGroups().keySet()) {
        JsonObject placedFeature = readJsonObject(
            GENERATED_PLACED_FEATURE_DIR.resolve(placedFeatureFileName(ore.oreName(), biomeTarget)));
        List<JsonObject> placements = placementEntries(placedFeature);

        JsonObject groupFilter = placements.stream()
            .filter(entry -> "crucible:nearest_surface_biome_group".equals(entry.get("type").getAsString()))
            .findFirst()
            .orElse(null);

        if (configuredGroups.contains(biomeTarget)) {
          assertNotNull(groupFilter, ore.oreName() + " should keep the configured biome-group filter for " + biomeTarget);
          assertEquals(biomeTarget, groupFilter.get("group").getAsString());
        } else {
          assertTrue(groupFilter == null,
              ore.oreName() + " should not add a biome-group filter for direct biome target " + biomeTarget);
        }
      }
    }
  }

  @Test
  void generatedCountsMatchConfiguredSpawnRatios() throws IOException {
    for (OreConfig ore : loadOreConfigs().values()) {
      for (Map.Entry<String, Double> biomeEntry : ore.biomeGroups().entrySet()) {
        JsonObject placedFeature = readJsonObject(
            GENERATED_PLACED_FEATURE_DIR.resolve(placedFeatureFileName(ore.oreName(), biomeEntry.getKey())));
        int actualCount = findPlacementByType(placedFeature, "minecraft:count")
            .get("count")
            .getAsInt();

        int expectedCount = expectedCountFor(ore, biomeEntry.getValue());
        assertEquals(expectedCount, actualCount,
            ore.oreName() + " should match the configured spawn ratio for " + biomeEntry.getKey());
      }
    }
  }

  @Test
  void largerBiomeMultipliersNeverGenerateSmallerCountsForTheSameOre() throws IOException {
    for (OreConfig ore : loadOreConfigs().values()) {
      List<Map.Entry<String, Double>> targets = ore.biomeGroups().entrySet().stream()
          .sorted(Map.Entry.comparingByValue())
          .toList();

      int previousCount = -1;
      double previousMultiplier = Double.NEGATIVE_INFINITY;
      for (Map.Entry<String, Double> target : targets) {
        JsonObject placedFeature = readJsonObject(
            GENERATED_PLACED_FEATURE_DIR.resolve(placedFeatureFileName(ore.oreName(), target.getKey())));
        int currentCount = findPlacementByType(placedFeature, "minecraft:count")
            .get("count")
            .getAsInt();

        if (target.getValue() > previousMultiplier) {
          assertTrue(currentCount >= previousCount,
              ore.oreName() + " should not assign a smaller spawn count to a larger biome multiplier");
        }

        previousMultiplier = target.getValue();
        previousCount = currentCount;
      }
    }
  }

  private static int expectedCountFor(OreConfig ore, double multiplier) {
    return Math.max(
        1,
        (int) Math.round(ore.veinsPerChunk() * multiplier * ORE_SPAWN_RATE_MULTIPLIER * ore.spawnWeight() / 50.0d));
  }

  private static List<JsonObject> placementEntries(JsonObject placedFeature) {
    List<JsonObject> placements = new ArrayList<>();

    for (JsonElement element : placedFeature.getAsJsonArray("placement")) {
      placements.add(element.getAsJsonObject());
    }

    return placements;
  }

  private static JsonObject findPlacementByType(JsonObject placedFeature, String type) {
    return placementEntries(placedFeature).stream()
        .filter(entry -> type.equals(entry.get("type").getAsString()))
        .findFirst()
        .orElseThrow(() -> new AssertionError("Missing " + type + " placement"));
  }

  private static Set<String> listConfiguredBiomeGroups() throws IOException {
    try (Stream<Path> files = Files.list(BIOME_GROUP_DIR)) {
      return files
          .filter(path -> path.getFileName().toString().endsWith(".json"))
          .map(path -> "crucible:" + stripJsonExtension(path.getFileName().toString()))
          .collect(Collectors.toCollection(TreeSet::new));
    }
  }

  private static Map<String, OreConfig> loadOreConfigs() throws IOException {
    try (Stream<Path> files = Files.list(ORE_DIR)) {
      return files
          .filter(path -> path.getFileName().toString().endsWith(".json"))
          .sorted(Comparator.comparing(path -> path.getFileName().toString()))
          .map(OreWorldgenDataTest::readOreConfig)
          .collect(Collectors.toMap(
              OreConfig::oreName,
              ore -> ore,
              (left, right) -> left,
              LinkedHashMap::new));
    }
  }

  private static OreConfig readOreConfig(Path path) {
    JsonObject root = readJsonObject(path);
    JsonObject generation = root.getAsJsonObject("generation");
    JsonObject biomeGroups = root.getAsJsonObject("biomeGroups");
    Map<String, Double> parsedBiomeGroups = new LinkedHashMap<>();

    for (Map.Entry<String, JsonElement> entry : biomeGroups.entrySet()) {
      parsedBiomeGroups.put(entry.getKey(), entry.getValue().getAsDouble());
    }

    return new OreConfig(
        stripJsonExtension(path.getFileName().toString()),
        generation.get("spawnWeight").getAsInt(),
        generation.get("veinsPerChunk").getAsInt(),
        Map.copyOf(parsedBiomeGroups));
  }

  private static JsonObject readJsonObject(Path path) {
    try (Reader reader = Files.newBufferedReader(path)) {
      return JsonParser.parseReader(reader).getAsJsonObject();
    } catch (IOException e) {
      throw new IllegalStateException("Failed to read JSON from " + path, e);
    }
  }

  private static String placedFeatureFileName(String oreName, String biomeTarget) {
    String[] parts = biomeTarget.split(":", 2);
    return oreName + "__" + parts[0] + "__" + parts[1] + ".json";
  }

  private static String stripJsonExtension(String fileName) {
    return fileName.substring(0, fileName.length() - ".json".length());
  }

  private record OreConfig(String oreName, int spawnWeight, int veinsPerChunk, Map<String, Double> biomeGroups) {
  }
}
