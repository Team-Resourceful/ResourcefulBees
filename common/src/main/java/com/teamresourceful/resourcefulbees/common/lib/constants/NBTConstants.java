
package com.teamresourceful.resourcefulbees.common.lib.constants;

import com.teamresourceful.resourcefulbees.common.lib.tools.DontCheckCasing;
import com.teamresourceful.resourcefullib.common.exceptions.UtilityClassException;
import com.teamresourceful.resourcefullib.common.exceptions.ValidationException;
import org.jetbrains.annotations.ApiStatus;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public final class NBTConstants {

    private NBTConstants() throws UtilityClassException {
        throw new UtilityClassException();
    }




    //TODO make tag casing more consistent
    public static final String NBT_BEES = "Bees";
    public static final String NBT_DISPLAYNAMES = "DisplayNames";
    public static final String NBT_BEE_NAME = "DisplayName";
    public static final String NBT_BLOCK_ENTITY_TAG = "BlockEntityTag";
    @DontCheckCasing
    public static final String NBT_ENERGY = "energy";
    public static final String NBT_FEED_COUNT = "FeedCount";
    @DontCheckCasing
    public static final String NBT_FILTER_INVENTORY = "filterInv";
    @DontCheckCasing
    public static final String NBT_FILTER_RECIPE = "filterRecipe";
    @DontCheckCasing
    public static final String NBT_FLUID_OUTPUTS = "fluidOutputs";
    @DontCheckCasing
    public static final String NBT_ID = "id";
    @DontCheckCasing
    public static final String NBT_INVENTORY = "inv";
    @DontCheckCasing
    public static final String NBT_ITEM_OUTPUTS = "itemOutputs";
    @DontCheckCasing
    public static final String NBT_LOCATIONS = "locations";
    public static final String NBT_LOCKED = "Locked";
    @DontCheckCasing
    public static final String NBT_MUTATION_COUNT = "mutationCount";
    @DontCheckCasing
    public static final String NBT_PROCESS_ENERGY = "processEnergy";
    @DontCheckCasing
    public static final String NBT_PROCESS_RECIPE = "processRecipe";
    @DontCheckCasing
    public static final String NBT_PROCESS_STAGE = "processStage";
    @DontCheckCasing
    public static final String NBT_PROCESS_TIME = "processTime";
    public static final String NBT_TANK = "Tank";
    public static final String ROTATIONS = "Rotations";
    public static final String SYNC_DATA = "SyncData";
    public static final String FAKE_FLOWER_POS = "FakeFlowerPos";
    public static final String POLLEN_TOP_COLOR = "PollenTopColor";
    public static final String POLLEN_BASE_COLOR = "PollenBaseColor";
    public static final String POLLEN_ID = "PollenID";

    public static final String ENTITY_TAG = "EntityTag";
    public static class Beecon {

        @DontCheckCasing
        public static final String RANGE = "range";
        @DontCheckCasing
        public static final String ACTIVE_EFFECTS = "active_effects";
    }
    public static class Breeder {

        public static final String TIME_REDUCTION = "TimeReduction";
    }
    public static class BeeJar {

        public static final String COLOR = "Color";
        public static final String ENTITY = "Entity";
        public static final String DISPLAY_NAME = "DisplayName";
    }

    public static class HoneyDipper {
        public static final String Entity = "Entity";
    }

    public static class BeeHive {
        public static final String SMOKED = "Smoked";
        public static final String HONEYCOMBS = "Honeycombs";
    }

    public static class BeeLocator {
        public static final String LAST_BIOME_ID = "LastBiomeId";
        public static final String LAST_BIOME = "LastBiome";
        public static final String LAST_BEE = "LastBee";
    }

    public static class Beepedia {
        public static final String COMPLETE = "Complete";
        public static final String CREATIVE = "Creative";
    }

    @ApiStatus.Internal
    public static void validate() {
        List<String> errors = validate(NBTConstants.class);
        if (!errors.isEmpty()) {
            StringBuilder builder = new StringBuilder("NBTConstants validation failed! Please fix the following errors:");
            for (String error : errors) {
                builder.append("\n").append(error);
            }
            throw new ValidationException(builder.toString());
        }
    }

    @ApiStatus.Internal
    private static List<String> validate(Class<?> clazz) {
        List<String> errors = new ArrayList<>();
        for (Field field : clazz.getFields()) {
            if (field.getType() == String.class) {
                try {
                    boolean hasCheck = field.getAnnotation(DontCheckCasing.class) != null;
                    final String value = (String) field.get(null);
                    if (value == null || value.isEmpty()) {
                        errors.add("NBT constant " + clazz.getSimpleName() + "." + field.getName() + " is empty");
                        continue;
                    }
                    if (!hasCheck && !Character.isUpperCase(value.charAt(0))) {
                        errors.add("NBT constant " + clazz.getSimpleName() + "." + field.getName() + " does not start with an uppercase character");
                    }
                    if (value.contains(" ")) {
                        errors.add("NBT constant " + clazz.getSimpleName() + "." + field.getName() + " contains a space");
                    }
                } catch (IllegalAccessException e) {
                    errors.add("NBTConstants field " + clazz.getSimpleName() + "." + field.getName() + " is inaccessible");
                }
            }
        }
        for (Class<?> aClass : clazz.getClasses()) {
            errors.addAll(validate(aClass));
        }
        return errors;
    }
}
