package com.resourcefulbees.resourcefulbees.entity.passive;

import com.resourcefulbees.resourcefulbees.ResourcefulBees;
import com.resourcefulbees.resourcefulbees.api.CustomBee;
import com.resourcefulbees.resourcefulbees.data.BeeTrait;
import com.resourcefulbees.resourcefulbees.entity.ICustomBee;
import com.resourcefulbees.resourcefulbees.lib.BeeConstants;
import com.resourcefulbees.resourcefulbees.lib.NBTConstants;
import com.resourcefulbees.resourcefulbees.registry.BeeRegistry;
import com.resourcefulbees.resourcefulbees.registry.RegistryHandler;
import com.resourcefulbees.resourcefulbees.utils.BeeInfoUtils;
import com.resourcefulbees.resourcefulbees.utils.BeeValidator;
import com.resourcefulbees.resourcefulbees.utils.MathUtils;
import net.minecraft.entity.*;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.BeeEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.NameTagItem;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.particles.BasicParticleType;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;
import net.minecraft.tags.ITag;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.IServerWorld;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.common.util.FakePlayer;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Random;

public class CustomBeeEntity extends BeeEntity implements ICustomBee {

    private static final DataParameter<String> BEE_TYPE = EntityDataManager.createKey(CustomBeeEntity.class, DataSerializers.STRING);
    private static final DataParameter<Integer> FEED_COUNT = EntityDataManager.createKey(CustomBeeEntity.class, DataSerializers.VARINT);

    private boolean remove;
    private boolean renderingInJei;

    public CustomBeeEntity(EntityType<? extends BeeEntity> type, World world) {
        super(type, world);
    }

    //region BEE INFO RELATED METHODS BELOW

    public void setBeeType(boolean fromBiome){
        Biome curBiome = this.world.getBiome(this.getBlockPos());
        String bee = fromBiome ? BeeRegistry.getRandomBee(curBiome) : BeeRegistry.getRandomBee();
        this.dataManager.set(BEE_TYPE, bee);
    }

    public void setBeeType(String beeType){
        this.dataManager.set(BEE_TYPE, getNameFromInfo(beeType));
    }

    public String getBeeType() {
        CustomBee info = getBeeInfo(this.dataManager.get(BEE_TYPE));
        if (info.getName().equals(BeeConstants.DEFAULT_BEE_TYPE) || info.getName().isEmpty()) {
            markRemove();
        }
        return this.dataManager.get(BEE_TYPE);
    }

    @Override
    public Float getSizeModifierFromInfo(String beeType) {
        return MathUtils.clamp(getBeeInfo(getBeeType()).getSizeModifier(), 0.5f, 2f);
    }

    public String getColorFromInfo(String beeType) {
        return getBeeInfo(beeType).ColorData.getHoneycombColor();
    }

    public String getNameFromInfo(String beeType) {
        return getBeeInfo(beeType).getName();
    }

    public CustomBee getBeeInfo() {
        return BeeRegistry.getInfo(this.getBeeType());
    }

    @Override
    public int getFeedCount() {
        return this.dataManager.get(FEED_COUNT);
    }

    @Override
    public void resetFeedCount() {
        this.dataManager.set(FEED_COUNT, 0);
    }

    @Override
    public void addFeedCount() {
        this.dataManager.set(FEED_COUNT, this.getFeedCount() + 1);
    }

    public CustomBee getBeeInfo(String beeType) {
        return BeeRegistry.getInfo(beeType);
    }
    //endregion

    //region JEI RELATED METHODS
    public void setRenderingInJei(boolean inJei){
            this.renderingInJei = inJei;
        }

    public boolean getRenderingInJei(){
            return this.renderingInJei;
        }
    //endregion

    //region CUSTOM BEE RELATED METHODS BELOW

    @Nonnull
    protected ITextComponent getProfessionName() {
        return new TranslationTextComponent("entity" + '.' + ResourcefulBees.MOD_ID + '.' + this.getBeeType() + "_bee");
    }

    @Override
    public boolean isInvulnerableTo(@Nonnull DamageSource source) {
        CustomBee info = getBeeInfo();
        if (source.equals(DamageSource.SWEET_BERRY_BUSH)) {
            return true;  //All bees should be immune to this like vanilla - Not sure why it doesn't carry over from vanilla bees
        }
        if (info.TraitData.hasDamageImmunities()){
            for (DamageSource damage : info.TraitData.getDamageImmunities())
                if (source.equals(damage)) return true;
        }
        return super.isInvulnerableTo(source);
    }

    @Override
    public boolean isPotionApplicable(@Nonnull EffectInstance potioneffectIn) {
        CustomBee info = getBeeInfo();
        if (info.TraitData.hasPotionImmunities()){
            for (Effect potion : info.TraitData.getPotionImmunities()){
                if (potion.equals(potioneffectIn.getPotion())) return false;
            }
        }
        return super.isPotionApplicable(potioneffectIn);
    }

    @Override
    public void livingTick() {
        if (remove) {
            if (!world.isRemote()) {
                String beeName = getBeeInfo().getName();
                if (beeName.equals(BeeConstants.DEFAULT_BEE_TYPE) || beeName.isEmpty()) {
                    LOGGER.info("Removed Bee, Reason: " + (beeName.isEmpty() ? "Empty BeeType" : "Default Bee"));
                    remove = false;
                    this.dead = true;
                    this.remove();
                }
            } else
                remove = false;
        }


        if (this.world.isRemote){
            if (this.ticksExisted % 40 == 0) {
                CustomBee info = getBeeInfo();
                if (info.TraitData.hasParticleEffects()){
                    for (BasicParticleType particle : info.TraitData.getParticleEffects()){
                        for (int i = 0; i < 10; ++i) {
                            this.world.addParticle(particle, this.getParticleX(0.5D),
                                    this.getRandomBodyY() - 0.25D, this.getParticleZ(0.5D),
                                    (this.rand.nextDouble() - 0.5D) * 2.0D, -this.rand.nextDouble(),
                                    (this.rand.nextDouble() - 0.5D) * 2.0D);
                        }
                    }
                }
            }
        }
        super.livingTick();
    }

