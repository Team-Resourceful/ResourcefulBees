package net.minecraft.entity.passive;

import com.dungeonderps.resourcefulbees.ResourcefulBees;
import com.dungeonderps.resourcefulbees.config.BeeInfo;
import com.dungeonderps.resourcefulbees.data.BeeData;
import com.dungeonderps.resourcefulbees.entity.ICustomBee;
import com.dungeonderps.resourcefulbees.entity.goals.BeeBreedGoal;
import com.dungeonderps.resourcefulbees.lib.BeeConst;
import com.dungeonderps.resourcefulbees.registry.RegistryHandler;
import com.dungeonderps.resourcefulbees.utils.BeeInfoUtils;
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
import net.minecraft.particles.ParticleTypes;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.state.properties.DoubleBlockHalf;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tileentity.IronBeehiveBlockEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.village.PointOfInterest;
import net.minecraft.village.PointOfInterestManager;
import net.minecraft.world.*;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.EnderTeleportEvent;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@SuppressWarnings("EntityConstructor")
public class CustomBeeEntity extends BeeEntity implements ICustomBee {

    //These are needed for Server->Client synchronization
    private static final DataParameter<String> BEE_COLOR = EntityDataManager.createKey(CustomBeeEntity.class, DataSerializers.STRING);
    private static final DataParameter<String> BEE_TYPE = EntityDataManager.createKey(CustomBeeEntity.class, DataSerializers.STRING);
    private int targetChangeTime;

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
                    world.setBlockState(beePosDown, ForgeRegistries.BLOCKS.getValue(BeeInfoUtils.getResource(getBeeInfo().getMutationBlock())).getDefaultState());
                    addCropCounter();
                }
            }
        }
    }

    public boolean validFillerBlock(Block block){
        return Objects.equals(block.getRegistryName(), BeeInfoUtils.getResource(getBeeInfo().getBaseBlock()));
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
        String flower = getBeeInfo().getFlower().toLowerCase();

        switch (flower){
            case BeeConst.FLOWER_TAG_ALL:
                return this.world.isBlockPresent(pos) && this.world.getBlockState(pos).getBlock().isIn(BlockTags.FLOWERS);
            case BeeConst.FLOWER_TAG_SMALL:
                return this.world.isBlockPresent(pos) && this.world.getBlockState(pos).getBlock().isIn(BlockTags.SMALL_FLOWERS);
            case BeeConst.FLOWER_TAG_TALL:
                return this.world.isBlockPresent(pos) && this.world.getBlockState(pos).getBlock().isIn(BlockTags.TALL_FLOWERS);
            default:
                return this.world.isBlockPresent(pos) && this.world.getBlockState(pos).getBlock().equals(ForgeRegistries.BLOCKS.getValue(BeeInfoUtils.getResource(getBeeInfo().getFlower())));
        }
    }

    protected final Predicate<BlockState> flowerPredicate = state -> {

        String flower = getBeeInfo().getFlower().toLowerCase();

        switch (flower) {
            case BeeConst.FLOWER_TAG_ALL:
                return state.isIn(BlockTags.TALL_FLOWERS) ? state.getBlock() != Blocks.SUNFLOWER || state.get(DoublePlantBlock.HALF) == DoubleBlockHalf.UPPER : state.isIn(BlockTags.SMALL_FLOWERS);
            case BeeConst.FLOWER_TAG_SMALL:
                return state.isIn(BlockTags.SMALL_FLOWERS);
            case BeeConst.FLOWER_TAG_TALL:
                return state.isIn(BlockTags.TALL_FLOWERS) && (state.getBlock() != Blocks.SUNFLOWER || state.get(DoublePlantBlock.HALF) == DoubleBlockHalf.UPPER);
            default:
                return state.getBlock().equals(ForgeRegistries.BLOCKS.getValue(BeeInfoUtils.getResource(getBeeInfo().getFlower())));

                /*    <---- Leaving this in case there's ever new flower based tags
                if (flower.charAt(0) == '#') {
                    // do something
                } else {
                    return state.equals(ForgeRegistries.BLOCKS.getValue(getBeeInfo(beeType).getResource(getBeeInfo(beeType).getFlower())).getDefaultState());
                }
                */
        }
    };

    public Predicate<BlockState> getFlowerPredicate(){ return flowerPredicate; }

    //**************************** BEE INFO RELATED METHODS BELOW *******************************************

    public String getBeeColor(){

        //This check is necessary to keep the client from crashing randomly
        String tempColor = this.dataManager.get(BEE_COLOR);
        if (tempColor.isEmpty()){
            return String.valueOf(BeeConst.DEFAULT_COLOR);
        } else {
            return tempColor;
        }
    }

    public void setBeeType(boolean fromBiome){
        Biome curBiome = this.world.getBiome(this.getPosition());
        String bee = fromBiome ? BeeInfo.getRandomBee(curBiome) : BeeInfo.getRandomBee();
        this.dataManager.set(BEE_TYPE, bee);
        this.dataManager.set(BEE_COLOR, getColorFromInfo(getBeeType()));
    }

    public void setBeeType(String beeType){
        this.dataManager.set(BEE_TYPE, getNameFromInfo(beeType));
        this.dataManager.set(BEE_COLOR, getColorFromInfo(beeType));
    }

    public String getBeeType() {

        if(getBeeInfo(this.dataManager.get(BEE_TYPE)) != null){
            BeeData info = getBeeInfo(this.dataManager.get(BEE_TYPE));

            if (info.getName().equals(BeeConst.DEFAULT_REMOVE)) {
                if (!world.isRemote()) {
                    this.dead = true;
                    this.remove();
                }
            } else if (info.getName().equals(BeeConst.DEFAULT_BEE_TYPE)) {
                this.dataManager.set(BEE_TYPE, BeeConst.DEFAULT_REMOVE);
                return BeeConst.DEFAULT_BEE_TYPE;
            }
            return this.dataManager.get(BEE_TYPE);
        }
        return BeeConst.DEFAULT_BEE_TYPE;
    }

    public String getColorFromInfo(String beeType) {
        return getBeeInfo(beeType).getColor();
    }

    public String getNameFromInfo(String beeType) {
        return getBeeInfo(beeType).getName();
    }

    public BeeData getBeeInfo() {
        return BeeInfo.getInfo(this.getBeeType());
    }

    public BeeData getBeeInfo(String beeType) {
        return BeeInfo.getInfo(beeType);
    }

    //***************************** CUSTOM BEE RELATED METHODS BELOW *************************************************

    protected ITextComponent getProfessionName() {
        return new TranslationTextComponent("entity" + '.' + ResourcefulBees.MOD_ID + '.' + this.getBeeType().toLowerCase() + "_bee");
    }

