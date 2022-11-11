package com.teamresourceful.resourcefulbees.common.block;

import com.teamresourceful.resourcefulbees.common.blockentity.TieredBeehiveBlockEntity;
import com.teamresourceful.resourcefulbees.common.compat.base.ModCompatHelper;
import com.teamresourceful.resourcefulbees.common.config.CommonConfig;
import com.teamresourceful.resourcefulbees.common.item.IShiftingToolTip;
import com.teamresourceful.resourcefulbees.common.item.upgrade.UpgradeType;
import com.teamresourceful.resourcefulbees.common.item.upgrade.nestupgrade.INestUpgrade;
import com.teamresourceful.resourcefulbees.common.lib.constants.ModConstants;
import com.teamresourceful.resourcefulbees.common.lib.constants.NBTConstants;
import com.teamresourceful.resourcefulbees.common.lib.constants.TranslationConstants;
import com.teamresourceful.resourcefulbees.common.lib.enums.BeehiveTier;
import it.unimi.dsi.fastutil.ints.IntDoublePair;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.animal.Bee;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BeehiveBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BeehiveBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.ToolActions;
import net.minecraftforge.common.util.FakePlayer;
import org.apache.commons.lang3.text.WordUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.function.Supplier;

public class TieredBeehiveBlock extends BeehiveBlock implements IShiftingToolTip {

    private static final MutableComponent NONE_TEXT = Component.literal("     NONE");
    //public static final IntegerProperty TIER_PROPERTY = IntegerProperty.create("tier", 1, 4);

    private final Supplier<BlockEntityType<TieredBeehiveBlockEntity>> entityType;
    private final BeehiveTier tier;

    public TieredBeehiveBlock(Supplier<BlockEntityType<TieredBeehiveBlockEntity>> entityType, BeehiveTier tier, Properties properties) {
        super(properties);
        this.entityType = entityType;
        this.tier = tier;
        this.registerDefaultState(this.stateDefinition.any().setValue(HONEY_LEVEL, 0).setValue(FACING, Direction.NORTH));//.setValue(TIER_PROPERTY, tier.ordinal()));
    }

    public BeehiveTier getTier() { return tier; }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(@NotNull Level level, @NotNull BlockState state, @NotNull BlockEntityType<T> blockEntityType) {
        return level.isClientSide ? null : createTickerHelper(blockEntityType, entityType.get(), TieredBeehiveBlockEntity::serverSideTick);
    }

