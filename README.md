# TFC Woodworking

An addon for [TerraFirmaCraft](https://github.com/TerraFirmaCraft/TerraFirmaCraft/tree/1.20.x) 1.20.1

Makes crafting wooden things more immersive through a tree (pun intended) of in-world crafting

### Download:
- [Curseforge](https://www.curseforge.com/minecraft/mc-mods/tfc-woodworking)
- [Modrinth](https://modrinth.com/mod/tfc-woodworking)

### Notes:
- Use [Axe Interactions section](#axe-interactions) and [Saw Interactions section](#saw-interactions) to see all recipes until JEI integration and/or Patchouli book page is implemented
- Most amounts shown in parentheses are defaults, can be changed in config file
- Canoes from FirmaCiv is possible. It requires 3 stripped logs placed facing horizontally. You will need to strip them separately beforehand.

### Features:
- Stripping logs/wood is only possible when 4 sides are empty (no solid blocks)
- Chopping logs/wood is only possible when there is no block above
- Fair wood recipe (2 bark + 1 log -> 1 wood) (2 bast + 1 stripped log -> stripped wood)
- Custom log pile stores logs, debarked logs, debarked log halves and debarked log quarters
- Tools take damage with a chance dependent on tool's tier
- If chopped log projectile hits a log pile, instead of dropping into the world it is inserted inside
- A lot of config options to play with
- Tags (items/logs_log, items/logs_half, items/logs_quarter) to modify what items can go into custom version of log pile

### TODO:
- Totem carving
- JEI/Patcholi integration
- ArborFirmaCraft compat

### Axe Interactions:
- Log -> Stripped Log + (4) bark
- Stripped Log -> Debarked Log + (4) bast
- Wood -> Stripped Wood + (6) bark
- Stripped Wood -> Debarked Log + (6) bast
- Debarked Log -> (2) Debarked Log Half
- Debarked Log Half -> (2) Debarked Log Quarter

### Saw Interactions:
- Log -> (4) Log Fence + Sawdust
- Planks -> Stairs + (2) Fence + Sawdust
- Stairs -> Slab + (2) Fence + Sawdust
- Slab -> (2) Trapdoor + Sawdust
- Debarked Log Half -> (4) Supports + Sawdust
- Debarked Log Quarter -> (2) Lumber + Sawdust
