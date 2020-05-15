package net.minecraft.entity.passive;

import com.dungeonderps.resourcefulbees.ResourcefulBees;
import com.dungeonderps.resourcefulbees.config.BeeInfo;
import com.dungeonderps.resourcefulbees.entity.goals.BeeBreedGoal;
import com.dungeonderps.resourcefulbees.lib.BeeConst;
import com.dungeonderps.resourcefulbees.registry.RegistryHandler;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.DoublePlantBlock;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.goal.FollowParentGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.ai.goal.TemptGoal;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.NameTagItem;
import net.minecraft.item.SpawnEggItem;
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
import net.minecraft.util.DamageSource;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.village.PointOfInterest;
import net.minecraft.village.PointOfInterestManager;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;
import java.awt.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@SuppressWarnings("EntityConstructor")
public class CustomBeeEntity extends BeeEntity {

    //These are needed for Server->Client synchronization
    private static final DataParameter<String> BEE_COLOR = EntityDataManager.createKey(CustomBeeEntity.class, DataSerializers.STRING);
    private static final DataParameter<String> BEE_TYPE = EntityDataManager.createKey(CustomBeeEntity.class, DataSerializers.STRING);

    public CustomBeeEntity(EntityType<? extends BeeEntity> type, World world) {
        super(type, world);
    }

    //*************************** STANDARD BEE METHODS BELOW **********************************************************

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new BeeEntity.StingGoal(this, 1.4, true));
        this.goalSelector.addGoal(1, new BeeEntity.EnterBeehiveGoal());
        this.goalSelector.addGoal(2, new BeeBreedGoal(this, 1.0D));
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
            assert this.hivePos != null;
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
                    world.setBlockState(beePosDown, Objects.requireNonNull(ForgeRegistries.BLOCKS.getValue(BeeInfo.BEE_INFO.get(this.getBeeType()).getResource(BeeInfo.BEE_INFO.get(this.getBeeType()).getMutationBlock()))).getDefaultState());
                    addCropCounter();
                }
            }
        }
    }

    public boolean validFillerBlock(Block block){
        return Objects.equals(block.getRegistryName(), BeeInfo.BEE_INFO.get(this.getBeeType()).getResource(BeeInfo.BEE_INFO.get(this.getBeeType()).getBaseBlock()));
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

    @Override
    public boolean isFlowers(BlockPos pos) {
        String flower = BeeInfo.BEE_INFO.get(this.getBeeType()).getFlower().toLowerCase();

        switch (flower){
            case BeeConst.FLOWER_TAG_ALL:
                return this.world.isBlockPresent(pos) && this.world.getBlockState(pos).getBlock().isIn(BlockTags.FLOWERS);
            case BeeConst.FLOWER_TAG_SMALL:
                return this.world.isBlockPresent(pos) && this.world.getBlockState(pos).getBlock().isIn(BlockTags.SMALL_FLOWERS);
            case BeeConst.FLOWER_TAG_TALL:
                return this.world.isBlockPresent(pos) && this.world.getBlockState(pos).getBlock().isIn(BlockTags.TALL_FLOWERS);
            default:
                return this.world.isBlockPresent(pos) && this.world.getBlockState(pos).getBlock().equals(ForgeRegistries.BLOCKS.getValue(BeeInfo.BEE_INFO.get(this.getBeeType()).getResource(BeeInfo.BEE_INFO.get(this.getBeeType()).getFlower())));
        }
    }

    protected final Predicate<BlockState> flowerPredicate = state -> {

        String flower = BeeInfo.BEE_INFO.get(this.getBeeType()).getFlower().toLowerCase();

        switch (flower) {
            case BeeConst.FLOWER_TAG_ALL:
                return state.isIn(BlockTags.TALL_FLOWERS) ? state.getBlock() != Blocks.SUNFLOWER || state.get(DoublePlantBlock.HALF) == DoubleBlockHalf.UPPER : state.isIn(BlockTags.SMALL_FLOWERS);
            case BeeConst.FLOWER_TAG_SMALL:
                return state.isIn(BlockTags.SMALL_FLOWERS);
            case BeeConst.FLOWER_TAG_TALL:
                return state.isIn(BlockTags.TALL_FLOWERS) && (state.getBlock() != Blocks.SUNFLOWER || state.get(DoublePlantBlock.HALF) == DoubleBlockHalf.UPPER);
            default:
                return state.getBlock().equals(ForgeRegistries.BLOCKS.getValue(BeeInfo.BEE_INFO.get(this.getBeeType()).getResource(BeeInfo.BEE_INFO.get(this.getBeeType()).getFlower())));

                /*    <---- Leaving this in case there's ever new flower based tags
                if (flower.charAt(0) == '#') {
                    // do something
                } else {
                    return state.equals(ForgeRegistries.BLOCKS.getValue(BEE_INFO.get(beeType).getResource(BEE_INFO.get(beeType).getFlower())).getDefaultState());
                }
                */
        }
    };

    public Predicate<BlockState> getFlowerPredicate(){ return flowerPredicate; }

    //***************************** CUSTOM BEE RELATED METHODS BELOW *************************************************

    protected ITextComponent getProfessionName() {   //<--- Recently mapped by Darkhax
        return new TranslationTextComponent("entity" + '.' + ResourcefulBees.MOD_ID + '.' + this.getBeeType().toLowerCase() + "_bee");
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
            return String.valueOf(BeeConst.DEFAULT_COLOR);
        } else {
            return this.dataManager.get(BEE_COLOR);
        }
    }

