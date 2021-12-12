package com.teamresourceful.resourcefulbees.datagen.providers.lang;

import com.teamresourceful.resourcefulbees.client.data.LangGeneration;
import com.teamresourceful.resourcefulbees.common.lib.constants.TranslationConstants;
import com.teamresourceful.resourcefulbees.common.registry.minecraft.*;
import com.teamresourceful.resourcefulbees.datagen.bases.BaseLanguageProvider;
import net.minecraft.data.DataGenerator;
import org.jetbrains.annotations.NotNull;

public class ModLanguageProvider extends BaseLanguageProvider {

    public ModLanguageProvider(DataGenerator gen) {
        super(gen, TranslationConstants.class);
    }

    @Override
    public @NotNull String getName() {
        return "Resourceful Bees Language Provider";
    }

    @Override
    protected void addTranslations() {
        addBlocks();
        addItems();
        addPotions();
        addItemGroups();
        addAdvancements();
        addDefaultBees();
        addHoney();
        addTraits();
        addHoneycombs();
        addBook();
        add("tooltip.resourcefulbees.bee.creator", "§7- %s");
        add("entity.minecraft.villager.resourcefulbees.beekeeper", "Beekeeper");
        super.addTranslations();
    }

    private void addHoneycombs() {
        addHoneycomb("iron", "Iron");
        addHoneycomb("gold", "Gold");
        addHoneycomb("emerald", "Emerald");
        addHoneycomb("diamond", "Diamond");
        addHoneycomb("redstone", "Redstone");
        addHoneycomb("nether_quartz", "Nether Quartz");
        addHoneycomb("lapis", "Lapis");
        addHoneycomb("coal", "Coal");
        addHoneycomb("ender", "Ender");
        addHoneycomb("creeper", "Beeper");
        addHoneycomb("pigman", "Pigman");
        addHoneycomb("skeleton", "Skeleton");
        addHoneycomb("wither", "Wither");
        addHoneycomb("zombee", "Zombee");
        addHoneycomb("netherite", "Netherite");
        addHoneycomb("rgbee", "RGBee");
        addHoneycomb("dragon", "Dragon");
        addHoneycomb("catnip", "Catnip");
    }

    private void addTraits() {
        add("trait.resourcefulbees.wither", "Wither");
        add("trait.resourcefulbees.blaze", "Blaze");
        add("trait.resourcefulbees.can_swim", "Can Swim");
        add("trait.resourcefulbees.creeper", "Creeper");
        add("trait.resourcefulbees.zombee", "Zombee");
        add("trait.resourcefulbees.pigman", "Pigman");
        add("trait.resourcefulbees.ender", "Ender");
        add("trait.resourcefulbees.nether", "Nether");
        add("trait.resourcefulbees.oreo", "Oreo");
        add("trait.resourcefulbees.kitten", "Kitten");
        add("trait.resourcefulbees.slimy", "Slimy");
        add("trait.resourcefulbees.desert", "Desert");
        add("trait.resourcefulbees.angry", "Angry");
        add("trait.resourcefulbees.teleport", "Teleport");
        add("trait.resourcefulbees.flammable", "Flammable");
        add("trait.resourcefulbees.spider", "Spider");

        add("trait.resourcefulbees.special.teleport", "During the day the bee can randomly teleport to an empty position within 4 blocks. This ability is stopped by the Ender Beecon and Bee Hives.");
        add("trait.resourcefulbees.special.flammable", "This effect will randomly light the bee on fire.");
        add("trait.resourcefulbees.special.angry", "Bees with this effect will become aggressive and cause bees around to join them. They can and will attack you, Calming effects will stop them from being angry.");
        add("trait.resourcefulbees.special.slimy", "Slimy bees will make squishy sounds and spurt out slime particles at random.");
        add("trait.resourcefulbees.special.spider", "Allows bees to fly through spiderwebs without issues.");
    }

    private void addItemGroups() {
        add(ItemGroupResourcefulBees.RESOURCEFUL_BEES, "Resourceful Bees");
        add(ItemGroupResourcefulBees.RESOURCEFUL_BEES_HONEY, "Resourceful Bees - Honey");
        add(ItemGroupResourcefulBees.RESOURCEFUL_BEES_BEES, "Resourceful Bees - Spawn Eggs");
        add(ItemGroupResourcefulBees.RESOURCEFUL_BEES_COMBS, "Resourceful Bees - Combs");
        add(ItemGroupResourcefulBees.RESOURCEFUL_BEES_HIVES, "Resourceful Bees - Hives");
    }

    private void addHoney() {
        addHoney("catnip", "Catnip", true, true);
        addHoney("rainbow", "Rainbow", true, true);
        add("fluid.resourcefulbees.honey", "Honey");
    }

    private void addPotions() {
        addEffect(ModEffects.CALMING, "Calming");
        addPotion(ModPotions.CALMING_POTION, "Calming");
        addPotion(ModPotions.LONG_CALMING_POTION, "Calming");
    }

    private void addDefaultBees() {
        addBee("gold", "Gold");
        addBee("iron", "Iron");
        addBee("emerald", "Emerald");
        addBee("diamond", "Diamond");
        addBee("redstone", "Redstone");
        addBee("nether_quartz", "Nether Quartz");
        addBee("lapis", "Lapis");
        addBee("coal", "Coal");
        addBee("ender", "Ender");
        addBee("beeper", "Beeper");
        addBee("pigman", "Pigman");
        addBee("skeleton", "Skeleton");
        addBee("wither", "Wither");
        addBee("zombee", "Zombee");
        addBee("netherite", "Netherite");
        addBee("oreo", "Oreo");
        addBee("slimy", "Slimy");
        addBee("icy", "Icy");
        addBee("kitten", "Kitten");
        addBee("dragon", "Dragon");
        addBee("dungeon", "Dungeon");
        addBee("brown_mushroom", "Brown Mushroom");
        addBee("red_mushroom", "Red Mushroom");
        addBee("crimson_fungus", "Crimson Fungus");
        addBee("warped_fungus", "Warped Fungus");
        addBee("yeti", "Yeti");
        add(LangGeneration.ITEM_RESOURCEFULBEES+"rgbee_bee_spawn_egg", "RGBee Spawn Egg");
        add(LangGeneration.ITEM_RESOURCEFULBEES+"rgbee_bee", "RGBee");
    }