    /**
     * Static method for use with Dispenser logic
     *
     * @param block      the block
     * @param level      the level
     * @param pos        the blocks position
     * @param useScraper set to true if scraper logic should run
     * @return returns true if the hive has been emptied
     */
    public static boolean dropResourceHoneycomb(TieredBeehiveBlock block, Level level, BlockPos pos, boolean useScraper) {
        return block.dropResourceHoneycomb(level, pos, null, useScraper);
    }



    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        Direction direction = context.getPlayer() != null && context.getPlayer().isShiftKeyDown() ? context.getHorizontalDirection() : context.getHorizontalDirection().getOpposite();
        return this.defaultBlockState().setValue(FACING, direction);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
        return new TieredBeehiveBlockEntity(entityType, pos, state);
    }

    public boolean isHiveSmoked(BlockPos pos, Level level) {
        return level.getBlockEntity(pos) instanceof TieredBeehiveBlockEntity hive && hive.isSedated();
    }

    @Override
    public @NotNull InteractionResult use(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos, Player player, @NotNull InteractionHand handIn, @NotNull BlockHitResult hit) {
        ItemStack itemstack = player.getItemInHand(handIn);

        if (state.getValue(HONEY_LEVEL) >= 5) {
            boolean isShear = CommonConfig.ALLOW_SHEARS.get() && itemstack.canPerformAction(ToolActions.SHEARS_HARVEST);
            boolean isScraper = itemstack.canPerformAction(ModConstants.SCRAPE_HIVE);

            if (isShear || isScraper) {
                InteractionResult success = performHoneyHarvest(state, level, pos, player, handIn, itemstack, isScraper);
                if (success != null) return success;
            }
        }

        if (itemstack.getItem() instanceof INestUpgrade upgrade && upgrade.getUpgradeType().equals(UpgradeType.NEST)) {
            if (upgrade.getTier().from.equals(this.tier)) return upgrade.getTier().upgrader.performUpgrade(state, level, pos, itemstack);
            else {
                player.displayClientMessage(Component.literal("You can not upgrade this nest with that upgrade."), true);
            }
        }

        return InteractionResult.PASS;
    }

    @Nullable
    private InteractionResult performHoneyHarvest(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos, Player player, @NotNull InteractionHand handIn, ItemStack itemstack, boolean isScraper) {
        level.playSound(player, player.getX(), player.getY(), player.getZ(), SoundEvents.BEEHIVE_SHEAR, SoundSource.NEUTRAL, 1.0F, 1.0F);
        dropResourceHoneycomb(level, pos, player, isScraper);
        itemstack.hurtAndBreak(1, player, player1 -> player1.broadcastBreakEvent(handIn));

        if (level.getBlockEntity(pos) instanceof TieredBeehiveBlockEntity beehiveTileEntity && !beehiveTileEntity.hasCombs()) {
            if (isHiveSmoked(pos, level)) {
                this.resetHoneyLevel(level, state, pos);
            } else {
                if (beehiveTileEntity.hasBees() && !(player instanceof FakePlayer) && player instanceof ServerPlayer serverPlayer && !ModCompatHelper.shouldNotAngerBees(serverPlayer)) {
                    this.angerBeesNearby(level, pos);
                }
                this.releaseBeesAndResetHoneyLevel(level, state, pos, player, BeehiveBlockEntity.BeeReleaseStatus.EMERGENCY);
            }
            return InteractionResult.SUCCESS;
        }
        return null;
    }

    private void angerBeesNearby(Level level, BlockPos pos) {
        AABB aabb = new AABB(pos).inflate(8.0D, 6.0D, 8.0D);
        List<Bee> beeEntityList = level.getEntitiesOfClass(Bee.class, aabb);
        if (!beeEntityList.isEmpty()) {
            List<Player> nearbyPlayers = level.getEntitiesOfClass(Player.class, aabb);

            if (!nearbyPlayers.isEmpty()) {
                beeEntityList.stream()
                    .filter(beeEntity -> beeEntity.getTarget() == null)
                    .forEach(beeEntity -> {
                        Player randomPlayer = nearbyPlayers.get(level.random.nextInt(nearbyPlayers.size()));
                        if (!(randomPlayer instanceof FakePlayer)) {
                            beeEntity.setTarget(randomPlayer);
                        }
                    });
            }
        }
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void appendHoverText(@NotNull ItemStack stack, @Nullable BlockGetter level, @NotNull List<Component> components, @NotNull TooltipFlag flag) {
        setupTooltip(stack, level, components, flag);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(HONEY_LEVEL, FACING);
    }

    private void createHoneycombsTooltip(@NotNull List<Component> tooltip, CompoundTag blockEntityTag) {
        tooltip.add(TranslationConstants.BeeHive.HONEYCOMBS.withStyle(ChatFormatting.GOLD));
        if (blockEntityTag.contains(NBTConstants.BeeHive.HONEYCOMBS, Tag.TAG_LIST)) {
            HashMap<String, Integer> combs = new HashMap<>();
            ListTag combList = blockEntityTag.getList(NBTConstants.BeeHive.HONEYCOMBS, Tag.TAG_LIST);

            for (int i = 0; i < combList.size(); i++) {
                String id = combList.getCompound(i).getString("id");
                String comb = id.substring(id.indexOf(":") + 1).replace("_", " ");
                combs.merge(comb, 1, Integer::sum);
            }

            //noinspection deprecation
            combs.forEach((comb, count) -> tooltip.add(Component.literal("     ")
                    .append(String.valueOf(count))
                    .append("x ")
                    .append(WordUtils.capitalize(comb))));
        } else {
            tooltip.add(NONE_TEXT);
        }
    }

    private void createBeesTooltip(@NotNull List<Component> tooltip, CompoundTag blockEntityTag) {
        tooltip.add(TranslationConstants.BeeHive.BEES.withStyle(ChatFormatting.GOLD));
        if (blockEntityTag.contains("Bees", Tag.TAG_LIST)) {
            HashMap<String, Integer> bees = new HashMap<>();
            ListTag beeList = blockEntityTag.getList(NBTConstants.NBT_BEES, Tag.TAG_COMPOUND);

            for (int i = 0; i < beeList.size(); i++) {
                CompoundTag entityData = beeList.getCompound(i).getCompound("EntityData");
                String id = entityData.getString("id");
                String beeType = id.substring(id.indexOf(":") + 1).replace("_", " ");
                if (beeType.length() == 3 && beeType.matches("Bee")) {
                    bees.merge("Minecraft Bee", 1, Integer::sum);
                } else {
                    bees.merge(beeType, 1, Integer::sum);
                }
            }

            //noinspection deprecation
            bees.forEach((name, count) -> tooltip.add(Component.literal("     ")
                    .append(String.valueOf(count))
                    .append("x ")
                    .append(WordUtils.capitalize(name))));
        } else {
            tooltip.add(NONE_TEXT);
        }
        tooltip.add(Component.empty());
    }

    /**
     * @param player is nullable when being called from dispenser
     */
    public boolean dropResourceHoneycomb(Level level, BlockPos pos, @Nullable Player player, boolean useScraper) {
        if (level.getBlockEntity(pos) instanceof TieredBeehiveBlockEntity hive) {
            IntDoublePair extraRolls = ModCompatHelper.rollExtraHoneycombs(player, useScraper);
            int rolls = extraRolls.firstInt();
            while (hive.hasCombs()) {
                ItemStack honeycomb = hive.getResourceHoneycomb();
                popResource(level, pos, honeycomb);
                if (rolls > 0 && level.random.nextDouble() < extraRolls.secondDouble()) {
                    ItemStack extraHoneycomb = honeycomb.copy();
                    extraHoneycomb.setCount(1);
                    popResource(level, pos, extraHoneycomb);
                }
                rolls--;
                if (useScraper) break;
            }
            return !hive.hasCombs();
        }
        return false;
    }

    @Override
    public Component getShiftingDisplay() {
        return TranslationConstants.Items.FOR_MORE_INFO;
    }

    @Override
    public void appendShiftTooltip(@NotNull ItemStack stack, @Nullable BlockGetter level, @NotNull List<Component> components, @NotNull TooltipFlag flag) {
//        if (stack.hasTag()) {
//            CompoundTag stackTag = stack.getTag();
//            if (stackTag != null && !stackTag.isEmpty() && stackTag.contains(NBTConstants.NBT_BLOCK_ENTITY_TAG)) {
//                CompoundTag blockEntityTag = stackTag.getCompound(NBTConstants.NBT_BLOCK_ENTITY_TAG);
//                localTier = blockEntityTag.getInt(NBTConstants.NBT_TIER);
//                localTierModifier = blockEntityTag.getFloat(NBTConstants.NBT_TIER_MODIFIER);
//            }
//        }
//
//
//        components.add(TranslationConstants.Items.MORE_INFO.withStyle(ChatFormatting.YELLOW));
//        components.add(new TranslatableComponent(TranslationConstants.BeeHive.MAX_BEES, Math.round(CommonConfig.HIVE_MAX_BEES.get() * localTierModifier)).withStyle(ChatFormatting.GOLD));
//        components.add(new TranslatableComponent(TranslationConstants.BeeHive.MAX_COMBS, Math.round(CommonConfig.HIVE_MAX_COMBS.get() * localTierModifier)).withStyle(ChatFormatting.GOLD));
//
//
//        if (localTier != 1) {
//            int timeReduction = localTier > 1 ? (int) ((localTier * .05) * 100) : (int) (.05 * 100);
//            String sign = localTier > 1 ? "-" : "+";
//            components.add(new TranslatableComponent(TranslationConstants.BeeHive.HIVE_TIME, sign, timeReduction).withStyle(ChatFormatting.GOLD));
//        }
    }

    @Override
    public Component getControlDisplay() {
        return TranslationConstants.Items.TOOLTIP_STATS;
    }

    @Override
    public void appendControlTooltip(@NotNull ItemStack stack, @Nullable BlockGetter level, @NotNull List<Component> components, @NotNull TooltipFlag flag) {
        if (stack.hasTag()) {
            CompoundTag stackTag = stack.getTag();
            if (stackTag != null && !stackTag.isEmpty() && stackTag.contains(NBTConstants.NBT_BLOCK_ENTITY_TAG)) {
                CompoundTag blockEntityTag = stackTag.getCompound(NBTConstants.NBT_BLOCK_ENTITY_TAG);
                createBeesTooltip(components, blockEntityTag);
                createHoneycombsTooltip(components, blockEntityTag);
            }
        } else {
            components.add(TranslationConstants.BeeHive.BEES.withStyle(ChatFormatting.GOLD));
            components.add(NONE_TEXT);
            components.add(Component.empty());
            components.add(TranslationConstants.BeeHive.HONEYCOMBS.withStyle(ChatFormatting.GOLD));
            components.add(NONE_TEXT);
        }
    }
}
