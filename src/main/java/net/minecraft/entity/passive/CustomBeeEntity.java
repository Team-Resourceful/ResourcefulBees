package net.minecraft.entity.passive;

import com.dungeonderps.resourcefulbees.ResourcefulBees;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ILivingEntityData;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.goal.BreedGoal;
import net.minecraft.entity.ai.goal.FollowParentGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.ai.goal.TemptGoal;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;

/*
Copied from old file as point of reference - do not use


        this.name = name;
        this.beeColor = new Color(beeColor[0], beeColor[1],beeColor[2]);
        this.hiveColor = new Color(hiveColor[0], hiveColor[1], hiveColor[2]);
        this.drop = new ItemStack(ForgeRegistries.ITEMS.getValue(new ResourceLocation(drop)));
        this.baseBlock = Block.getBlockFromItem(ForgeRegistries.ITEMS.getValue(new ResourceLocation(baseBlock)));
        this.mutBlock = Block.getBlockFromItem(ForgeRegistries.ITEMS.getValue(new ResourceLocation(mutBlock)));
        // create checker for what they passed in for flower
        // single flower
        // tag
        // all
        // small only
        // tall only
        switch (flower) {
            case "all":

            case "small":

            case "tall":

            default:
                if (flower.charAt(0) == '#') {
                    // do something
                } else {
                    // do something
                }
        }
 */





@SuppressWarnings("EntityConstructor")
public class CustomBeeEntity extends BeeEntity {


    //These are needed for Server->Client synchronization
    private static final DataParameter<String> BEE_COLORS = EntityDataManager.createKey(CustomBeeEntity.class, DataSerializers.STRING);

    //These are needed for dynamic creation from JSON configs
    public static final ArrayList<String> BEE_TYPES = new ArrayList<>();
    public static final HashMap<String, String> BEE_COLOR_LIST = new HashMap<>();
    public static final HashMap<String, String> FLOWERS = new HashMap<>();
    public static final HashMap<String, String> BASE_BLOCKS = new HashMap<>();
    public static final HashMap<String, String> MUTATION_BLOCKS = new HashMap<>();
    public static final HashMap<String, String> BEE_DROPS = new HashMap<>();

    //These are internal values stored for each instance
    private String Bee_Type = BEE_TYPES.get(rand.nextInt(BEE_TYPES.size()));
    private String Bee_Color = BEE_COLOR_LIST.get(Bee_Type);
    private String Bee_Flower = FLOWERS.get(Bee_Type);
    private String Base_Block = BASE_BLOCKS.get(Bee_Type);
    private String Mutation_Block = MUTATION_BLOCKS.get(Bee_Type);
    private String Bee_Drop = BEE_DROPS.get(Bee_Type);

    public CustomBeeEntity(EntityType<? extends BeeEntity> p_i225714_1_, World p_i225714_2_) {
        super(p_i225714_1_, p_i225714_2_);

    }

    //TODO Implement Dynamic Resource Loading - See KubeJS for Example
    protected ITextComponent func_225513_by_() {
        return new TranslationTextComponent("entity" + '.' + ResourcefulBees.MOD_ID + '.' + this.Bee_Type);
    }

    public float[] getBeeColorAsFloat() {
        Color tempColor = Color.decode(this.Bee_Color);
        return tempColor.getComponents(null);
    }


    //TODO Figure Out why Vanilla Bees aren't angered when attacking Modded Bees
    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new BeeEntity.StingGoal(this, 1.399999976158142D, true));
        this.goalSelector.addGoal(1, new BeeEntity.EnterBeehiveGoal());
        this.goalSelector.addGoal(2, new BreedGoal(this, 1.0D));
        this.goalSelector.addGoal(3, new TemptGoal(this, 1.25D, Ingredient.fromTag(ItemTags.FLOWERS), false));
        this.pollinateGoal = new BeeEntity.PollinateGoal();
        this.goalSelector.addGoal(4, this.pollinateGoal);
        this.goalSelector.addGoal(5, new FollowParentGoal(this, 1.25D));
        this.goalSelector.addGoal(5, new BeeEntity.UpdateBeehiveGoal());
        this.findBeehiveGoal = new BeeEntity.FindBeehiveGoal();
        this.goalSelector.addGoal(5, this.findBeehiveGoal);
        this.findFlowerGoal = new BeeEntity.FindFlowerGoal();
        this.goalSelector.addGoal(6, this.findFlowerGoal);
        this.goalSelector.addGoal(7, new BeeEntity.FindPollinationTargetGoal());
        this.goalSelector.addGoal(8, new BeeEntity.WanderGoal());
        this.goalSelector.addGoal(9, new SwimGoal(this));
        this.targetSelector.addGoal(1, (new BeeEntity.AngerGoal(this)).setCallsForHelp());
        this.targetSelector.addGoal(2, new BeeEntity.AttackPlayerGoal(this));
    }

    @Nullable
    @Override
    public ILivingEntityData onInitialSpawn(IWorld worldIn, DifficultyInstance difficultyIn, SpawnReason reason, @Nullable ILivingEntityData spawnDataIn, @Nullable CompoundNBT dataTag) {
        LOGGER.info(BEE_TYPES + " Current Bee Type");
        LOGGER.info(BEE_COLOR_LIST + " Current Bee Color");
        LOGGER.info(FLOWERS + " Current Bee Flower");
        LOGGER.info(BASE_BLOCKS + " Current Bee block");
        LOGGER.info(MUTATION_BLOCKS + " Current Bee mutation");
        LOGGER.info(BEE_DROPS + " Current Bee drop");

        LOGGER.info(this.Bee_Type + " Current Bee Type");
        LOGGER.info(this.Bee_Color + " Current Bee Color");
        LOGGER.info(this.Bee_Flower + " Current Bee Flower");
        LOGGER.info(this.Base_Block + " Current Bee block");
        LOGGER.info(this.Mutation_Block + " Current Bee mutation");
        LOGGER.info(this.Bee_Drop + " Current Bee drop");
        return super.onInitialSpawn(worldIn, difficultyIn, reason, spawnDataIn, dataTag);
    }

    @Override
    protected void registerData() {
        super.registerData();
        this.dataManager.register(BEE_COLORS, Bee_Color);
    }

    @Override
    public void readAdditional(CompoundNBT compound) {
        super.readAdditional(compound);
        this.Bee_Type = compound.getString("BeeType");
        this.Bee_Color = compound.getString("BeeColor");
        this.Bee_Flower = compound.getString("Flower");
        this.Base_Block = compound.getString("BaseBlock");
        this.Mutation_Block = compound.getString("MutationBlock");
        this.Bee_Drop = compound.getString("BeeDrop");
    }

    @Override
    public void writeAdditional(CompoundNBT compound) {
        super.writeAdditional(compound);
        compound.putString("BeeType", this.Bee_Type);
        compound.putString("BeeColor", this.Bee_Color);
        compound.putString("Flower", this.Bee_Flower);
        compound.putString("BaseBlock", this.Base_Block);
        compound.putString("MutationBlock", this.Mutation_Block);
        compound.putString("BeeDrop", this.Bee_Drop);
    }
}
