package com.dungeonderps.resourcefulbees.config;

import net.minecraft.entity.passive.CustomBeeEntity;
import net.minecraftforge.common.ForgeConfigSpec;

/*
TODO REMEMBER THIS INFORMATION!!!!!!

Epic_OreoLast Friday at 9:48 PM
in 1.15.2 say I wanted to dynamically create and register any number of entities, each with distinct properties, based on a custom mob class that's an extension of a vanilla mob class, how would I go about doing so? Is there a way, when registering the entity, to change specific custom values in the entity's class so that I only need the one class as a template?
CommobleLast Friday at 9:54 PM
yes
y'know how you can register a bunch of different blocks that all use the same Block class?
same thing
Epic_OreoLast Friday at 9:58 PM
hmm ok I'll take a look at that and see how to reapply to entities
CommobleLast Friday at 9:59 PM
one thing to keep in mind:
Epic_OreoLast Friday at 10:00 PM
trying to set the mod up so users could add their own custom mob varieties given a set of parameters
CommobleLast Friday at 10:00 PM
when you register an EntityType, it takes an entity factory of the form (entityType, world) -> entity as an argument; most entities define a constructor shaped like that and use that as the factory
ah, it's hard to register things dynamically
lemme finish my other train of thought first and then I'll get to that
IItemstackLast Friday at 10:00 PM
time for custom config
Epic_OreoLast Friday at 10:02 PM
well the goal was the users would have a template in a json file and then build custom mobs using that template, but I can't account for what quantity of mobs a pack dev might want to make custom
IItemstackLast Friday at 10:02 PM
another idea would be to use 1 type but then change up the onInitialSpawn part
CommobleLast Friday at 10:02 PM
a problem with that is that datapacks aren't loaded until after entities are registered
Epic_OreoLast Friday at 10:03 PM
well the json would be in the config folder
IItemstackLast Friday at 10:03 PM
datapacks aren't loaded until after the server starts
CommobleLast Friday at 10:03 PM
same with configs, although you can trick forge into loading your configs early
Epic_OreoLast Friday at 10:03 PM
ahh
Epic_OreoLast Friday at 10:07 PM
so without tricking forge to load a config earlier, it would likely be better to register one mob type, then supply the variation data to the entity class when the configs load, and just have the mob randomly select which data set to use when it's spawned in right?
CommobleLast Friday at 10:08 PM
yeah, and it would store its properties in its NBT data
Epic_OreoLast Friday at 10:09 PM
ok that makes sense, I'd considered that route but thought maybe I could do do multiple entities at registration.
thanks for the help :slight_smile:
 */

public class ResourcefulBeesConfig {

    public static final String CATEGORY_GENERAL = "general";
    //public static final String CATEGORY_POWER = "power";

    public static ForgeConfigSpec COMMON_CONFIG;



    static {
        ForgeConfigSpec.Builder COMMON_BUILDER = new ForgeConfigSpec.Builder();
        COMMON_BUILDER.comment("General Settings").push(CATEGORY_GENERAL);
        COMMON_BUILDER.pop();
        COMMON_CONFIG = COMMON_BUILDER.build();

        setupBees();
    }

    public static void setupBees(){
        CustomBeeEntity.BEE_TYPES.add("IronBee");
        CustomBeeEntity.BEE_TYPES.add("DiamondBee");
        CustomBeeEntity.BEE_TYPES.add("EmeraldBee");
        //TestBeeEntity.BEE_TYPES.add("GoldBee");


        CustomBeeEntity.BEE_COLOR_LIST.put("IronBee", "#ffcc99");
        CustomBeeEntity.BEE_COLOR_LIST.put("DiamondBee", "#00ffff");
        CustomBeeEntity.BEE_COLOR_LIST.put("EmeraldBee", "#18eb09");
        //TestBeeEntity.BEE_COLOR_LIST.put("GoldBee", "#bdb708");

        CustomBeeEntity.FLOWERS.put("IronBee", "Poppy");
        CustomBeeEntity.FLOWERS.put("DiamondBee", "Poppy");
        CustomBeeEntity.FLOWERS.put("EmeraldBee", "Poppy");

        CustomBeeEntity.BEE_DROPS.put("IronBee", "Drop");
        CustomBeeEntity.BEE_DROPS.put("DiamondBee", "Drop");
        CustomBeeEntity.BEE_DROPS.put("EmeraldBee", "Drop");

        CustomBeeEntity.BASE_BLOCKS.put("IronBee", "Block");
        CustomBeeEntity.BASE_BLOCKS.put("DiamondBee", "Block");
        CustomBeeEntity.BASE_BLOCKS.put("EmeraldBee", "Block");

        CustomBeeEntity.MUTATION_BLOCKS.put("IronBee", "Block");
        CustomBeeEntity.MUTATION_BLOCKS.put("DiamondBee", "Block");
        CustomBeeEntity.MUTATION_BLOCKS.put("EmeraldBee", "Block");


    }



