# Crucible Major Biome Groups

Version target: Java/Fabric 1.21.4+ Overworld biomes.

Rule for ore generation:

```text
If a biome is missing from an ore's biome list, that ore does not spawn there.
```

These groups are meant for mod design, not strict vanilla climate science. The goal is to make ore distribution readable and gameplay-useful.

---

## Recommended groups

Your current groups are good, but I would add these:

```text
caves
shores_and_rivers
mushroom_fields
```

Reason:

- **Caves** are their own biome IDs now, and ores will probably want cave-specific boosts.
- **Rivers/beaches/shores** do not fit oceans or plains perfectly, but they are useful for tin, lead, clay-ish metals, etc.
- **Mushroom Fields** is too weird to shove into plains, forests, or oceans.

I would use these final groups:

```text
deserts
lukewarm_forests
warm_forests
cold_forests
plains
oceans
ice
rocky_mountainous
caves
shores_and_rivers
mushroom_fields
```

---

# Group: deserts

Dry, sandy, badlands, mesa-style terrain.

```text
minecraft:desert
minecraft:badlands
minecraft:eroded_badlands
minecraft:wooded_badlands
```

Notes:

- `minecraft:mesa` is not the modern biome ID.
- Use `minecraft:badlands` and its variants instead.

---

# Group: lukewarm_forests

Normal temperate forests. Good for general overworld resources.

```text
minecraft:forest
minecraft:birch_forest
minecraft:old_growth_birch_forest
minecraft:dark_forest
minecraft:flower_forest
minecraft:cherry_grove
```

Notes:

- `minecraft:cherry_grove` could also go in `plains`, but it is tree-heavy enough that I would put it here.
- `minecraft:flower_forest` could also go in `plains`, but it is technically a forest variant.

---

# Group: warm_forests

Hotter, wetter, tropical, savanna, and swampy biomes.

```text
minecraft:jungle
minecraft:sparse_jungle
minecraft:bamboo_jungle
minecraft:savanna
minecraft:savanna_plateau
minecraft:windswept_savanna
minecraft:swamp
minecraft:mangrove_swamp
```

Notes:

- Savannas are not forests in the normal sense, but for ore/worldgen grouping they fit better here than in deserts or plains.
- Swamps could be their own group if you want more precise ore identity later.

---

# Group: cold_forests

Cold tree biomes. Good for nickel, silver, or other colder-region metals.

```text
minecraft:taiga
minecraft:snowy_taiga
minecraft:old_growth_pine_taiga
minecraft:old_growth_spruce_taiga
minecraft:pale_garden
```

Notes:

- `minecraft:pale_garden` is technically a dark forest variant, but for your mod design it probably fits better as a cold/haunted forest group.
- If you want a more vanilla-accurate grouping, move `minecraft:pale_garden` to `lukewarm_forests`.

---

# Group: plains

Open grassy biomes, flower fields, and meadows.

```text
minecraft:plains
minecraft:sunflower_plains
minecraft:meadow
```

Notes:

- I would not put `minecraft:snowy_plains` here. Put it in `ice`.
- I would not put `minecraft:flower_forest` here unless you want flowers/plains to be one combined category.

---

# Group: oceans

All ocean biomes.

```text
minecraft:ocean
minecraft:deep_ocean
minecraft:warm_ocean
minecraft:lukewarm_ocean
minecraft:deep_lukewarm_ocean
minecraft:cold_ocean
minecraft:deep_cold_ocean
minecraft:frozen_ocean
minecraft:deep_frozen_ocean
```

Notes:

- Rivers and beaches are not here. They are in `shores_and_rivers`.
- If you later add underwater ores, this group will matter a lot.

---

# Group: ice

Snow, ice, frozen plains, frozen water, and frozen high places.

```text
minecraft:snowy_plains
minecraft:ice_spikes
minecraft:snowy_slopes
minecraft:frozen_peaks
minecraft:frozen_river
minecraft:frozen_ocean
minecraft:deep_frozen_ocean
minecraft:snowy_beach
```

Notes:

- `minecraft:frozen_peaks` could also go in `rocky_mountainous`, but if you want an “ice shit” group, put it here.
- `minecraft:snowy_taiga` stays in `cold_forests`, because it is primarily a forest.
- `minecraft:grove` could go here or in `rocky_mountainous`; I put it in `rocky_mountainous` below because it is part of mountain generation.

---

# Group: rocky_mountainous

Mountains, hills, peaks, stony terrain, and rugged highlands.

```text
minecraft:windswept_hills
minecraft:windswept_gravelly_hills
minecraft:windswept_forest
minecraft:stony_peaks
minecraft:jagged_peaks
minecraft:grove
minecraft:stony_shore
```

Notes:

- `minecraft:frozen_peaks` is in `ice`, but you can duplicate it here if your system allows multiple groups per biome.
- `minecraft:stony_shore` could also go in `shores_and_rivers`, but it is rocky enough that it is useful here.
- `minecraft:windswept_savanna` is in `warm_forests`, because it is still savanna-flavoured.

---

# Group: caves

Underground biome IDs.

```text
minecraft:dripstone_caves
minecraft:lush_caves
minecraft:deep_dark
```

Notes:

