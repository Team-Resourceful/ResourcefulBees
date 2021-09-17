**please continue to report any bugs/issues to https://issues.resourcefullbees.com/**

# ----- [0.9.9.6] -----
- Fixed hive ingredient crashing servers, closing #283

# ----- [0.9.9.5] -----
- Changed how the apiary recipe works to allow for upgraded hives and nests that have been upgraded with a hive upgrade item.
- Added a recipe to empty honey generators
- Fixed the manual centrifuge not respecting bottle output count, therefore closing #274
- Fixed #280 by updating the item handler on load instead of only on upgrade change.
- Fixed #266 Updated the apiary breeder to only update processes when a slot has changed, still requires a change in the future to not be called as much but reduces it to be called a lot less than every tick. and as precaution I kept the check when its about to process to check again just in case if it didn't update correctly.
- Fixed the conversion recipes to duplicating buckets. Closes #257 

# ----- [0.9.9.4] -----
- stacked filled bee jars (i.e. ones you get from mutations and quests) no longer clear all nbt data when you spawn a bee with them, it will now just decrease the stack size.
- added "isInvulnerable" option to CombatData
- removed unnessacary validApiary block and item tags (we forgot to remove them when we changed how apiaries are validated, you can still use the tag to add blocks to apiaries, that hasn't been removed)

# ----- [0.9.9.3] -----
- Changed Entity to Entity mutations in JEI to show the entity model rather than a spawnegg, this should hopefully remove a lot of confusion.  (The beepedia still shows entity models like always)

# ----- [0.9.9.2] -----
- Fixed an edge case crash when mutationData isn't added to the bee json  
- Removed particles from Ender Beecon effects. (This should fix issue with performance when a beecon is granting a lot of effects to bees)

# ----- [0.9.9.1] -----
- Fixed an issue where you couldn't start a server with resourcefulbees
- fixed default icy bee not spawning 

# ----- [0.9.9] -----
- Fixed an issue where centrifuge input counts weren't respected. 
- Added Honey Glass. (Glass which allows only players or only bees to pass through) 
- Added a texture for creative beepedia. 
- Added angry dungeon bee texture. 
- Added a Yeti Dev Bee. 
- Made it so that apiaries don't require a floor and now check for if a block has collision for if it's a valid block. 
- Valid apiary blocks using `hasCollision` check now have the tag added to their items. (This will allow you to search for it in JEI)
- Fixed Item Mutations outputting items with a blank nbt tag. 
- Bees can be parents in a breed without needing to have a self breed option. (`isBreedable` is false but has feed item) 
- Added lore options to bees in JEI and Beepedia. 
- Added 2 new models Yeti Horns and Guardian Spikes. 
- Fixed layer textures not loading properly when defined with a preceding `/`

# ----- [0.6.7.2b] -----
- Fixed an error trying to process bee damage immunities, in doing so we actually added the ability for all damage sources including modded ones to be allowed.

# ----- [0.6.7.1b] -----
- Fixed a crash when attempting to breed bees with no feed return item

# ----- [0.6.7b] -----

- Added Spider trait, this trait allows bees to pass through spider webs without being slowed.
- Changed default bees to use various types of flowers
- Updated chinese translation
- Added Bottomless Honey Pot, this block allows for voiding of honey with ease.
- Added 7 more patreon reward textures.
- Added the ability for each bee to have their apiary output types set individually 
  (e.g. `"apiaryOutputTypes" : ["COMB", "COMB", "BLOCK", "COMB"],`)
- Mutations that result or use a bee will now show up in the beepedia breeding tab of that bee
- Spawn egg item mutations will now show up in the beepedia breeding tab of that bee
- Added flower and hive positions for bees to TOP display 

- Fixed base layer textures not showing up on additions if the bee had custom colors.
- Fixed honey generator not sending power to immersive engineering wires.
- Fixed the NBT tag persistanceRequired not being respected with bee despawning rules.
- Fixed blocks without items not showing as bee flowers.
- Fixed entity mutations not working when natural spawns are turned off.
- Made optimisations for bees finding their flowers.
- Fixed comb blocks being able to be centrifuged in non multiblock centrifuges.
- Fixed comb block centrifuge timing.
- Fixed custom honey/comb recipes not showing up on multiplayer in some cases.
- Fixed Bees reappearing out of hive makes honeycomb count decrease to miminum of 2.
- Fixed bee shadows not changing with bee size
- Fixed a duplication bug
- Fixed Ender beecon options page in Fifty Shades of Bees not showing up properly
- Disabled an experimental item.

# ----- [0.6.6a] -----
- Added Honey Congealer (honey goes in, blocks come out)
- added an interface to all honey tanks, you can now input and output honey bottles from the interface
- doubled the capactity of all honey tanks
- fixed a bug causing beecons to not save their inventory properly
- added an extra check to hopefully stop the beepedia from crashing if for SOME reason the comb of a bee suddenly decides to stop existing.
- added missing angry textures for armoured and crop bees.
- added kitten bee base texture for use with patreon bees
- when a bee file breaks it will now crash again, this time with a more detailed crash report stating exactly which bee/honey/trait broke
- fixed a crash when attempting to toggle a beecon's beam/sound on multiplayer
- fixed Hive not getting Tier Modifier as a float preventing T2 Hive from having the correct number of combs and bees.
- fixes for interfaces to hopefully stop them from moving items to other slots in their interfaces when shift clicking an item.
- baby bees are now immune to despawning

Alpha Features
- Honey pipes exist in code but do not work and are purely cosmetic in this patch.

# ----- [0.6.5b] -----
- fixed a crash related to centrifuges when it can't find a valid tank
- cleaned up patchouli effects page

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

## IMPORTANT INFO FOR PACK DEVS:
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

### Additions:
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

### Fixes:
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

### Changes:
- Bee Jars will now clear a bees stored hive and flower position upon release
- pick block on bee gives spawn egg in creative mode
- botania flower (polydisiac sp?) will no longer breed our bees


Under the hood there has been tons of code cleanup and general performance fixes/optimizations/null checks etc. 
Overall the mod should feel much better than it did before. There is like stuff that was fixed/changed/added not listed above.