    /*
    //EntityType<? extends BeeEntity> p_i225714_1_;
    //World p_i225714_2_;
    //public static Path BEE_PATH;
    /*
    public static class CommonConfig {
        public static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
        public static final ForgeConfigSpec SPEC;

        static {
            BUILDER.comment("Mod options");
            SPEC = BUILDER.build();
        }
    }
    // might delete
    /*
    public static class BeeConfig {
        private ForgeConfigSpec.BooleanValue enabled;
        private ForgeConfigSpec.ConfigValue<ArrayList<Integer>> beeColor;
        private ForgeConfigSpec.ConfigValue<ArrayList<Integer>> hiveColor1;
        private ForgeConfigSpec.ConfigValue<ArrayList<Integer>> hiveColor2;
        private ForgeConfigSpec.ConfigValue<String> name;
        private ForgeConfigSpec.ConfigValue<String> resource;

    }

    // setup the mod config folder
    public static void setup() {
        /*
        Path configPath = FMLPaths.CONFIGDIR.get();
        Path rbConfigPath = Paths.get(configPath.toAbsolutePath().toString(), "resourcefulbees");
        // subfolder for bees
        Path rbBeesPath = Paths.get(rbConfigPath.toAbsolutePath().toString(), "bees");
        BEE_PATH = rbBeesPath;
        try {
            Files.createDirectory(rbConfigPath);
            Files.createDirectory(rbBeesPath);
        } catch (FileAlreadyExistsException e) {
            // do nothing
        } catch (IOException e) {
            //ResourcefulBeeLogger.logger.error("Failed to create resourcefulbees config directory");
        }
        writeDefault();
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, CommonConfig.SPEC, "resourcefulbees/common.toml");

    }
    */

    /*
    // write default bees
    public static void writeDefault() {
        // example resource array will add to this
        String[] name = new String[]{"iron", "gold", "diamond"};
        String[] drops = new String[]{"minecraft:iron_ingot", "minecraft:gold_ingot", "minecraft:diamond"};
        int[][] beeColors = new int[3][3], nestColors= new int[3][3];
        // REPLACE THIS WITH ACTUAL NUMBERS
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                nestColors[i][j] = 100;
                beeColors[i][j] = 100;
            }
        }
        String[] flowers = new String[]{"minecraft:poppy", "minecraft:dandelion", "minecraft:orchid"};
        String[] baseBlocks = new String[]{"minecraft:stone", "minecraft:stone", "minecraft:stone"};
        String[] mutBlocks = new String[]{"minecraft:iron_ore","minecraft:gold_ore", "minecraft:diamond_ore"};

        try {
            for (int i = 0; i < drops.length; i++) {
                File file = new File(BEE_PATH + name[i] + ".json");
                file.createNewFile();
                BufferedWriter writer = new BufferedWriter(new FileWriter(file));
                // begin
                writer.write("{\n");
                // drop
                writer.write("  \"drop\":\"" + drops[i] +"\",\n");
                // bee color
                writer.write("  \"beeColor\":" + beeColors[i].toString() + ",\n");
                // nest color
                writer.write("  \"nestColor\":" + nestColors[i] + ",\n");
                // flower
                writer.write("  \"flower\":\"" + flowers[i] + "\",\n");
                // base block
                writer.write("  \"base_block\":\"" + baseBlocks[i] + "\",\n");
                // mutated block
                writer.write("  \"mutated_block\":\"" + mutBlocks[i] + "\"\n");
                // end
                writer.write("}");
                writer.close();
            }
        } catch (FileAlreadyExistsException e) {
            // do nothing
        } catch (IOException e) {
            // log thing
        }
    }

     */

    /*
    // parse bees here. to be used where bees are registered.
    public BeeBuilderEntity parseBee(File file) throws FileNotFoundException {
        String name = file.getName(); // find good way to cut the file name
        int end = name.indexOf('.');
        name = name.substring(0, end);
        try {
            // log the reading of the file here
            BufferedReader reader = new BufferedReader(new FileReader(file));
            JsonElement jsonElement = new JsonParser().parse(reader);
            JsonObject jsonObject = jsonElement.getAsJsonObject();
            int[] beeColor = new int[3];
            int[] nestColor = new int[3];
            JsonArray ar1 = jsonObject.get("beeColor").getAsJsonArray();
            JsonArray ar2 = jsonObject.get("nestColor").getAsJsonArray();
            for (int i = 0; i < 3; i++) {
                beeColor[i] = ar1.get(i).getAsInt();
                nestColor[i] = ar1.get(i).getAsInt();
            }
            return new BeeBuilderEntity(name, jsonObject.get("drop").getAsString(), beeColor, nestColor, jsonObject.get("flower").getAsString(), jsonObject.get("base_block").getAsString(), jsonObject.get("mutated_block").getAsString(), p_i225714_1_, p_i225714_2_); // will pass in name,
        } catch (IOException e) {
            // log an error happened
        }
        return null;
    }
     */



}
