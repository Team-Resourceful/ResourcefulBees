# ----- [0.6.4b] -----
- fixed bee box not properly supplying bee with data upon release
- updated patchouli (#188) (note: beecon gif doesn't work but doesn't crash now)
- mutation validation logging now displays bee name
- fixed named colors in bee jsons causing crashes
- reworked some block interactions
- hid resource pack from pack selection screen
- fixed tiered hives not upgrading properly (#189)
- general code cleanup

# ----- [0.6.3b] -----
- fixed fluid crashes in centrifuge
- changed how bee is accessed from its goals to eliminate possible mixin crashes

# ----- [0.6.2b] -----
- fixed stack overflow error caused by custom honey block

# ----- [0.6.1b] -----

- fixed jar manifest

# ----- [0.6.0b] -----

##IMPORTANT INFO FOR PACK DEVS:
- removed "defaultChance" AND "defaultWeight" from mutation objects and "chance" from mutation outputs, replaced with "chance" in the main mutation object
- Simplified mutation types to just BLOCK, ITEM, and ENTITY (BLOCK_TO_BLOCK, BLOCK_TO_FLUID, FLUID_TO_BLOCK, and FLUID_TO_FLUID all now use BLOCK)
- old mutation types will still work for the time being but when 1.17 hits those old types will be removed.

```json
{
    "type": "BLOCK",
    "inputID" : "minecraft:stone",
    "chance": 0.25,
    "outputs": [
        {"outputID" : "minecraft:iron_ore", "weight": 20},
        {"outputID" : "minecraft:gold_ore", "weight": 10},
        {"outputID" : "minecraft:diamond_ore"}
    ]
}
```

###Additions:
- Added platforms for hives over lava or water
- Added DungeonBee texture
- Bees can now have their min and max y-level for spawning configured
- Added the Beepedia, this is an item that will allow you to see information about any bee added by this mod
- you can now have 3 item and 1 fluid output for bee centrifuge output
- if a recipe uses a custom honey, when no bottles are present the proper honey fluid will deposit to the tank
- the fluid tanks in the centrifuge will now fill from left to right instead of having each slot allotted a tank (changed to allow for multiple fluid outputs per slot)
- Added CROP and ARMORED model types, and a template texture for each
- Added Honey dipper, a tool that will allow you to set a hive and flower manually for bees
- Added dungeon bee to default bees
- added advancements
- added bee box and lost bee box
- pretty nests can now be upgraded in world to T4 (hives will be removed in 1.17 in favor of this)
- silk touched hives containing bees can now be used in hive upgrade recipes
- ADDED CHECK FOR PERFORMANT MOD - WILL NOW DISPLAY WARNING TO USER WHEN INSTALLED ALONGSIDE

###Fixes:
- fixed prismarine hives showing up in ice spikes.
- a LOT of code clean up, and hopefully some significant performance increases
- bees should find their flowers easier
- fixed the chance to get cat spawn eggs from catnip combs
- fixed catnip honey having too much saturation
- backend changes to mutations to make them faster to run.
- smoker now works properly including animations and sneaking 
- bees should no longer randomly die/suffocate
- hives/apiaries weren't aging bees properly
- fixed recipe loading in jei which *should* eliminate some funky behaviors

###Changes:
- Bee Jars will now clear a bees stored hive and flower position upon release
- pick block on bee gives spawn egg in creative mode
- botania flower (polydisiac sp?) will no longer breed our bees


Under the hood there has been tons of code cleanup and general performance fixes/optimizations/null checks etc. 
Overall the mod should feel much better than it did before. There is like stuff that was fixed/changed/added not listed above.
please continue to report any bugs/issues to https://issues.resourcefulebees.com/
