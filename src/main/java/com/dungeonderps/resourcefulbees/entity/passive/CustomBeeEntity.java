package com.dungeonderps.resourcefulbees.entity.passive;

import com.dungeonderps.resourcefulbees.ResourcefulBees;
import com.dungeonderps.resourcefulbees.config.BeeInfo;
import com.dungeonderps.resourcefulbees.data.BeeData;
import com.dungeonderps.resourcefulbees.entity.ICustomBee;
import com.dungeonderps.resourcefulbees.lib.BeeConstants;
import com.dungeonderps.resourcefulbees.registry.RegistryHandler;
import com.dungeonderps.resourcefulbees.utils.BeeInfoUtils;
import com.dungeonderps.resourcefulbees.utils.MathUtils;
import net.minecraft.entity.*;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.BeeEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.NameTagItem;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.tags.ITag;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.DimensionType;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Random;

@SuppressWarnings("EntityConstructor")
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
        Biome curBiome = this.world.getBiome(this.getPosition());
        String bee = fromBiome ? BeeInfo.getRandomBee(curBiome) : BeeInfo.getRandomBee();
        this.dataManager.set(BEE_TYPE, bee);
    }

    public void setBeeType(String beeType){
        this.dataManager.set(BEE_TYPE, getNameFromInfo(beeType));
    }

    public String getBeeType() {
        BeeData info = getBeeInfo(this.dataManager.get(BEE_TYPE));
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
        return getBeeInfo(beeType).getHoneycombColor();
    }

    public String getNameFromInfo(String beeType) {
        return getBeeInfo(beeType).getName();
    }

    public BeeData getBeeInfo() {
        return BeeInfo.getInfo(this.getBeeType());
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

    public BeeData getBeeInfo(String beeType) {
        return BeeInfo.getInfo(beeType);
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
    public boolean attackEntityFrom(DamageSource source, float amount) {
        if (source.isFireDamage() && (getBeeInfo().isNetherBee() || getBeeInfo().isBlazeBee()))
            return false;
        return super.attackEntityFrom(source, amount);
    }

    @Override
    public boolean isPotionApplicable(@Nonnull EffectInstance potioneffectIn) {
        if (getBeeInfo().isWitherBee() && potioneffectIn.getPotion().equals(Effects.WITHER))
            return false;
        return super.isPotionApplicable(potioneffectIn);
    }

    @Override
    public void livingTick() {
        if (remove) {
            if (!world.isRemote() && (getBeeType().equals(BeeConstants.DEFAULT_BEE_TYPE) || getBeeType().isEmpty())) {
                LOGGER.info("Removed Bee, Reason: " + (getBeeType().isEmpty() ? "Empty BeeType" : "Default Bee"));
                remove = false;
                this.dead = true;
                this.remove();
            } else
                remove = false;
        }

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
    public ILivingEntityData onInitialSpawn(@Nonnull IWorld worldIn, @Nonnull DifficultyInstance difficultyIn, SpawnReason reason, @Nullable ILivingEntityData spawnDataIn, @Nullable CompoundNBT dataTag) {
        setBeeType(reason.equals(SpawnReason.CHUNK_GENERATION) || reason.equals(SpawnReason.NATURAL));
        return super.onInitialSpawn(worldIn, difficultyIn, reason, spawnDataIn, dataTag);
    }

    public static boolean canBeeSpawn(EntityType<? extends AnimalEntity> typeIn, IWorld worldIn, SpawnReason reason, BlockPos pos, Random randomIn) {
        return worldIn.getWorld().func_234922_V_().equals(DimensionType.THE_NETHER)
                || worldIn.getWorld().func_234922_V_().equals(DimensionType.THE_END)
                || worldIn.getLight(pos) > 8;
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
        this.dataManager.set(BEE_TYPE, compound.getString(BeeConstants.NBT_BEE_TYPE));
        this.dataManager.set(FEED_COUNT, compound.getInt(BeeConstants.NBT_FEED_COUNT));
    }

    @Override
    public void writeAdditional(@Nonnull CompoundNBT compound) {
        super.writeAdditional(compound);
        compound.putString(BeeConstants.NBT_BEE_TYPE, this.getBeeType());
        compound.putInt(BeeConstants.NBT_FEED_COUNT, this.getFeedCount());
    }

    public CustomBeeEntity createSelectedChild(String beeType) {
        CustomBeeEntity childBee = new CustomBeeEntity(RegistryHandler.CUSTOM_BEE.get(), this.world);
        childBee.setBeeType(beeType);
        return childBee;
    }

    //@TODO TEST THIS!
    //This is because we don't want IF being able to breed our animals
    @Override
    public void setInLove(@Nullable PlayerEntity player) {
        if(player != null)
            super.setInLove(player);
    }

    @Override
    public boolean isBreedingItem(@Nonnull ItemStack stack) {
        String validBreedItem = this.getBeeInfo().getFeedItem();

        if (BeeInfoUtils.TAG_RESOURCE_PATTERN.matcher(validBreedItem).matches()) {
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
    public ActionResultType func_230254_b_(PlayerEntity player, @Nonnull Hand hand) {
        ItemStack itemstack = player.getHeldItem(hand);
        Item item = itemstack.getItem();
        if (item instanceof NameTagItem){
            super.func_230254_b_(player,hand);
        }
        if (this.isBreedingItem(itemstack)) {
            if (!this.world.isRemote && this.getGrowingAge() == 0 && this.canBreed()) {
                this.consumeItemFromStack(player, itemstack);
                this.addFeedCount();
                if (this.getFeedCount() >= this.getBeeInfo().getFeedAmount()) {
                    this.setInLove(player);
                }
                player.swing(hand, true);
                return ActionResultType.PASS;
            }

            if (this.isChild()) {
                this.consumeItemFromStack(player, itemstack);
                this.ageUp((int)((float)(-this.getGrowingAge() / 20) * 0.1F), true);
                return ActionResultType.PASS;
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
            return ActionResultType.PASS;
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