    private void addItems() {
        addItem(ModItems.OREO_COOKIE, "Epic Oreo");
        addItem(ModItems.CRAFTING_BEE_BOX, "Lost Bee Box");
        addItem(ModItems.BEE_BOX, "Bee Box");
        addItem(ModItems.BEEPEDIA, "Beepedia");
        addItem(ModItems.HONEY_DIPPER, "Honey Dipper");
        addItem(ModItems.SCRAPER, "Scraper");
        addItem(ModItems.SMOKER, "Bee Smoker");
        addItem(ModItems.BELLOW, "Bellow");
        addItem(ModItems.SMOKERCAN, "Smoker Canister");
        addItem(ModItems.WAX, "Beeswax");
        add("item.resourcefulbees.bee_jar_empty", "Empty Bee Jar");
        add("item.resourcefulbees.bee_jar_filled", "Filled Bee Jar");
        addItem(ModItems.IRON_STORAGE_UPGRADE, "Iron Storage Upgrade");
        addItem(ModItems.GOLD_STORAGE_UPGRADE, "Gold Storage Upgrade");
        addItem(ModItems.DIAMOND_STORAGE_UPGRADE, "Diamond Storage Upgrade");
        addItem(ModItems.EMERALD_STORAGE_UPGRADE, "Emerald Storage Upgrade");
        addItem(ModItems.APIARY_BREEDER_UPGRADE, "Breeder Upgrade");
        addItem(ModItems.APIARY_BREED_TIME_UPGRADE, "Breed Time Upgrade");
        addItem(ModItems.HONEY_FLUID_BUCKET, "Honey Bucket");
        addItem(ModItems.T1_HIVE_UPGRADE, "Tier 1 Nest Upgrade");
        addItem(ModItems.T2_HIVE_UPGRADE, "Tier 2 Nest Upgrade");
        addItem(ModItems.T3_HIVE_UPGRADE, "Tier 3 Nest Upgrade");
        addItem(ModItems.T4_HIVE_UPGRADE, "Tier 4 Nest Upgrade");
    }

    private void addBlocks() {
        addBlock(ModBlocks.WAX_BLOCK, "Beeswax Block");
        addBlock(ModBlocks.GOLD_FLOWER, "Golden Flower");
        addBlock(ModBlocks.OAK_BEE_NEST, "Oak Bee Nest");
        addBlock(ModBlocks.BIRCH_BEE_NEST, "Birch Bee Nest");
        addBlock(ModBlocks.BROWN_MUSHROOM_BEE_NEST, "Brown Mushroom Bee Nest");
        addBlock(ModBlocks.CRIMSON_BEE_NEST, "Crimson Bee Nest");
        addBlock(ModBlocks.CRIMSON_NYLIUM_BEE_NEST, "Crimson Nylium Bee Nest");
        addBlock(ModBlocks.DARK_OAK_BEE_NEST, "Dark Oak Bee Nest");
        addBlock(ModBlocks.RED_MUSHROOM_BEE_NEST, "Red Mushroom Bee Nest");
        addBlock(ModBlocks.SPRUCE_BEE_NEST, "Spruce Bee Nest");
        addBlock(ModBlocks.WARPED_BEE_NEST, "Warped Bee Nest");
        addBlock(ModBlocks.WARPED_NYLIUM_BEE_NEST, "Warped Nylium Bee Nest");
        addBlock(ModBlocks.ACACIA_BEE_NEST, "Acacia Bee Nest");
        addBlock(ModBlocks.GRASS_BEE_NEST, "Grass Bee Nest");
        addBlock(ModBlocks.JUNGLE_BEE_NEST, "Jungle Bee Nest");
        addBlock(ModBlocks.NETHER_BEE_NEST, "Nether Bee Nest");
        addBlock(ModBlocks.PRISMARINE_BEE_NEST, "Prismarine Bee Nest");
        addBlock(ModBlocks.PURPUR_BEE_NEST, "Purpur Bee Nest");
        addBlock(ModBlocks.WITHER_BEE_NEST, "Wither Bee Nest");
        addBlock(ModBlocks.T1_APIARY_BLOCK, "Tier 1 Apiary");
        addBlock(ModBlocks.T2_APIARY_BLOCK, "Tier 2 Apiary");
        addBlock(ModBlocks.T3_APIARY_BLOCK, "Tier 3 Apiary");
        addBlock(ModBlocks.T4_APIARY_BLOCK, "Tier 4 Apiary");
        addBlock(ModBlocks.APIARY_BREEDER_BLOCK, "Apiary Breeder");
        addBlock(ModBlocks.HONEY_GENERATOR, "Honey Generator");
        addBlock(ModBlocks.ENDER_BEECON, "Ender Beecon");
        addBlock(ModBlocks.SOLIDIFICATION_CHAMBER, "Solidification Chamber");
        addBlock(ModBlocks.HONEY_POT, "Honey Pot");
    }