//    Entity queenBeePlayer = null;

    @Override
    public void tick() {
        if (this.hasNectar() && BeeInfo.BEE_INFO.get(this.getBeeType()).isNetherBee()){
            this.setFire(1);
        }
//        if (this.getDisplayName().getFormattedText().toLowerCase().equals("queen bee")){
//            if (queenBeePlayer !=null) {
//                this.setBeeAttacker(queenBeePlayer);
//                this.setHasStung(false);
//            }
//        }
        super.tick();
    }

    @Override
    public boolean attackEntityFrom(DamageSource source, float amount) {
        if (source.isFireDamage() && BeeInfo.BEE_INFO.get(this.getBeeType()).isNetherBee())
            return false;
        return super.attackEntityFrom(source, amount);
    }

    //REMOVE Reason Stuff AND Create Data Parameter for BeeName
    @Nullable
    @Override
    public ILivingEntityData onInitialSpawn(IWorld worldIn, DifficultyInstance difficultyIn, SpawnReason reason, @Nullable ILivingEntityData spawnDataIn, @Nullable CompoundNBT dataTag) {
        selectRandomBee(reason.equals(SpawnReason.CHUNK_GENERATION) || reason.equals(SpawnReason.NATURAL));
        return super.onInitialSpawn(worldIn, difficultyIn, reason, spawnDataIn, dataTag);
    }

    public static boolean canBeeSpawn(EntityType<? extends AnimalEntity> typeIn, IWorld worldIn, SpawnReason reason, BlockPos pos, Random randomIn) {
        return worldIn.getLightSubtracted(pos, 0) > 8;
    }

    @Override
    protected void registerData() {
        super.registerData();
        //Need to supply default value
        this.dataManager.register(BEE_COLOR, String.valueOf(BeeConst.DEFAULT_COLOR));
        this.dataManager.register(BEE_TYPE, BeeConst.DEFAULT_BEE_TYPE);
    }

    @Override
    public void readAdditional(CompoundNBT compound) {
        super.readAdditional(compound);
        this.dataManager.set(BEE_TYPE, compound.getString(BeeConst.NBT_BEE_TYPE));
        if(!this.getBeeType().isEmpty()) {
            this.dataManager.set(BEE_COLOR, BeeInfo.BEE_INFO.get(this.getBeeType()).getColor());
        }
    }

    @Override
    public void writeAdditional(CompoundNBT compound) {
        super.writeAdditional(compound);
        compound.putString(BeeConst.NBT_BEE_TYPE, this.getBeeType());
    }

    //easier to manage bee selection for testing, could be useful for bee breeding
    //or other possible reasons for changing bee type.
    //if fleshed out further - may want to consider separate class for handling bee types
    private void selectRandomBee(boolean fromBiome){
        if (fromBiome) {
            Biome curBiome = this.world.getBiome(this.getPosition());
            ArrayList<String> spawnList = new ArrayList<>(BeeInfo.SPAWNABLE_BIOMES.get(curBiome));
            this.dataManager.set(BEE_TYPE, BeeInfo.BEE_INFO.get(spawnList.get(rand.nextInt(spawnList.size()))).getName());
        } else {
            this.dataManager.set(BEE_TYPE, BeeInfo.BEE_INFO.get(BeeInfo.BEE_INFO.keySet().toArray()[rand.nextInt(BeeInfo.BEE_INFO.size() - 1) +1]).getName());
        }
        this.dataManager.set(BEE_COLOR, BeeInfo.BEE_INFO.get(getBeeType()).getColor());
    }

    public void selectBeeType(String beeType){
        this.dataManager.set(BEE_TYPE, BeeInfo.BEE_INFO.get(beeType).getName());
        this.dataManager.set(BEE_COLOR, BeeInfo.BEE_INFO.get(beeType).getColor());
    }

    public String getBeeType() {
        BeeInfo info = BeeInfo.BEE_INFO.get(this.dataManager.get(BEE_TYPE));
        if (info != null) {
            return this.dataManager.get(BEE_TYPE);
        } else {
            if (!world.isRemote())
                this.remove();

            LOGGER.info("Tried to Remove Default bee here: " + this.getPosition().toString());
            return BeeConst.DEFAULT_BEE_TYPE;
        }
    }

    public CustomBeeEntity createSelectedChild(String beeType) {
        CustomBeeEntity childBee = new CustomBeeEntity(RegistryHandler.CUSTOM_BEE.get(), this.world);
        childBee.selectBeeType(beeType);
        return childBee;
    }

    @Override
    public boolean processInteract(PlayerEntity player, Hand hand) {
        ItemStack itemstack = player.getHeldItem(hand);
        Item item = itemstack.getItem();
        if (item instanceof NameTagItem){
//            LOGGER.info("queen be player is: " + player.getDisplayName().getFormattedText());
//            queenBeePlayer = player;
            super.processInteract(player,hand);
        }
        if (item == RegistryHandler.SMOKER.get() && this.isAngry()) {
            smokeBee();
            itemstack.damageItem(1, player, player1 -> player1.sendBreakAnimation(hand));
        }
        if (this.isBreedingItem(itemstack)) {
            if (!this.world.isRemote && this.getGrowingAge() == 0 && this.canBreed()) {
                this.consumeItemFromStack(player, itemstack);
                this.setInLove(player);
                player.swing(hand, true);
                return true;
            }

            if (this.isChild()) {
                this.consumeItemFromStack(player, itemstack);
                this.ageUp((int)((float)(-this.getGrowingAge() / 20) * 0.1F), true);
                return true;
            }
        }
        if (item instanceof SpawnEggItem && ((SpawnEggItem)item).hasType(itemstack.getTag(), this.getType())) {
            if (!this.world.isRemote) {
                AgeableEntity ageableentity = this.createSelectedChild(this.getBeeType());
                if (ageableentity != null) {
                    ageableentity.setGrowingAge(-24000);
                    ageableentity.setLocationAndAngles(this.getPosX(), this.getPosY(), this.getPosZ(), 0.0F, 0.0F);
                    this.world.addEntity(ageableentity);
                    if (itemstack.hasDisplayName()) {
                        ageableentity.setCustomName(itemstack.getDisplayName());
                    }

                    this.onChildSpawnFromEgg(player, ageableentity);
                    if (!player.abilities.isCreativeMode) {
                        itemstack.shrink(1);
                    }
                }
            }

            return true;
        } else {
            return false;
        }
    }

    private void smokeBee(){
        //we're using reflection to access private setAnger method for bee
        //It's looks scary but it's simple.
        //create new method, get method from class we want to call,
        //call method, pass in the object we want to call the method on
        //pass in the value for the parameter.

        Method setAnger = null;   /// <<<<----- creating method container
        try {
            setAnger = BeeEntity.class.getDeclaredMethod("setAnger", int.class); ///<<<<------- Creating instance of method
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        setAnger.setAccessible(true);     ///<<<<------- Making the method accessible
        try {
            setAnger.invoke(this, 0);   ///<<<<------ Invoking method
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }
}