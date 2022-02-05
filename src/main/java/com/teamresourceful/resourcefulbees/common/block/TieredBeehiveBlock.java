package com.teamresourceful.resourcefulbees.common.block;

import com.teamresourceful.resourcefulbees.common.blockentity.TieredBeehiveBlockEntity;
import com.teamresourceful.resourcefulbees.common.config.CommonConfig;
import com.teamresourceful.resourcefulbees.common.item.upgrade.UpgradeType;
import com.teamresourceful.resourcefulbees.common.item.upgrade.nestupgrade.INestUpgrade;
import com.teamresourceful.resourcefulbees.common.lib.constants.ModConstants;
import com.teamresourceful.resourcefulbees.common.lib.constants.NBTConstants;
import com.teamresourceful.resourcefulbees.common.lib.constants.TranslationConstants;
import com.teamresourceful.resourcefulbees.common.lib.enums.BeehiveTier;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
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
import net.minecraftforge.registries.RegistryObject;
import org.apache.commons.lang3.text.WordUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;

public class TieredBeehiveBlock extends BeehiveBlock {

    private static final TextComponent NONE_TEXT = new TextComponent("     NONE");
    //public static final IntegerProperty TIER_PROPERTY = IntegerProperty.create("tier", 1, 4);

    private final RegistryObject<BlockEntityType<TieredBeehiveBlockEntity>> entityType;
    private final BeehiveTier tier;