    private void addAdvancements() {
        add("advancements.resourcefulbees.root.title", "Resourceful Bees");
        add("advancements.resourcefulbees.root.description", "By Epic Oreo, ThatGrayBoat, Dawn Felstar and Vik");
        add("advancements.resourcefulbees.wax.title", "Wax On, Wax Off");
        add("advancements.resourcefulbees.wax.description", "Obtain some Wax");
        add("advancements.resourcefulbees.scraper.title", "Comb Harvesting Device");
        add("advancements.resourcefulbees.scraper.description", "Obtain a Scraper");
        add("advancements.resourcefulbees.bee_jar.title", "You Put Bees in This");
        add("advancements.resourcefulbees.bee_jar.description", "Obtain a Bee Jar");
        add("advancements.resourcefulbees.beepedia.title", "Gotta See 'em All");
        add("advancements.resourcefulbees.beepedia.description", "Obtain a Beepedia");
        add("advancements.resourcefulbees.ender_beecon.title", "It Protects Bees");
        add("advancements.resourcefulbees.ender_beecon.description", "Obtain an Ender Beecon");
        add("advancements.resourcefulbees.honey_generator.title", "Honey Power?");
        add("advancements.resourcefulbees.honey_generator.description", "Obtain a Honey Generator");
        add("advancements.resourcefulbees.obtain_bee_nest.title", "Buzzy Box");
        add("advancements.resourcefulbees.obtain_bee_nest.description", "Obtain a Bee Nest");
        add("advancements.resourcefulbees.t1_hive_upgrade.title", "Mini Bee Shack");
        add("advancements.resourcefulbees.t1_hive_upgrade.description", "Obtain a T1 Hive Upgrade");
        add("advancements.resourcefulbees.t2_hive_upgrade.title", "Mini Bee Apartment");
        add("advancements.resourcefulbees.t2_hive_upgrade.description", "Obtain a T2 Hive Upgrade");
        add("advancements.resourcefulbees.t3_hive_upgrade.title", "Mini Bee House");
        add("advancements.resourcefulbees.t3_hive_upgrade.description", "Obtain a T3 Hive Upgrade");
        add("advancements.resourcefulbees.t4_hive_upgrade.title", "Mini Bee Mansion");
        add("advancements.resourcefulbees.t4_hive_upgrade.description", "Obtain a T4 Hive Upgrade");
        add("advancements.resourcefulbees.t1_apiary.title", "Make a Bee Hive a Home");
        add("advancements.resourcefulbees.t1_apiary.description", "Obtain a T1 Apiary");
        add("advancements.resourcefulbees.t2_apiary.title", "Love your Bees");
        add("advancements.resourcefulbees.t2_apiary.description", "Obtain a T2 Apiary");
        add("advancements.resourcefulbees.t3_apiary.title", "Beeginning the Endgame");
        add("advancements.resourcefulbees.t3_apiary.description", "Obtain a T3 Apiary");
        add("advancements.resourcefulbees.t4_apiary.title", "It's Time to Stop");
        add("advancements.resourcefulbees.t4_apiary.description", "Obtain a T4 Apiary");
        add("advancements.resourcefulbees.iron_storage_upgrade.title", "More Comb Storage");
        add("advancements.resourcefulbees.iron_storage_upgrade.description", "Obtain an Iron Storage Upgrade");
        add("advancements.resourcefulbees.gold_storage_upgrade.title", "Even More Comb Storage");
        add("advancements.resourcefulbees.gold_storage_upgrade.description", "Obtain a Gold Storage Upgrade");
        add("advancements.resourcefulbees.diamond_storage_upgrade.title", "A LOT of Comb Storage");
        add("advancements.resourcefulbees.diamond_storage_upgrade.description", "Obtain a Diamond Storage Upgrade");
        add("advancements.resourcefulbees.emerald_storage_upgrade.title", "MAXIMUM COMB STORAGE");
        add("advancements.resourcefulbees.emerald_storage_upgrade.description", "Obtain an Emerald Storage Upgrade");
        add("advancements.resourcefulbees.bee_box.title", "It's Full of Bees!");
        add("advancements.resourcefulbees.bee_box.description", "Obtain a bee box");
        add("advancements.resourcefulbees.crafting_bee_box.title", "Lost and Found");
        add("advancements.resourcefulbees.crafting_bee_box.description", "Obtain a Lost Bee Box");
        add("advancements.resourcefulbees.honey_dipper.title", "Bee see what Bee do");
        add("advancements.resourcefulbees.honey_dipper.description", "Obtain a Honey Dipper");
        add("advancements.resourcefulbees.smoker.title", "Bee Safety 101");
        add("advancements.resourcefulbees.smoker.description", "Obtain a Smoker");
        add("advancements.resourcefulbees.apiary_breed_time_upgrade.title", "Faster Breeding");
        add("advancements.resourcefulbees.apiary_breed_time_upgrade.description", "Obtain a Breed Time Upgrade");
        add("advancements.resourcefulbees.apiary_breeder_upgrade.title", "More Breeding");
        add("advancements.resourcefulbees.apiary_breeder_upgrade.description", "Obtain a Breeder Upgrade");
        add("advancements.resourcefulbees.apiary_breeder.title", "Apiary Breeding");
        add("advancements.resourcefulbees.apiary_breeder.description", "Obtain an Apiary Breeder");
        add("advancements.resourcefulbees.apiary_storage.title", "Comb Storage");
        add("advancements.resourcefulbees.apiary_storage.description", "Obtain an Apiary Storage");
        add("advancements.resourcefulbees.beepedia_unlock_bee.title", "Start your Journey!");
        add("advancements.resourcefulbees.beepedia_unlock_bee.description", "Add your first bee to the Beepedia");
        add("advancements.resourcefulbees.beepedia_complete.title", "Beepedia Champion!");
        add("advancements.resourcefulbees.beepedia_complete.description", "Find every bee and add them to your Beepedia");
        add("advancements.resourcefulbees.collect_bee.title", "Caught One");
        add("advancements.resourcefulbees.collect_bee.description", "Collect a bee with a Bee Jar");
        add("advancements.resourcefulbees.fifty_shades_of_bees.title", "Stinging Reviews");
        add("advancements.resourcefulbees.fifty_shades_of_bees.description", "Obtain a copy of Fifty Shades of Bees");
        add("advancements.resourcefulbees.kitten_bee.title", "Awww Baby Kitten!");
        add("advancements.resourcefulbees.kitten_bee.description", "Hunt and capture the sneaky Kitten Bee");
        add("advancements.resourcefulbees.oreo_bee.title", "Don't Eat This");
        add("advancements.resourcefulbees.oreo_bee.description", "Find and catch the elusive Oreo Bee");
    }