    @Nullable
    @Override
    public ILivingEntityData onInitialSpawn(@Nonnull IServerWorld worldIn, @Nonnull DifficultyInstance difficultyIn, SpawnReason reason, @Nullable ILivingEntityData spawnDataIn, @Nullable CompoundNBT dataTag) {
        setBeeType(reason.equals(SpawnReason.CHUNK_GENERATION) || reason.equals(SpawnReason.NATURAL));
        return super.onInitialSpawn(worldIn, difficultyIn, reason, spawnDataIn, dataTag);
    }

    @SuppressWarnings("unused")
    public static boolean canBeeSpawn(EntityType<? extends AnimalEntity> typeIn, IWorld worldIn, SpawnReason reason, BlockPos pos, Random randomIn) {
        return false; //TODO fix when forge updates biome stuff

                /*worldIn.getWorld().func_234922_V_().equals(DimensionType.THE_NETHER)
                || worldIn.getWorld().func_234922_V_().equals(DimensionType.THE_END)
                || worldIn.getLight(pos) > 8;*/
    }

    @Override
    protected void registerData() {
        super.registerData();
        this.dataManager.register(BEE_TYPE, BeeConstants.DEFAULT_BEE_TYPE);
        this.dataManager.register(FEED_COUNT, 0);
    }

    @Override
    public void readAdditional(@Nonnull CompoundNBT compound) {
        super.readAdditional(compound);
        this.dataManager.set(BEE_TYPE, compound.getString(NBTConstants.NBT_BEE_TYPE));
        this.dataManager.set(FEED_COUNT, compound.getInt(NBTConstants.NBT_FEED_COUNT));
    }

    @Override
    public void writeAdditional(@Nonnull CompoundNBT compound) {
        super.writeAdditional(compound);
        compound.putString(NBTConstants.NBT_BEE_TYPE, this.getBeeType());
        compound.putInt(NBTConstants.NBT_FEED_COUNT, this.getFeedCount());
    }

    public CustomBeeEntity createSelectedChild(String beeType) {
        CustomBeeEntity childBee = new CustomBeeEntity(RegistryHandler.CUSTOM_BEE.get(), this.world);
        childBee.setBeeType(beeType);
        return childBee;
    }

    //This is because we don't want IF being able to breed our animals
    @Override
    public void setInLove(@Nullable PlayerEntity player) {
        if(player != null && !(player instanceof FakePlayer))
            super.setInLove(player);
    }

    @Override
    public void resetInLove() {
        super.resetInLove();
        resetFeedCount();
    }

    @Override
    public boolean isBreedingItem(@Nonnull ItemStack stack) {
        String validBreedItem = this.getBeeInfo().BreedData.getFeedItem();

        if (BeeValidator.TAG_RESOURCE_PATTERN.matcher(validBreedItem).matches()) {
            ITag<Item> itemTag = BeeInfoUtils.getItemTag(validBreedItem.replace(BeeConstants.TAG_PREFIX, ""));
            return itemTag != null && stack.getItem().isIn(itemTag);
        } else {
            switch (validBreedItem) {
                case BeeConstants.FLOWER_TAG_ALL:
                    return stack.getItem().isIn(ItemTags.FLOWERS);
                case BeeConstants.FLOWER_TAG_SMALL:
                    return stack.getItem().isIn(ItemTags.SMALL_FLOWERS);
                case BeeConstants.FLOWER_TAG_TALL:
                    return stack.getItem().isIn(ItemTags.TALL_FLOWERS);
                default:
                    return stack.getItem().equals(BeeInfoUtils.getItem(validBreedItem));
            }
        }
    }



    @Nonnull
    @Override
    public ActionResultType interactMob(PlayerEntity player, @Nonnull Hand hand) {
        ItemStack itemstack = player.getHeldItem(hand);
        Item item = itemstack.getItem();
        if (item instanceof NameTagItem){
            super.interactMob(player,hand);
        }
        if (this.isBreedingItem(itemstack)) {
            if (!this.world.isRemote && this.getGrowingAge() == 0 && this.canBreed()) {
                this.consumeItemFromStack(player, itemstack);
                this.addFeedCount();
                if (this.getFeedCount() >= this.getBeeInfo().BreedData.getFeedAmount()) {
                    this.setInLove(player);
                }
                player.swingHand(hand, true);
                return ActionResultType.PASS;
            }

            if (this.isChild()) {
                this.consumeItemFromStack(player, itemstack);
                this.ageUp((int)((float)(-this.getGrowingAge() / 20) * 0.1F), true);
                return ActionResultType.PASS;
            }
        }
        return ActionResultType.FAIL;
    }

    @Nonnull
    @Override
    public EntitySize getSize(@Nonnull Pose poseIn) {
        float scale = getSizeModifierFromInfo(getBeeType());
        return super.getSize(poseIn).scale(scale);
    }

    @Override
    public void notifyDataManagerChange(@Nonnull DataParameter<?> parameter) {
        if (parameter.equals(BEE_TYPE))
            recalculateSize();
        super.notifyDataManagerChange(parameter);
    }

    private void markRemove() {
        if (!remove) {
            this.remove = true;
        }
    }
    //endregion
}