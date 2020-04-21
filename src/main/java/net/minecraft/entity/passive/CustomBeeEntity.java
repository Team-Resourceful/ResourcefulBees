package net.minecraft.entity.passive;

import com.dungeonderps.resourcefulbees.RegistryHandler;
import com.dungeonderps.resourcefulbees.ResourcefulBees;
import com.dungeonderps.resourcefulbees.config.BeeInfo;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.DoublePlantBlock;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.state.properties.DoubleBlockHalf;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tileentity.IronBeehiveBlockEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.village.PointOfInterest;
import net.minecraft.village.PointOfInterestManager;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;
import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/*
Copied from old file as point of reference - do not use

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
    public static final HashMap<String, BeeInfo> BEE_INFO = new HashMap<>();


    //These are internal values stored for each instance
    private String beeType = "default";

    public CustomBeeEntity(EntityType<? extends BeeEntity> p_i225714_1_, World p_i225714_2_) {
        super(p_i225714_1_, p_i225714_2_);
    }

    //*************************** STANDARD BEE METHODS BELOW **********************************************************

    //TODO Figure Out why Vanilla Bees aren't angered when attacking Modded Bees
    // - beesourceful has same issue - not tested since adding (new Class[0])
    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new BeeEntity.StingGoal(this, 1.4, true));
        this.goalSelector.addGoal(1, new BeeEntity.EnterBeehiveGoal());
        this.goalSelector.addGoal(2, new BreedGoal(this, 1.0D));
        this.goalSelector.addGoal(3, new TemptGoal(this, 1.25D, Ingredient.fromTag(ItemTags.FLOWERS), false));
        this.pollinateGoal = new PollinateGoal2();
        this.goalSelector.addGoal(4, this.pollinateGoal);
        this.goalSelector.addGoal(5, new FollowParentGoal(this, 1.25D));
        this.goalSelector.addGoal(5, new UpdateBeehiveGoal2());
        this.findBeehiveGoal = new FindBeehiveGoal2();
        this.goalSelector.addGoal(5, this.findBeehiveGoal);
        this.findFlowerGoal = new BeeEntity.FindFlowerGoal();
        this.goalSelector.addGoal(6, this.findFlowerGoal);
        this.goalSelector.addGoal(7, new FindPollinationTargetGoal2());
        this.goalSelector.addGoal(8, new BeeEntity.WanderGoal());
        this.goalSelector.addGoal(9, new SwimGoal(this));
        this.targetSelector.addGoal(1, (new BeeEntity.AngerGoal(this)).setCallsForHelp());
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

    public boolean doesHiveHaveSpace(BlockPos pos) {
        TileEntity blockEntity = this.world.getTileEntity(pos);
        return blockEntity instanceof IronBeehiveBlockEntity && !((IronBeehiveBlockEntity) blockEntity).isFullOfBees();
    }

    //*************************** INTERNAL CLASSES AND METHODS FOR BEE GOALS BELOW ***********************************
    //TODO Finish implementing Bee Goals with custom bee data
    // - Pollination, Flower/Mutation/Etc

    protected class UpdateBeehiveGoal2 extends BeeEntity.UpdateBeehiveGoal {

        public UpdateBeehiveGoal2() {
            super();
        }

        public List<BlockPos> getNearbyFreeHives() {
            BlockPos blockpos = new BlockPos(CustomBeeEntity.this);
            PointOfInterestManager pointofinterestmanager = ((ServerWorld) world).getPointOfInterestManager();
            Stream<PointOfInterest> stream = pointofinterestmanager.func_219146_b(pointOfInterestType ->
                            pointOfInterestType == RegistryHandler.IRON_BEEHIVE_POI.get(), blockpos,
                    20, PointOfInterestManager.Status.ANY);
            return stream.map(PointOfInterest::getPos).filter(CustomBeeEntity.this::doesHiveHaveSpace)
                    .sorted(Comparator.comparingDouble(pos -> pos.distanceSq(blockpos))).collect(Collectors.toList());
        }
    }

    protected class FindPollinationTargetGoal2 extends BeeEntity.FindPollinationTargetGoal {

        public FindPollinationTargetGoal2(){
            super();
        }

        @Override
        public void tick() {
            applyPollinationEffect();
        }
    }

    //***** Pollination Effect is used for Mutating Blocks.

    public void applyPollinationEffect(){
        if (rand.nextInt(1) == 0) {
            for (int i = 1; i <= 2; ++i) {
                BlockPos beePosDown = (new BlockPos(CustomBeeEntity.this)).down(i);
                BlockState state = world.getBlockState(beePosDown);
                Block block = state.getBlock();
                if (validFillerBlock(block)) {
                    world.playEvent(2005, beePosDown, 0);
                    world.setBlockState(beePosDown, ForgeRegistries.BLOCKS.getValue(BEE_INFO.get(beeType).getResource(BEE_INFO.get(beeType).getMutBlock())).getDefaultState());
                    addCropCounter();
                }
            }
        }
    }

    public boolean validFillerBlock(Block block){
        return block.getRegistryName().equals(BEE_INFO.get(beeType).getResource(BEE_INFO.get(beeType).getBaseBlock()));
    }

    public class FindBeehiveGoal2 extends BeeEntity.FindBeehiveGoal {

        public FindBeehiveGoal2(){
            super();
        }

        @Override
        public boolean canBeeStart() {
            return hivePos != null && !detachHome() && canEnterHive() && !this.isCloseEnough(hivePos)
                    && isHiveValid();
        }
    }

    public class PollinateGoal2 extends BeeEntity.PollinateGoal {

        public PollinateGoal2(){
            super();
        }

        @Override
        public Optional<BlockPos> getFlower() {
            return this.findFlower(getFlowerPredicate(), 5.0D);
        }
    }

    //TODO Figure out how Flowers work and how to make customized
    protected final Predicate<BlockState> flowerPredicate = state ->
            state.isIn(BlockTags.TALL_FLOWERS) ? state.getBlock() != Blocks.SUNFLOWER
                    || state.get(DoublePlantBlock.HALF) == DoubleBlockHalf.UPPER : state.isIn(BlockTags.SMALL_FLOWERS);

    public Predicate<BlockState> getFlowerPredicate(){
        return flowerPredicate;
    }

    //***************************** CUSTOM BEE RELATED METHODS BELOW *************************************************

    //TODO Implement Dynamic Resource Loading - See KubeJS for Example
    protected ITextComponent func_225513_by_() {
        return new TranslationTextComponent("entity" + '.' + ResourcefulBees.MOD_ID + '.' + this.beeType);
    }

    public float[] getBeeColorAsFloat() {
        String beeColor = getBeeColor();
        Color tempColor = Color.decode(beeColor);
        return tempColor.getComponents(null);
    }

    public String getBeeColor(){

        //This check is necessary to keep the client from crashing randomly
        String tempColor = this.dataManager.get(BEE_COLOR);
        if (tempColor.isEmpty()){
            return "#FFFFFF";
        } else {
            return this.dataManager.get(BEE_COLOR);
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
        this.beeType = compound.getString("BeeType");
        if(!beeType.isEmpty()) {
            this.dataManager.set(BEE_COLOR, BEE_INFO.get(this.beeType).getColor());
        }
    }

    @Override
    public void writeAdditional(CompoundNBT compound) {
        super.writeAdditional(compound);
        compound.putString("BeeType", beeType);
    }

    //easier to manage bee selection for testing, could be useful for bee breeding
    //or other possible reasons for changing bee type.
    //if fleshed out further - may want to consider separate class for handling bee types
    private void selectRandomBee(){
        //TODO Cleanup Bee Data
        this.beeType = BEE_INFO.get(BEE_INFO.keySet().toArray()[rand.nextInt(BEE_INFO.size())]).getName();
        this.dataManager.set(BEE_COLOR, BEE_INFO.get(beeType).getColor());
    }

    public String getBeeType() {
        return beeType;
    }
}