    private void addBook() {
        add("book.resourcefulbees.landing_text", "Welcome Resourceful Beekeeper! This guide is much less filled with bee erotica than it is information about beekeeping. We hope that it serves you well in understanding the basics.$(br)$(o)$(#ff0000)Please keep in mind that MOST, if not ALL, bees are added by pack developers!$()");
        add("book.resourcefulbees.apiary_category", "Apiary Basics");
        add("book.resourcefulbees.apiary_category.desc", "The best of the best beekeepers maintain apiaries for the varieties of bees they own. Here, we will cover how to build one, why you want one, what it can do to expand your capabilities as a beekeeper.");
        add("book.resourcefulbees.apiary_category.apiary_basics", "Apiary Basics");
        add("book.resourcefulbees.apiary_category.apiary_basics.1", "There are $(or)four tiers$() to the apiary.$(br2)Each tier $(or)increases$() the amount of $(or)combs$() generated per bee and $(or)decreases$() the amount of $(or)time$() the bee spends in the Apiary.$(br2)An Apiary can only be occupied by nine $(or)unique$() bees.");
        add("book.resourcefulbees.apiary_category.apiary_basics.2", "This means that no more than one of each bee type can be inside the Apiary block at a time.$(br2)As many bees as you'd like can be inside the structure however.$(br2)Some players have found optimal ways to keep bees regularly going in and out so they are always generating combs.");
        add("book.resourcefulbees.apiary_category.apiary_basics.3", "To make adding and removing bees to and from the structure easier, an $(thing)Import$() and $(thing)Export$() feature is provided.$(br2)To import a bee simply capture a bee in a jar, place it in the import slot and click the button.$(br2)To export a bee, fill the jar slot with empty jars, lock and select the bee you wish to");
        add("book.resourcefulbees.apiary_category.apiary_basics.4", "export, and click the button.$(br2)When a bee has been imported, it is $(or)locked$() by default.$(br2)Locking is useful for $(or)preventing comb generation$() of a specific bee type also, as the bee won't be able to exit until it is $(or)unlocked$().");
        add("book.resourcefulbees.apiary_category.apiary_breeder", "Apiary Breeder");
        add("book.resourcefulbees.apiary_category.apiary_breeder.1", "The $(item)Apiary Breeder$() is an automated way to breed bees.$(br2)To use the breeder you must capture each parent bee in a jar and place the filled jars in the appropriate slots.$(br2)You would then need to determine which");
        add("book.resourcefulbees.apiary_category.apiary_breeder.2", "flowers each parent needs to trigger its \"love\" state and place them in the proper slots.$(br2)Lastly, supply empty jars for the child bee to be put into.$(br2)The breeder always runs, but it only consumes resources when it can place a filled jar into the $(l,resourcefulbees,apiary/apiary_storage)$(item)Apiary Storage$().");
        add("book.resourcefulbees.apiary_category.apiary_breeder.3", "By default each breed process takes 2 minutes, however this can be reduced through the use of upgrades.$(br2)In addition, only one breed process can occur unless breed upgrades are used in which case the max is five breeds at once.");
        add("book.resourcefulbees.apiary_category.apiary_breeder.4", "The breeder is an optional block that that can be added to an Apiary Structure.");
        add("book.resourcefulbees.apiary_category.apiary_storage", "Apiary Storage");
        add("book.resourcefulbees.apiary_category.apiary_storage.1", "When a bee has generated honeycombs in the $(l,resourcefulbees,apiary/apiary_basics)$(item)Apiary$(), they get put into the $(item)Apiary Storage$().$(br2)The Apiary Storage is also used for the outputs from the Apiary Breeder.$(br2)There are $(or)four upgrades$() available to increase the capacity.");
        add("book.resourcefulbees.apiary_category.apiary_storage.2", "You can connect the storage block to item cables or import busses from other mods for automation purposes.");
        add("book.resourcefulbees.apiary_category.multiblock", "Building an Apiary");
        add("book.resourcefulbees.apiary_category.multiblock.1", "Building an Apiary Multiblock is extremely $(or)simple$().$(br2)The structure consists of an $(l,resourcefulbees,apiary/apiary_basics)$(item)Apiary$(), an $(l,resourcefulbees,apiary/apiary_storage)$(item)Apiary Storage$(), any block from the $(thing)valid_apiary$() tag, and $(o)optionally$() an $(l,resourcefulbees,apiary/apiary_breeder)$(item)Apiary Breeder$().");
        add("book.resourcefulbees.apiary_category.multiblock.2", "The size of the structure should be 7x6x7 so the interior is 5x4x5 as a $(or)hollow box$().$(br2)The $(l,resourcefulbees,apiary/apiary_storage)$(item)Apiary Storage$() and $(l,resourcefulbees,apiary/apiary_breeder)$(item)Apiary Breeder$() (if used) can be placed anywhere as part of the skeletal structure.$(br2)The $(l,resourcefulbees,apiary/apiary_basics)$(item)Apiary$(), however, must be placed $(or)facing inward$() on the face of any one side of the box.");
        add("book.resourcefulbees.apiary_category.multiblock.3", "A fantastic way to see this in action would be to enter a creative world and use the $(thing)Creative Build$() button to build a super basic version.$(br2)You can also use the $(thing)Visualize$() button on an $(or)unvalidated$() apiary to see what blocks are invalid and what the structure bounds should look like in world.");
        add("book.resourcefulbees.apiary_category.multiblock.4", "The unvalidated Apiary screen also has $(thing)Offset$() buttons to set the position of the Apiary for when it is checking the validity of the structure.$(br)$(o)These buttons are based on the direction the Apiary is facing.$()$(br2)On the next page you can see what a completed structure would look like as well as visualize it in the world.");
        add("book.resourcefulbees.apiary_category.multiblock.5", "Visualize the structure to get an idea of how to build it.");
        add("book.resourcefulbees.apiary_category.multiblock.6", "While the structure on the left looks ugly in the book, you can make it look beautiful in the world.$(br2)Also, it is recommended that the bottom blocks of the structure be $(or)grass$() blocks or whatever blocks you need to plant \"flowers\" on.$(br2)It is also recommended to put a light inside to prevent mob spawns.");
        add("book.resourcefulbees.apiary_category.multiblock.7", "One final important note regarding the Apiary Multiblock,$(br2)You can use the same skeletal structure to build multiple Apiaries.$(br2)The only caveat in doing so is that $(or)each apiary must have its own storage and breeder block!$()");
        add("book.resourcefulbees.bee_category", "Bee Basics");
        add("book.resourcefulbees.bee_category.desc", "In this section you will learn basic information about bee keeping.$(br2)Topics covered include, how and where bees $(or)spawn$(), how bees can be $(or)bred$(), what bee $(or)traits$() are and why they are important, and how resourceful bees can $(or)mutate$() blocks into other blocks.");
        add("book.resourcefulbees.bee_category.breeding", "Bee Breeding");
        add("book.resourcefulbees.bee_category.breeding.1", "Some bees may be configured to be breedable. If a bee is breedable, it $(o)may$() have predefined parents, but will $(l)always$() be able to breed with another bee of the same type.");
        add("book.resourcefulbees.bee_category.breeding.2", "Bee breeding works very much like vanilla breeding, with one notable exception. Some bees may be configured to require multiple $(or)feed items$() in order to trigger the \"love\" state.$(br2)The $(or)feed item$() for a bee doesn't have to specifically be a flower. It can be configured to use any type of item or tag.");
        add("book.resourcefulbees.bee_category.breeding.3", "Bees can only be bred in world. Optionally, an $(item)Apiary Breeder$() can be used to breed bees. Visit that section of the book for more information on its usage.");
        add("book.resourcefulbees.bee_category.mutation", "Bee Mutation");
        add("book.resourcefulbees.bee_category.mutation.1", "Some bees are customized with a special pollination effect called $(l)Mutations$(). Mutations allow a bee to convert a block into another block when the bee has $(or)nectar$() and $(or)valid hive$().");
        add("book.resourcefulbees.bee_category.mutation.2", "A mutation input is the block or block tag being converted. A mutation output is the block the input is being converted to.$(br2)Each bee can have a customizable number of mutations. By this, we mean that the bee can either mutate one block, ten blocks, or a hundred blocks before it would need to enter the hive and reset the counter.");
        add("book.resourcefulbees.bee_category.mutation.3", "$(o)It is possible to automate the bee mutations using other mods. We will leave that up to you to figure out.$()");
        add("book.resourcefulbees.bee_category.spawning", "Bee Spawning");
        add("book.resourcefulbees.bee_category.spawning.1", "Some bees may have been configured to spawn in the world. The $(or)spawn weight$(), $(or)biome$() and $(or)light level$() can be customized as well as the $(or)min$() and $(or)max$() $(or)group size$().$(br2)Nest generation is also configurable.");
        add("book.resourcefulbees.bee_category.spawning.2", "When configured to generate, nests will $(l)always$() generate in biomes where bees were configured to spawn.$(br)Weighting for nest generation can be configured independently for Nether, End and Overworld biome categories.$(br)The texture for nests will vary based on the biome.");
        add("book.resourcefulbees.bee_category.spawning.3", "When nests spawn in the world, they may come prefilled with bees. The number of bees can vary from 0 to 1/2 the $(or)hive max bees$() config value.");
        add("book.resourcefulbees.bee_category.traits", "Bee Traits");
        add("book.resourcefulbees.bee_category.traits.1", "Special bees require special $(or)traits$(). A trait given to a bee can provide a variety of effects such as $(or)damage types$() and $(or)immunities$(), $(or)potion effects$() and $(or)immunities$(), $(or)special abilities$(), or $(or)particle effects$(). For more information on traits, see the $(o)$(or)beepedia$()");
        add("book.resourcefulbees.centrifuge_category", "Centrifuge Basics");
        add("book.resourcefulbees.centrifuge_category.desc", "The chapters here will walk through the various honeycomb processing machines available to your disposal. These machines are key to converting honeycombs into something more useful in your day to day journeys.");
        add("book.resourcefulbees.centrifuge_category.mechanical", "Mechanical Centrifuge");
        add("book.resourcefulbees.centrifuge_category.mechanical.1", "To be able to obtain resources from honeycombs, a beekeeper needs to have a centrifuge. Early on, a mechanical centrifuge is sufficient enough to get going. Centrifuging also requires bottles to store liquid honey.");
        add("book.resourcefulbees.centrifuge_category.mechanical.2", "The Mechanical Centrifuge is an entry-level centrifuge. Right-click while sneaking to use.");
        add("book.resourcefulbees.centrifuge_category.multiblock", "Multiblock Centrifuge");
        add("book.resourcefulbees.centrifuge_category.multiblock.1", "The Multiblock Centrifuge is a powerhouse compared to the $(item)Powered Centrifuge$(). It has the capability to process Honeycomb Blocks in addition to normal honeycombs. It can also process 3 separate combs simultaneously.");
        add("book.resourcefulbees.centrifuge_category.multiblock.2", "Visualize the structure to get an idea of how to build it.");
        add("book.resourcefulbees.centrifuge_category.multiblock.3", "To build the multiblock you will need $(or)one $(item)Centrifuge Controller$() and $(or)35 $(item)Centrifuge Casings$().$(br2)The structure size is 3x4x3 with the controller one block up from the bottom in the center of any face.");
        add("book.resourcefulbees.centrifuge_category.powered", "Powered Centrifuge");
        add("book.resourcefulbees.centrifuge_category.powered.1", "The powered centrifuge will increase efficiency in processing honeycombs at the expense of energy.");
        add("book.resourcefulbees.honeycomb_category", "Honeycomb Basics");
        add("book.resourcefulbees.honeycomb_category.desc", "This section will walk you through honeycomb generation and collection. We will also discuss several tools available to the most avid beekeepers.");
        add("book.resourcefulbees.honeycomb_category.honeycombs", "Honeycombs");
        add("book.resourcefulbees.honeycomb_category.honeycombs.1", "$(or)Honeycombs$() are the most $(or)integral$() byproduct of bees.$(br2)They can be $(or)separated$() in $(items)Centrifuges$() to produce $(or)honey$(), $(or)wax$(), and various other $(or)resources$().$(br2)Every bee capable of producing a honeycomb will");
        add("book.resourcefulbees.honeycomb_category.honeycombs.2", "produce it's $(or)own$() type of comb.$(br2)Bear in mind however that $(or)not all$() bees may be able to produce a honeycomb.$(br2)As wonderful as honeycombs can be for producing honey, wax, and resources, they are also a $(or)nutritious$() delicacy capable of providing $(or)sustenance$().");
        add("book.resourcefulbees.honeycomb_category.scraper", "Scraper");
        add("book.resourcefulbees.honeycomb_category.scraper.1", "The $(item)Scraper$() is a tool that allows an aspiring beekeeper to collect honeycombs from a beehive. Unlike shears however, the scraper will only collect $(or)one comb$() at a time from a hive. $(l)All$() combs must be collected in order to reset the $(or)Honey Level$().");
        add("book.resourcefulbees.honeycomb_category.smoker", "Smoker");
        add("book.resourcefulbees.honeycomb_category.smoker.1", "The $(item)Smoker$() is an absolute must for any bee keepers just getting their feet $(or)sticky$(). Using a Smoker on a hive will have the same effect as placing a $(or)campfire$() beneath the hive. For everyone else, smoking a hive $(or)prevents$() bees from becoming $(#ff0000)angry$() when collecting honeycombs.");
        add("book.resourcefulbees.honeycomb_category.smoker.2", "To craft a smoker you must first make a Bellow and Smoker Canister which can be combined.$(br2)$(o)Wonder what would happen if I used it on a bee?$()");
        add("book.resourcefulbees.honeycomb_category.tiered_hives", "Tiered Beehives");
        add("book.resourcefulbees.honeycomb_category.tiered_hives.1", "Tiered beehives are a $(l)massive$() upgrade from the vanilla beehive.$(br2)Tiered Hives are also the only hives which support generation of our custom honeycombs, other than the apiary of course.");
        add("book.resourcefulbees.honeycomb_category.tiered_hives.2", "For the uninitiated, when a bee collects pollen from a \"flower\", the bee will fly into a hive and after a period of time will emerge to repeat the process. When the bee is released from the hive a $(or)honeycomb$() will have been generated.$(br2)When the $(or)Honey Level$() reaches 5, the combs can be collected.");
        add("book.resourcefulbees.honeycomb_category.tiered_hives.3", "Combs can be collected from a Tiered Hive in $(or)two ways$().$(br2)First, when enabled, combs can be collected following the vanilla method using $(item)Shears$().$(br2)The second method is using a $(l,resourcefulbees,honeycomb_gathering/scraper)$(item)Scraper$(), which is $(l)always enabled$().");
        add("book.resourcefulbees.honeycomb_category.tiered_hives.4", "Both methods can be automated using the $(item)Dispenser$() and some $(#ff0000)redstone$().$(br2)$(l)$(#ff0000)Beware$(), however, that if a Tiered Hive has not been $(l,resourcefulbees,honeycomb_gathering/smoker)$(or)smoked$() prior to collecting the combs, any bees inside will release and become angry.");
        add("book.resourcefulbees.help_category", "Help and Bugs");
        add("book.resourcefulbees.help_category.desc", "Most bees in this in this mod are customized by the pack developers.$(br2)If you have any bugs, questions, or feedback for us please click the links button to the right.");
        add("book.resourcefulbees.help_category.links", "Links");
        add("book.resourcefulbees.help_category.links.1", "Join our $(l,https,//discord.gg/rteh4mh)Discord$(/l) for any questions, concerns, feedback, or suggestions.$(br2)Please report any bugs or issue you may have on our $(l,https,//github.com/Resourceful-Bees/ResourcefulBees/issues/new/choose)Github$(/l)$(br2)Visit our $(l,https,//resourceful-bees.readthedocs.io/en/1.16.3/)wiki$(/l) to learn how to make bees!");
        add("book.resourcefulbees.help_category.links.2", "An apiary tutorial can be found $(l,https,//www.youtube.com/watch?v=kGdwnjtBRRk)here$(/l)$(br2)");
        add("book.resourcefulbees.jei_category", "JEI Categories");
        add("book.resourcefulbees.jei_category.desc", "This section covers the JEI categories which are critical to using this mod.");
        add("book.resourcefulbees.jei.apiary_category", "Apiary Category");
        add("book.resourcefulbees.jei.apiary_category.title", "Apiary Category");
        add("book.resourcefulbees.jei.apiary_category.text.1", "Outputs of all four apiaries for a single bee.");
        add("book.resourcefulbees.jei.apiary_category.text.2", "$(#bd0000)1$() - The bee producing the honeycomb or other output$(br2)$(#bd0000)2$() - The tiered apiary the bee must go in to produce the output$(br2)$(#bd0000)3$() - The output produced by the bee$(br2)Note, Hives display in JEI the same as apiaries");
        add("book.resourcefulbees.jei.block_mutation_category", "Block Mutation Cat.");
        add("book.resourcefulbees.jei.block_mutation_category.title", "Block Mutation Cat.");
        add("book.resourcefulbees.jei.block_mutation_category.text.1", "Bees can mutate blocks or fluids into other blocks");
        add("book.resourcefulbees.jei.block_mutation_category.text.2", "$(#bd0000)1$() - The bee performing the block mutation$(br2)$(#bd0000)2$() - The block or fluid being mutated when the bee is carrying nectar$(br2)$(#bd0000)3$() - The resulting block from the mutation$(br2)$(#bd0000)4$() - A helpful hint is displayed when mouse is hovering over");
        add("book.resourcefulbees.jei.breed_category", "Breed Category");
        add("book.resourcefulbees.jei.breed_category.title", "Breed Category");
        add("book.resourcefulbees.jei.breed_category.text.1", "Some bees can only be obtained by breeding");
        add("book.resourcefulbees.jei.breed_category.text.2", "$(#bd0000)1$() - The bees getting kinky$(br2)$(#bd0000)2$() - The item and quantity needed to arouse the bee$(br2)$(#bd0000)3$() - The chance these parents will birth this child$(br2)$(#bd0000)4$() - The result of the bees not using a contraceptive");
        add("book.resourcefulbees.jei.centrifuge_category", "Centrifuge Category");
        add("book.resourcefulbees.jei.centrifuge_category.title", "Centrifuge Category");
        add("book.resourcefulbees.jei.centrifuge_category.text.1", "The centrifuge is used to get resources from honeycombs");
        add("book.resourcefulbees.jei.centrifuge_category.text.2", "$(#bd0000)1$() - The bottle input is optional$(br2)$(#bd0000)2$() - The main input and amount needed$(br2)$(#bd0000)3$() - The main output produced. This can also be a fluid$(br2)$(#bd0000)4$() - The secondary output produced$(br2)$(#bd0000)5$() - If a bottle is supplied, this is the output produced");
        add("book.resourcefulbees.jei.centrifuge_category.text.3", "$(#bd0000)6$() - Some recipes are multiblock recipes only$(br2)$(#bd0000)7$() - The chance of getting the associated output");
        add("book.resourcefulbees.jei.flower_category", "Flower Category");
        add("book.resourcefulbees.jei.flower_category.title", "Flower Category");
        add("book.resourcefulbees.jei.flower_category.text.1", "Bees need to pollinate the proper flower to produce a honeycomb");
        add("book.resourcefulbees.jei.flower_category.text.2", "$(#bd0000)1$() - The bee doing the pollination$(br2)$(#bd0000)2$() - The flower the bee needs to pollinate");
        add("book.resourcefulbees.jei.fluid_mutation_category", "Fluid Mutation Cat.");
        add("book.resourcefulbees.jei.fluid_mutation_category.title", "Fluid Mutation Cat.");
        add("book.resourcefulbees.jei.fluid_mutation_category.text.1", "Bees can mutate blocks into fluids or fluids into other fluids");
        add("book.resourcefulbees.jei.fluid_mutation_category.text.2", "$(#bd0000)1$() - The bee performing the fluid mutation$(br2)$(#bd0000)2$() - The block or fluid being mutated when the bee is carrying nectar$(br2)$(#bd0000)3$() - The resulting fluid from the mutation$(br2)$(#bd0000)4$() - A helpful hint is displayed when mouse is hovering over");
        add("book.resourcefulbees.ender_beecon_category", "Ender Beecon");
        add("book.resourcefulbees.ender_beecon_category.desc", "Consumes honey to protect your bees.");
        add("book.resourcefulbees.ender_beecon_category.basics", "Beecon Basics");
        add("book.resourcefulbees.ender_beecon_category.teleportation", "Ender Disruption");
        add("book.resourcefulbees.ender_beecon_category.tanks", "Filling the Beecon");
        add("book.resourcefulbees.ender_beecon_category.effects", "Effects");
        add("book.resourcefulbees.ender_beecon_category.options", "Beecon Options");
        add("book.resourcefulbees.ender_beecon_category.basics.1", "The Ender Beecon is designed with giving all your bees freedom to mingle in mind.$(br2)It does this by effecting your bees in various ways. One major one is that it will prevent bees from despawning within it's effect range.$(br2)Other ways bees are effected can be found in later chapters.");
        add("book.resourcefulbees.ender_beecon_category.teleportation.range", "Disruption Range");
        add("book.resourcefulbees.ender_beecon_category.teleportation.1", "If you have issues with bees teleporting away then the Ender Beecon is for you.$(br2)To prevent your bees from teleporting you simply need to supply your Ender Beecon with honey.$(br2)So long as the internal tank has honey in it it will prevent bees from teleporting.");
        add("book.resourcefulbees.ender_beecon_category.teleportation.2", "The range that the Ender Beecon will prevent bees from teleporting can be found in the interface.$(br2)This range will increase with each effect that is active.");
        add("book.resourcefulbees.ender_beecon_category.tanks.stacking", "Tank Stacking");
        add("book.resourcefulbees.ender_beecon_category.tanks.1", "The Ender Beecon requires honey to function as such you");
        add("book.resourcefulbees.ender_beecon_category.tanks.2", "will need to be able to fill it with honey.$(br2)This can be done in many ways, with either a honey bucket or honey bottle you can insert the item into the interface and the tank will automatically fill itself from the contents.$(br2)You can also simply right click the tank with bottle or buckets to fill it that way.");
        add("book.resourcefulbees.ender_beecon_category.tanks.3", "If you need more honey input, you can simply put any tank with honey in it underneath the Ender Beecon.$(br2)It will automatically refill itself from the tank, You can also simply pipe honey in from any side of the device.");
        add("book.resourcefulbees.ender_beecon_category.tanks.4", "When you upgrade your tank from one tier to another it will retain any honey left in it.");
        add("book.resourcefulbees.ender_beecon_category.effects.calming", "The Calming effect when applied to bees will prevent them from getting angry, It will also calm bees down who are already angry.$(br2)If a bee has the Angry trait it will calm them down, allowing you to keep them around other bees.");
        add("book.resourcefulbees.ender_beecon_category.effects.water_breathing", "Water Breathing when applied to bees will prevent them from drowning when coming in contact with water.$(br2)Now your bees can take a nice little drink without dying.");
        add("book.resourcefulbees.ender_beecon_category.effects.fire_resistance", "Fire Resistance does what it says on the tin, it will prevent all forms of fire damage from hurting your bees.$(br2)Now you can keep your nether bees around without worry.");
        add("book.resourcefulbees.ender_beecon_category.effects.regeneration", "Regeneration is for any other damage that bees can take, warning though it tends to be quite honey consuming to keep this active.");
        add("book.resourcefulbees.ender_beecon_category.effects.1", "The Ender Beecon can apply certain effects to your bees. While effects are active they will drain honey from the internal tank.$(br2)The more effects that are active the larger the effect range will be and the more honey the Beecon will consume.");
        add("book.resourcefulbees.ender_beecon_category.options.beam_color", "Beam Color");
        add("book.resourcefulbees.ender_beecon_category.options.show_beam", "Toggling the Beam");
        add("book.resourcefulbees.ender_beecon_category.options.muting" ,  "Muting the Beecon");
        add("book.resourcefulbees.ender_beecon_category.options.1", "The Ender Beecon's beam will change color based on the type of honey that is inside the internal tank.$(br2)Default honey and Catnip honey will leave the Beecon beam white if you don't want to stand out.$(br2)Rainbow colored honey will make the beam rainbow too.");
        add("book.resourcefulbees.ender_beecon_category.options.2", "If you want to hide the Ender Beecon's beam all you need to do is right click the Beecon with a stick.$(br2)Now you can hide your Beecons without them bee-ing seen.");
        add("book.resourcefulbees.ender_beecon_category.options.3", "If you can't stand the noise the Ender Beecon makes you can suppress it by simply right clicking it with some wool.$(br2)If you want to hear your Beecon again simply right click it again.");
        add("book.resourcefulbees.ess_tools_category", "Essential Tools");
        add("book.resourcefulbees.ess_tools_category.desc", "There are some tools that a beekeeper just can't do without.");
        add("book.resourcefulbees.ess_tools_category.honey_dipper", "Honey Dipper");
        add("book.resourcefulbees.ess_tools_category.honey_dipper.1", "Right-click a bee to select it. Right-click an appropriate flower to set its flower position or right-click a hive to set its hive position.$(br2)Shift + right-click to clear selected bee. Setting a bees position will also clear the selected bee.");
        add("book.resourcefulbees.ess_tools_category.honey_dipper.2", "More uses for the Honey Dipper are planned ,)");
        add("book.resourcefulbees.ess_tools_category.bee_jar", "Bee Jar");
        add("book.resourcefulbees.ess_tools_category.bee_jar.1", "The Bee Jar is used for capturing and transporting bees. When releasing a bee from the jar, the flower and hive positions will be cleared while the rest of a bees memories and data are retained.");
        add("book.resourcefulbees.ess_tools_category.bee_jar.2", "Bee Jars are reusable!");
        add("book.resourcefulbees.ess_tools_category.bee_box", "Bee Box");
        add("book.resourcefulbees.ess_tools_category.bee_box.1", "The bee box works like the jar except that it can carry up to 10 bees at once.");
        add("book.resourcefulbees.ess_tools_category.bee_box.2", "A lost bee box can be obtained when upgrading hives containing bees.");
        add("book.resourcefulbees.ess_tools_category.beepedia", "Beepedia");
        add("book.resourcefulbees.ess_tools_category.beepedia.1", "The beepedia is the ultimate device for learning about the bees in your world. It provides all the same information as JEI, plus more! Seriously, check it out!");
        add("book.resourcefulbees.ess_tools_category.beepedia.2", "The beepedia can also keep track of which bees you've interacted with by right-clicking a bee while holding it.");
    }
}
