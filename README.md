# TFC Woodworking

An addon for [TerraFirmaCraft](https://github.com/TerraFirmaCraft/TerraFirmaCraft/tree/1.20.x) 1.20.1

Makes crafting wooden things more immersive through a tree (pun intended) of in-world crafting

### Notes:
- Use [Axe Interactions section](#axe-interactions) and [Saw Interactions section](#saw-interactions) to see all recipes until JEI integration and/or Patchouli book page is implemented
- Most amounts shown in parentheses are defaults, can be changed in config file

### Features:
- Stripping logs/wood is only possible when 4 sides are empty (no solid blocks)
- Chopping logs/wood is only possible when there is no block above
- Fair wood recipe (2 bark + 1 log -> 1 wood) (2 bast + 1 stripped log for stripped wood)
- Custom log pile stores logs, debarked logs, debarked log halves and debarked log quarters. Both total limit of the log pile as well as individual capacities for each type of item can be changed in the config file
- Tools take damage with a chance dependant on tool's tier
- If chopped log projectile hits a log pile, instead of dropping into the world it is inserted inside

### TODO:
- Totem carving
- JEI/Patcholi integration

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