//    Entity queenBeePlayer = null;

    @Override
    public void tick() {

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
        if (source.isFireDamage() && getBeeInfo().isNetherBee())
            return false;
        return super.attackEntityFrom(source, amount);
    }

    @Override
    public void livingTick() {
        if (this.world.isRemote && getBeeInfo().isEnderBee()){
            for(int i = 0; i < 2; ++i) {
                this.world.addParticle(ParticleTypes.PORTAL, this.getPosXRandom(0.5D),
                        this.getPosYRandom() - 0.25D, this.getPosZRandom(0.5D),
                        (this.rand.nextDouble() - 0.5D) * 2.0D, -this.rand.nextDouble(),
                        (this.rand.nextDouble() - 0.5D) * 2.0D);
            }
        }
        super.livingTick();
    }

    @Nullable
    @Override
    public ILivingEntityData onInitialSpawn(IWorld worldIn, DifficultyInstance difficultyIn, SpawnReason reason, @Nullable ILivingEntityData spawnDataIn, @Nullable CompoundNBT dataTag) {
        setBeeType(reason.equals(SpawnReason.CHUNK_GENERATION) || reason.equals(SpawnReason.NATURAL));
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
            this.dataManager.set(BEE_COLOR, getColorFromInfo(this.getBeeType()));
        }
    }

    @Override
    public void writeAdditional(CompoundNBT compound) {
        super.writeAdditional(compound);
        compound.putString(BeeConst.NBT_BEE_TYPE, this.getBeeType());
    }

    public CustomBeeEntity createSelectedChild(String beeType) {
        CustomBeeEntity childBee = new CustomBeeEntity(RegistryHandler.CUSTOM_BEE.get(), this.world);
        childBee.setBeeType(beeType);
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

            return true;
        }
        return false;
    }


    //************** CUSTOM ENTITY TASKS/GOALS ***********************************************

    protected void updateAITasks() {
        if (getBeeInfo().isEnderBee()) {
            if (this.world.isDaytime() && this.ticksExisted %150 == 0) {
                this.teleportRandomly();
            }
        }
        if (getBeeInfo().isNetherBee()){
            if (this.ticksExisted %150 == 0)
            this.setFire(3);
        }
        super.updateAITasks();
    }

    /**
     * Teleport the enderman to a random nearby position
     */
    protected boolean teleportRandomly() {
        if (!this.world.isRemote() && this.isAlive()) {
            double d0 = this.getPosX() + (this.rand.nextDouble() - 0.5D) * 4.0D;
            double d1 = this.getPosY() + (double)(this.rand.nextInt(4) - 2);
            double d2 = this.getPosZ() + (this.rand.nextDouble() - 0.5D) * 4.0D;
            return this.teleportTo(d0, d1, d2);
        } else {
            return false;
        }
    }

    /**
     * Teleport the enderman
     */
    private boolean teleportTo(double x, double y, double z) {
        BlockPos.Mutable blockpos$mutable = new BlockPos.Mutable(x, y, z);

        while(blockpos$mutable.getY() > 0 && !this.world.getBlockState(blockpos$mutable).getMaterial().blocksMovement()) {
            blockpos$mutable.move(Direction.DOWN);
        }

        BlockState blockstate = this.world.getBlockState(blockpos$mutable);
        boolean canMove = blockstate.getMaterial().blocksMovement();
        boolean water = blockstate.getFluidState().isTagged(FluidTags.WATER);
        if (canMove && !water) {
            EnderTeleportEvent event = new EnderTeleportEvent(this, x, y, z, 0);
            if (MinecraftForge.EVENT_BUS.post(event)) return false;
            boolean teleported = this.attemptTeleport(event.getTargetX(), event.getTargetY(), event.getTargetZ(), true);
            if (teleported) {
                this.world.playSound(null, this.prevPosX, this.prevPosY, this.prevPosZ, SoundEvents.ENTITY_ENDERMAN_TELEPORT, this.getSoundCategory(), 1.0F, 1.0F);
                this.playSound(SoundEvents.ENTITY_ENDERMAN_TELEPORT, 1.0F, 1.0F);
            }
            return teleported;
        }
        return false;
    }

    @Override
    public boolean attackEntityAsMob(Entity entityIn) {
        boolean flag = entityIn.attackEntityFrom(DamageSource.func_226252_a_(this), (float)((int)this.getAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).getValue()));
        if (flag) {
            this.applyEnchantments(this, entityIn);
            if (entityIn instanceof LivingEntity) {
                ((LivingEntity)entityIn).setBeeStingCount(((LivingEntity)entityIn).getBeeStingCount() + 1);
                int i = 0;
                if (this.world.getDifficulty() == Difficulty.NORMAL) {
                    i = 10;
                } else if (this.world.getDifficulty() == Difficulty.HARD) {
                    i = 18;
                }
                BeeData info = this.getBeeInfo();

                if (info.isCreeperBee()) {
                    this.explode();
                } else if (info.isNetherBee()){
                    entityIn.setFire(5);
                } else if (i > 0) {
                    if (info.isWitherBee())
                        ((LivingEntity) entityIn).addPotionEffect(new EffectInstance(Effects.WITHER, i * 20, 1));
                    else if (info.isZomBee())
                        ((LivingEntity) entityIn).addPotionEffect(new EffectInstance(Effects.HUNGER, i * 20, 20));
                    else if (info.isPigmanBee())
                        ((LivingEntity) entityIn).addPotionEffect(new EffectInstance(Effects.MINING_FATIGUE, i * 20, 0));
                    else
                        ((LivingEntity) entityIn).addPotionEffect(new EffectInstance(Effects.POISON, i * 20, 0));

                }
            }

            this.setHasStung(true);
            this.setAttackTarget(null);
            this.playSound(SoundEvents.ENTITY_BEE_STING, 1.0F, 1.0F);
        }

        return flag;
    }

    /**
     * Creates an explosion as determined by this creeper's power and explosion radius.
     */
    private void explode() {
        if (!this.world.isRemote) {
            Explosion.Mode explosion$mode = net.minecraftforge.event.ForgeEventFactory.getMobGriefingEvent(this.world, this) ? Explosion.Mode.BREAK : Explosion.Mode.NONE;
            this.dead = true;
            this.world.createExplosion(this, this.getPosX(), this.getPosY(), this.getPosZ(), rand.nextFloat() * 3, explosion$mode);
            this.remove();
            this.spawnLingeringCloud();
        }

    }

    private void spawnLingeringCloud() {
        Collection<EffectInstance> collection = this.getActivePotionEffects();
        if (!collection.isEmpty()) {
            AreaEffectCloudEntity areaeffectcloudentity = new AreaEffectCloudEntity(this.world, this.getPosX(), this.getPosY(), this.getPosZ());
            areaeffectcloudentity.setRadius(2.5F);
            areaeffectcloudentity.setRadiusOnUse(-0.5F);
            areaeffectcloudentity.setWaitTime(10);
            areaeffectcloudentity.setDuration(areaeffectcloudentity.getDuration() / 2);
            areaeffectcloudentity.setRadiusPerTick(-areaeffectcloudentity.getRadius() / (float)areaeffectcloudentity.getDuration());

            for(EffectInstance effectinstance : collection) {
                areaeffectcloudentity.addEffect(new EffectInstance(effectinstance));
            }

            this.world.addEntity(areaeffectcloudentity);
        }

    }

}