- You should definitely keep this group.
- It lets ores have cave-specific boosts without forcing them into surface biome categories.
- Example: copper likes `dripstone_caves`; lead could like `deep_dark`; softer metals could like `lush_caves`.

---

# Group: shores_and_rivers

Water-edge biomes that are not quite oceans.

```text
minecraft:river
minecraft:frozen_river
minecraft:beach
minecraft:snowy_beach
minecraft:stony_shore
```

Notes:

- This group is useful for tin, lead, clay-ish materials, and anything you want players to find near water.
- Some of these also appear in other groups:
  - `minecraft:frozen_river` also fits `ice`
  - `minecraft:snowy_beach` also fits `ice`
  - `minecraft:stony_shore` also fits `rocky_mountainous`

If your system only allows one group per biome, use this group for ore identity and avoid duplicates.

---

# Group: mushroom_fields

Weird rare island biome.

```text
minecraft:mushroom_fields
```

Notes:

- This does not fit cleanly anywhere else.
- You could use it for rare magical ore boosts, silver, weird alloys, or no ores at all.

---

# Ungrouped / not normal generation

```text
minecraft:the_void
```

Notes:

- This is not a normal survival biome.
- Do not use it for ore generation unless you are doing custom dimensions or testing.

---

# Full biome coverage checklist

This section lists every vanilla Java Overworld biome covered by the groups above.

```text
minecraft:badlands
minecraft:bamboo_jungle
minecraft:beach
minecraft:birch_forest
minecraft:cherry_grove
minecraft:cold_ocean
minecraft:dark_forest
minecraft:deep_cold_ocean
minecraft:deep_dark
minecraft:deep_frozen_ocean
minecraft:deep_lukewarm_ocean
minecraft:deep_ocean
minecraft:desert
minecraft:dripstone_caves
minecraft:eroded_badlands
minecraft:flower_forest
minecraft:forest
minecraft:frozen_ocean
minecraft:frozen_peaks
minecraft:frozen_river
minecraft:grove
minecraft:ice_spikes
minecraft:jagged_peaks
minecraft:jungle
minecraft:lukewarm_ocean
minecraft:lush_caves
minecraft:mangrove_swamp
minecraft:meadow
minecraft:mushroom_fields
minecraft:ocean
minecraft:old_growth_birch_forest
minecraft:old_growth_pine_taiga
minecraft:old_growth_spruce_taiga
minecraft:pale_garden
minecraft:plains
minecraft:river
minecraft:savanna
minecraft:savanna_plateau
minecraft:snowy_beach
minecraft:snowy_plains
minecraft:snowy_slopes
minecraft:snowy_taiga
minecraft:sparse_jungle
minecraft:stony_peaks
minecraft:stony_shore
minecraft:sunflower_plains
minecraft:swamp
minecraft:taiga
minecraft:warm_ocean
minecraft:windswept_forest
minecraft:windswept_gravelly_hills
minecraft:windswept_hills
minecraft:windswept_savanna
minecraft:wooded_badlands
```

Extra non-normal biome:

```text
minecraft:the_void
```

---

# If you want no duplicate biomes

Some biomes naturally fit more than one group. If your ore system only allows one biome group per biome, use this stricter setup:

```text
deserts:
  minecraft:desert
  minecraft:badlands
  minecraft:eroded_badlands
  minecraft:wooded_badlands

lukewarm_forests:
  minecraft:forest
  minecraft:birch_forest
  minecraft:old_growth_birch_forest
  minecraft:dark_forest
  minecraft:flower_forest
  minecraft:cherry_grove

warm_forests:
  minecraft:jungle
  minecraft:sparse_jungle
  minecraft:bamboo_jungle
  minecraft:savanna
  minecraft:savanna_plateau
  minecraft:windswept_savanna
  minecraft:swamp
  minecraft:mangrove_swamp

cold_forests:
  minecraft:taiga
  minecraft:snowy_taiga
  minecraft:old_growth_pine_taiga
  minecraft:old_growth_spruce_taiga
  minecraft:pale_garden

plains:
  minecraft:plains
  minecraft:sunflower_plains
  minecraft:meadow

oceans:
  minecraft:ocean
  minecraft:deep_ocean
  minecraft:warm_ocean
  minecraft:lukewarm_ocean
  minecraft:deep_lukewarm_ocean
  minecraft:cold_ocean
  minecraft:deep_cold_ocean
  minecraft:frozen_ocean
  minecraft:deep_frozen_ocean

ice:
  minecraft:snowy_plains
  minecraft:ice_spikes
  minecraft:snowy_slopes
  minecraft:frozen_peaks

rocky_mountainous:
  minecraft:windswept_hills
  minecraft:windswept_gravelly_hills
  minecraft:windswept_forest
  minecraft:stony_peaks
  minecraft:jagged_peaks
  minecraft:grove

caves:
  minecraft:dripstone_caves
  minecraft:lush_caves
  minecraft:deep_dark

shores_and_rivers:
  minecraft:river
  minecraft:frozen_river
  minecraft:beach
  minecraft:snowy_beach
  minecraft:stony_shore

mushroom_fields:
  minecraft:mushroom_fields
```

This strict setup covers every normal Overworld biome exactly once.
