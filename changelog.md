# -----{ 1.0.0-alpha.21 }-----
- re-release of alpha.20 due to broken workflow

# -----{ 1.0.0-alpha.20 }-----
- Fixed Centrifuge Inputs not auto-processing next available recipe (watch recipe input amounts!)
- Added position validation for Energy Inputs - sides-only for now.

# -----{ 1.0.0-alpha.19 }-----
- Fixed `generateDefaults` config value being automatically set to false too early in load process.
  - This caused default trait files to not get copied over and used.
  - You will need to set `generateDefaults` back to true for all changes to take effect!

# -----{ 1.0.0-alpha.18 }-----
- Changed despawn logic again. This time bees will be marked as persistent when released from a jar, spawned from an egg, or when bred. All other bees will despawn following normal vanilla mechanics and when the player moves >128 blocks away
- fixed nether quartz and rgbee not giving combs (and verified!)
- Bees should stop occasionally dragging across the ground when pollinating however they may seem slightly further away from normal flowers as a result.

# -----{ 1.0.0-alpha.17 }-----
- Changed despawn logic to use modified vanilla despawning mechanics. Bees should now only despawn when the player moves >128 blocks from them AND they fail to meet the following criteria:
  - Has a custom name
  - Is a passenger
  - Is leashed
  - Is a baby
  - Has a have in range 
  - Has a saved flower position
  - Is carrying nectar
  - Has beecon in range
    - The 10-minute timer will no longer apply. This needs extensive testing!

# -----{ 1.0.0-alpha.16 }-----
- Bumped Beekeeper trade experience from 2 -> 3 and doubled initial trade amounts to allow for faster leveling 
- Updated dependency versions
- Made Centrifuge Filter-style slots more distinguishable
- Made corrections to Centrifuge Config
- Centrifuge Input can now process combs without setting the filter slot
  - Filter slot will restrict processing but allow any valid centrifuge recipe ingredient to be placed 
- Fixed Honey Gen not generating rf when no upgrades are placed
- Fixed wither trait not preventing wither damage
- Resourceful Web Config is now implemented. (See the mod's details for more information on usage)
- Added astronaut bee textures
- Fixed RGBee not having correct honeycomb ID

# -----{ 1.0.0-alpha.15 }-----
- It's a brand-new mod.

### Notable Highlights:
- Apiaries are no longer a multiblock and have their own inventory. They also no longer have the unique bee requirement and can hold up to 20 bees at T4
- The Breeder now has its own inventory and is separate from the apiary.
- Honey Generator is now recipe driven and can be upgraded.
- Centrifuge multiblock can be confusing at first but can also become a very powerful multiblock. In a general sense, it's like taking a bunch of small multiblocks and turning them into one large one. It can be very simple, or it can be very complex. You decide!
- We will be making a video guide on using the Centrifuge multiblock very soon, which should be very helpful in using it.
- There is a `/resourcefulbees` command which you can use to print json templates to the logs. The command isn't complete, but can get you started. Feel free to ask questions in Discord!
- Bees have significantly more customization options than they did in 1.16.5.
- _Most_ jsons are datapack driven.
- Machines are all recipe driven.

#### Please keep in mind this mod is still in alpha. There can and likely will be more breaking changes, and as such the mod should only be included in packs if you accept the risks.
#### We are releasing this publicly in such an early state because we would like to get more feedback from the community. We need to know what you like and don't like. What bugs are there? What is confusing and should be clarified better? How can we improve and make this mod the best bee mod there is?

<br>Please report any and all bugs on our [GitHub Issue Tracker](https://github.com/Team-Resourceful/ResourcefulBees/issues/new/choose)
<br>Feel free to ask questions in our [Discord](https://discord.resourcefulbees.com)
