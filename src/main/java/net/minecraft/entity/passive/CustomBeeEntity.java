package net.minecraft.entity.passive;

import com.dungeonderps.resourcefulbees.ResourcefulBees;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ILivingEntityData;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.goal.BreedGoal;
import net.minecraft.entity.ai.goal.FollowParentGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.ai.goal.TemptGoal;
import net.minecraft.item.Item;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.tags.ItemTags;
import net.minecraft.tileentity.IronBeehiveBlockEntity;
import net.minecraft.tileentity.TileEntity;
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
    private static final DataParameter<String> BEE_COLOR = EntityDataManager.createKey(CustomBeeEntity.class, DataSerializers.STRING);

    //These are needed for dynamic creation from JSON configs
    public static final ArrayList<String> BEE_TYPES = new ArrayList<>();
    public static final HashMap<String, ArrayList<String>> BEE_INFO = new HashMap<>();
    // index 0 = color, 1 = flower, 2 = base, 3 = mut, 4 = drop, 5 = spawnInWorld, 6 = dimensionListString, 7 = biomeListString

    //These are internal values stored for each instance
    private String Bee_Type = "default";
    private String Bee_Flower = "Poppy";
    private String Base_Block = "minecraft:dirt";
    private String Mutation_Block = "minecraft:cobblestone";
    private String Bee_Drop = "minecraft:stick";


    public CustomBeeEntity(EntityType<? extends BeeEntity> p_i225714_1_, World p_i225714_2_) {
        super(p_i225714_1_, p_i225714_2_);
    }

    //TODO Implement Dynamic Resource Loading - See KubeJS for Example
    protected ITextComponent func_225513_by_() {
        return new TranslationTextComponent("entity" + '.' + ResourcefulBees.MOD_ID + '.' + this.Bee_Type);
    }

    public float[] getBeeColorAsFloat() {
        String beeColor = getBeeColor();
        Color tempColor = Color.decode(beeColor);
        return tempColor.getComponents(null);
    }

    public String getBeeColor(){

        //This check is necessary to keep the client from crashing randomly
        //TODO LOW PRIORITY - figure out why render happens before temp color has a value.
        String tempColor = this.dataManager.get(BEE_COLOR);
        if (tempColor.isEmpty()){
            return "#FFFFFF";
        } else {
            return this.dataManager.get(BEE_COLOR);
        }
    }


    //TODO Figure Out why Vanilla Bees aren't angered when attacking Modded Bees
    // - beesourceful has same issue - not tested since adding (new Class[0])
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
        this.targetSelector.addGoal(1, (new BeeEntity.AngerGoal(this)).setCallsForHelp(new Class[0]));
        this.targetSelector.addGoal(2, new BeeEntity.AttackPlayerGoal(this));
    }

    @Override
    public boolean isHiveValid() {
        if (!this.hasHive()) {
            return false;
        } else {
            TileEntity blockEntity = this.world.getTileEntity(this.hivePos);
            return blockEntity instanceof IronBeehiveBlockEntity
                    && ((IronBeehiveBlockEntity) blockEntity).isAllowedBee(this);
        }
    }

    @Nullable
    @Override
    public ILivingEntityData onInitialSpawn(IWorld worldIn, DifficultyInstance difficultyIn, SpawnReason reason, @Nullable ILivingEntityData spawnDataIn, @Nullable CompoundNBT dataTag) {
        selectRandomBee();
        return super.onInitialSpawn(worldIn, difficultyIn, reason, spawnDataIn, dataTag);
    }

    @Override
    protected void registerData() {
        super.registerData();
        //Need to supply default value
        this.dataManager.register(BEE_COLOR, "#FFFFFF");
    }

    @Override
    public void readAdditional(CompoundNBT compound) {
        super.readAdditional(compound);
        this.Bee_Type = compound.getString("BeeType");
        this.dataManager.set(BEE_COLOR, compound.getString("BeeColor"));
        this.Bee_Flower = compound.getString("Flower");
        this.Base_Block = compound.getString("BaseBlock");
        this.Mutation_Block = compound.getString("MutationBlock");
        this.Bee_Drop = compound.getString("BeeDrop");
    }

    @Override
    public void writeAdditional(CompoundNBT compound) {
        super.writeAdditional(compound);
        compound.putString("BeeType", Bee_Type);
        compound.putString("BeeColor", getBeeColor());
        compound.putString("Flower", Bee_Flower);
        compound.putString("BaseBlock", Base_Block);
        compound.putString("MutationBlock", Mutation_Block);
        compound.putString("BeeDrop", Bee_Drop);
    }

    //easier to manage bee selection for testing, could be useful for bee breeding
    //or other possible reasons for changing bee type.
    //if fleshed out further - may want to consider separate class for handling bee types
    private void selectRandomBee(){
        this.Bee_Type = BEE_TYPES.get(rand.nextInt(BEE_TYPES.size()));
        this.dataManager.set(BEE_COLOR, BEE_INFO.get(Bee_Type).get(0));
        this.Bee_Flower = BEE_INFO.get(Bee_Type).get(1);
        this.Base_Block = BEE_INFO.get(Bee_Type).get(2);
        this.Mutation_Block = BEE_INFO.get(Bee_Type).get(3);
        this.Bee_Drop = BEE_INFO.get(Bee_Type).get(4);
    }

    public Item getHoneyComb(){
        return ResourcefulBees.ObjectHolders.Items.RESOURCEFUL_HONEYCOMB;
    }

}