    public TieredBeehiveBlock(RegistryObject<BlockEntityType<TieredBeehiveBlockEntity>> entityType, BeehiveTier tier, Properties properties) {
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
     * @param world      the world
     * @param pos        the blocks position
     * @param useScraper set to true if scraper logic should run
     * @return returns true if the hive has been emptied
     */
    public static boolean dropResourceHoneycomb(TieredBeehiveBlock block, Level world, BlockPos pos, boolean useScraper) {
        return block.dropResourceHoneycomb(world, pos, useScraper);
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

    public boolean isHiveSmoked(BlockPos pos, Level world) {
        BlockEntity tileEntity = world.getBlockEntity(pos);
        return tileEntity instanceof TieredBeehiveBlockEntity && ((TieredBeehiveBlockEntity) tileEntity).isSedated();
    }

    @Override
    public @NotNull InteractionResult use(@NotNull BlockState state, @NotNull Level world, @NotNull BlockPos pos, Player player, @NotNull InteractionHand handIn, @NotNull BlockHitResult hit) {
        ItemStack itemstack = player.getItemInHand(handIn);

        if (state.getValue(HONEY_LEVEL) >= 5) {
            boolean isShear = CommonConfig.ALLOW_SHEARS.get() && itemstack.canPerformAction(ToolActions.SHEARS_HARVEST);
            boolean isScraper = itemstack.canPerformAction(ModConstants.SCRAPE_HIVE);

            if (isShear || isScraper) {
                InteractionResult success = performHoneyHarvest(state, world, pos, player, handIn, itemstack, isScraper);
                if (success != null) return success;
            }
        }

        if (itemstack.getItem() instanceof INestUpgrade upgrade && upgrade.getUpgradeType().equals(UpgradeType.NEST)) {
            if (upgrade.getTier().from.equals(this.tier)) return upgrade.getTier().upgrader.performUpgrade(state, world, pos, itemstack);
            else {
                player.displayClientMessage(new TextComponent("You can not upgrade this nest with that upgrade."), true);
            }
        }

        return InteractionResult.PASS;
    }

    @Nullable
    private InteractionResult performHoneyHarvest(@NotNull BlockState state, @NotNull Level world, @NotNull BlockPos pos, Player player, @NotNull InteractionHand handIn, ItemStack itemstack, boolean isScraper) {
        world.playSound(player, player.getX(), player.getY(), player.getZ(), SoundEvents.BEEHIVE_SHEAR, SoundSource.NEUTRAL, 1.0F, 1.0F);
        dropResourceHoneycomb(world, pos, isScraper);
        itemstack.hurtAndBreak(1, player, player1 -> player1.broadcastBreakEvent(handIn));

        BlockEntity tileEntity = world.getBlockEntity(pos);
        if (tileEntity instanceof TieredBeehiveBlockEntity beehiveTileEntity && !beehiveTileEntity.hasCombs()) {
            if (isHiveSmoked(pos, world)) {
                this.resetHoneyLevel(world, state, pos);
            } else {
                if (beehiveTileEntity.hasBees() && !(player instanceof FakePlayer)) {
                    this.angerBeesNearby(world, pos);
                }
                this.releaseBeesAndResetHoneyLevel(world, state, pos, player, BeehiveBlockEntity.BeeReleaseStatus.EMERGENCY);
            }
            return InteractionResult.SUCCESS;
        }
        return null;
    }

    private void angerBeesNearby(Level world, BlockPos pos) {
        AABB aabb = new AABB(pos).inflate(8.0D, 6.0D, 8.0D);
        List<Bee> beeEntityList = world.getEntitiesOfClass(Bee.class, aabb);
        if (!beeEntityList.isEmpty()) {
            List<Player> playerEntityList = world.getEntitiesOfClass(Player.class, aabb);

            if (!playerEntityList.isEmpty()) {
                beeEntityList.stream()
                        .filter(beeEntity -> beeEntity.getTarget() == null)
                        .forEach(beeEntity -> {
                            Player randomPlayer = playerEntityList.get(world.random.nextInt(playerEntityList.size()));
                            if (!(randomPlayer instanceof FakePlayer))
                                beeEntity.setTarget(randomPlayer);
                        });
            }
        }
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void appendHoverText(@NotNull ItemStack stack, @Nullable BlockGetter worldIn, @NotNull List<Component> tooltip, @NotNull TooltipFlag flagIn) {
        if (Screen.hasShiftDown()) {
            createAdvancedTooltip(stack, tooltip);
        } else {
            //createNormalTooltip(tooltip, stack);
        }

        super.appendHoverText(stack, worldIn, tooltip, flagIn);
    }


    //TODO create a better tooltip
/*    private void createNormalTooltip(@NotNull List<Component> tooltip, ItemStack stack) {
        if (stack.hasTag()) {
            CompoundTag stackTag = stack.getTag();
            if (stackTag != null && !stackTag.isEmpty() && stackTag.contains(NBTConstants.NBT_BLOCK_ENTITY_TAG)) {
                CompoundTag blockEntityTag = stackTag.getCompound(NBTConstants.NBT_BLOCK_ENTITY_TAG);
                localTier = blockEntityTag.getInt(NBTConstants.NBT_TIER);
                localTierModifier = blockEntityTag.getFloat(NBTConstants.NBT_TIER_MODIFIER);
            }
        }


        tooltip.add(TranslationConstants.Items.MORE_INFO.withStyle(ChatFormatting.YELLOW));
        tooltip.add(new TranslatableComponent(TranslationConstants.BeeHive.MAX_BEES, Math.round(CommonConfig.HIVE_MAX_BEES.get() * localTierModifier)).withStyle(ChatFormatting.GOLD));
        tooltip.add(new TranslatableComponent(TranslationConstants.BeeHive.MAX_COMBS, Math.round(CommonConfig.HIVE_MAX_COMBS.get() * localTierModifier)).withStyle(ChatFormatting.GOLD));


        if (localTier != 1) {
            int timeReduction = localTier > 1 ? (int) ((localTier * .05) * 100) : (int) (.05 * 100);
            String sign = localTier > 1 ? "-" : "+";
            tooltip.add(new TranslatableComponent(TranslationConstants.BeeHive.HIVE_TIME, sign, timeReduction).withStyle(ChatFormatting.GOLD));
        }
    }*/

    private void createAdvancedTooltip(@NotNull ItemStack stack, @NotNull List<Component> tooltip) {
        if (stack.hasTag()) {
            CompoundTag stackTag = stack.getTag();
            if (stackTag != null && !stackTag.isEmpty() && stackTag.contains(NBTConstants.NBT_BLOCK_ENTITY_TAG)) {
                CompoundTag blockEntityTag = stackTag.getCompound(NBTConstants.NBT_BLOCK_ENTITY_TAG);
                createBeesTooltip(tooltip, blockEntityTag);
                createHoneycombsTooltip(tooltip, blockEntityTag);
            }
        } else {
            tooltip.add(TranslationConstants.BeeHive.BEES.withStyle(ChatFormatting.GOLD));
            tooltip.add(NONE_TEXT);
            tooltip.add(TextComponent.EMPTY);
            tooltip.add(TranslationConstants.BeeHive.HONEYCOMBS.withStyle(ChatFormatting.GOLD));
            tooltip.add(NONE_TEXT);
        }
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(HONEY_LEVEL, FACING);//, TIER_PROPERTY);
    }

    private void createHoneycombsTooltip(@NotNull List<Component> tooltip, CompoundTag blockEntityTag) {
        tooltip.add(TranslationConstants.BeeHive.HONEYCOMBS.withStyle(ChatFormatting.GOLD));
        if (blockEntityTag.contains(NBTConstants.NBT_HONEYCOMBS_TE, Tag.TAG_LIST)) {
            HashMap<String, Integer> combs = new HashMap<>();
            ListTag combList = blockEntityTag.getList(NBTConstants.NBT_HONEYCOMBS_TE, Tag.TAG_LIST);

            for (int i = 0; i < combList.size(); i++) {
                String id = combList.getCompound(i).getString("id");
                String comb = id.substring(id.indexOf(":") + 1).replace("_", " ");
                combs.merge(comb, 1, Integer::sum);
            }

            combs.forEach((comb, count) -> tooltip.add(new TextComponent("     ")
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

            bees.forEach((name, count) -> tooltip.add(new TextComponent("     ")
                    .append(String.valueOf(count))
                    .append("x ")
                    .append(WordUtils.capitalize(name))));
        } else {
            tooltip.add(NONE_TEXT);
        }
        tooltip.add(TextComponent.EMPTY);
    }

    public boolean dropResourceHoneycomb(Level world, BlockPos pos, boolean useScraper) {
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity instanceof TieredBeehiveBlockEntity hive) {
            while (hive.hasCombs()) {
                popResource(world, pos, hive.getResourceHoneycomb());
                if (useScraper) break;
            }
            return !hive.hasCombs();
        }
        return false;
    }
}
