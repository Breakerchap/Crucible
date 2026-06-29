package com.remy.crucible.data;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.remy.crucible.Crucible;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.resources.Identifier;

import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public final class ResourceConfigLoader {
  private static final Gson GSON = new GsonBuilder().create();
  private static final List<OreDefinition> ORE_DEFINITIONS = loadOreDefinitions();
  private static final Map<Identifier, BiomeGroupDefinition> BIOME_GROUPS = loadBiomeGroups();

  private ResourceConfigLoader() {
  }

  public static List<OreDefinition> getOreDefinitions() {
    return ORE_DEFINITIONS;
  }

  public static Map<Identifier, BiomeGroupDefinition> getBiomeGroups() {
    return BIOME_GROUPS;
  }

  private static List<OreDefinition> loadOreDefinitions() {
    Path directory = findResourceDirectory("data/" + Crucible.MOD_ID + "/ores");

    try (Stream<Path> paths = Files.list(directory)) {
      return paths
          .filter(path -> path.toString().endsWith(".json"))
          .sorted(Comparator.comparing(path -> path.getFileName().toString()))
          .map(ResourceConfigLoader::readOreDefinition)
          .toList();
    } catch (IOException e) {
      throw new IllegalStateException("Failed to load ore definitions from " + directory, e);
    }
  }

  private static Map<Identifier, BiomeGroupDefinition> loadBiomeGroups() {
    Path directory = findResourceDirectory("data/" + Crucible.MOD_ID + "/biome_groups");
    Map<Identifier, BiomeGroupDefinition> biomeGroups = new LinkedHashMap<>();

    try (Stream<Path> paths = Files.list(directory)) {
      paths
          .filter(path -> path.toString().endsWith(".json"))
          .sorted(Comparator.comparing(path -> path.getFileName().toString()))
          .map(ResourceConfigLoader::readBiomeGroup)
          .forEach(group -> biomeGroups.put(group.id(), group));
    } catch (IOException e) {
      throw new IllegalStateException("Failed to load biome groups from " + directory, e);
    }

    return Map.copyOf(biomeGroups);
  }

  private static Path findResourceDirectory(String resourcePath) {
    return FabricLoader.getInstance()
        .getModContainer(Crucible.MOD_ID)
        .flatMap(container -> container.findPath(resourcePath))
        .orElseThrow(() -> new IllegalStateException("Missing resource directory: " + resourcePath));
  }

  private static OreDefinition readOreDefinition(Path path) {
    try (Reader reader = Files.newBufferedReader(path)) {
      RawOreDefinition raw = GSON.fromJson(reader, RawOreDefinition.class);
      String fileName = stripJsonExtension(path.getFileName().toString());

      return new OreDefinition(
          Identifier.parse(raw.material),
          Identifier.parse(raw.blocks.normal),
          Identifier.parse(raw.blocks.deepslate),
          raw.generation.enabled,
          raw.generation.spawnWeight,
          raw.generation.veinSize,
          raw.generation.veinsPerChunk,
          raw.generation.minY,
          raw.generation.maxY,
          raw.generation.bestY,
          raw.biomeGroups.entrySet().stream()
              .collect(LinkedHashMap::new,
                  (map, entry) -> map.put(Identifier.parse(entry.getKey()), entry.getValue()),
                  LinkedHashMap::putAll),
          fileName);
    } catch (IOException e) {
      throw new IllegalStateException("Failed to read ore definition " + path, e);
    }
  }

  private static BiomeGroupDefinition readBiomeGroup(Path path) {
    try (Reader reader = Files.newBufferedReader(path)) {
      RawBiomeGroupDefinition raw = GSON.fromJson(reader, RawBiomeGroupDefinition.class);

      return new BiomeGroupDefinition(
          Identifier.parse(raw.id),
          raw.biomes.stream().map(Identifier::parse).toList());
    } catch (IOException e) {
      throw new IllegalStateException("Failed to read biome group " + path, e);
    }
  }

  private static String stripJsonExtension(String fileName) {
    return fileName.substring(0, fileName.length() - ".json".length());
  }

  public record OreDefinition(
      Identifier materialId,
      Identifier normalBlockId,
      Identifier deepslateBlockId,
      boolean generationEnabled,
      int spawnWeight,
      int veinSize,
      int veinsPerChunk,
      int minY,
      int maxY,
      int bestY,
      Map<Identifier, Double> biomeGroups,
      String orePath) {
    public String materialPath() {
      return materialId.getPath();
    }
  }

  public record BiomeGroupDefinition(Identifier id, List<Identifier> biomes) {
  }

  private record RawOreDefinition(
      String material,
      RawBlocks blocks,
      RawGeneration generation,
      Map<String, Double> biomeGroups) {
  }

  private record RawBlocks(String normal, String deepslate) {
  }

  private record RawGeneration(
      boolean enabled,
      int spawnWeight,
      int veinSize,
      int veinsPerChunk,
      int minY,
      int maxY,
      int bestY) {
  }

  private record RawBiomeGroupDefinition(String id, List<String> biomes) {
  }
}
