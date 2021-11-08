package com.teamresourceful.resourcefulbees.common.block;

import com.teamresourceful.resourcefulbees.common.config.CommonConfig;
import com.teamresourceful.resourcefulbees.common.item.UpgradeItem;
import com.teamresourceful.resourcefulbees.common.lib.constants.NBTConstants;
import com.teamresourceful.resourcefulbees.common.registry.minecraft.ModItems;
import com.teamresourceful.resourcefulbees.common.tileentity.TieredBeehiveTileEntity;
import com.teamresourceful.resourcefulbees.common.utils.TooltipBuilder;
import com.teamresourceful.resourcefulbees.common.lib.constants.ModTags;
import net.minecraft.block.BeehiveBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.passive.BeeEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.BeehiveTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.FakePlayer;
import org.apache.commons.lang3.text.WordUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;

@SuppressWarnings("deprecation")
public class TieredBeehiveBlock extends BeehiveBlock {

    private static final StringTextComponent NONE_TEXT = new StringTextComponent("     NONE");
    public static final IntegerProperty TIER_PROPERTY = IntegerProperty.create("tier", 0, 4);
    private final int tier;
    private final float tierModifier;

    public TieredBeehiveBlock(final int tier, final float tierModifier, Properties properties) {
        super(properties);
        this.tier = tier;
        this.tierModifier = tierModifier;
        this.registerDefaultState(this.stateDefinition.any().setValue(HONEY_LEVEL, 0).setValue(FACING, Direction.NORTH).setValue(TIER_PROPERTY, tier));
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
    public static boolean dropResourceHoneycomb(TieredBeehiveBlock block, World world, BlockPos pos, boolean useScraper) {
        return block.dropResourceHoneycomb(world, pos, useScraper);
    }

    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context) {
        Direction direction = context.getPlayer() != null && context.getPlayer().isShiftKeyDown() ? context.getHorizontalDirection() : context.getHorizontalDirection().getOpposite();
        if (context.getItemInHand().getTag() != null){
            return this.defaultBlockState().setValue(FACING, direction).setValue(TIER_PROPERTY, context.getItemInHand().getTag().getCompound(NBTConstants.NBT_BLOCK_ENTITY_TAG).getInt(NBTConstants.NBT_TIER));
        }
        return this.defaultBlockState().setValue(FACING, direction).setValue(TIER_PROPERTY, tier);
    }

    @Nullable
    @Override
    public TileEntity newBlockEntity(@NotNull IBlockReader reader) {
        return null;
    }

    public boolean isHiveSmoked(BlockPos pos, World world) {
        TileEntity tileEntity = world.getBlockEntity(pos);
        return tileEntity instanceof TieredBeehiveTileEntity && ((TieredBeehiveTileEntity) tileEntity).isSedated();
    }

    @Override
    public @NotNull ActionResultType use(@NotNull BlockState state, @NotNull World world, @NotNull BlockPos pos, PlayerEntity player, @NotNull Hand handIn, @NotNull BlockRayTraceResult hit) {
        ItemStack itemstack = player.getItemInHand(handIn);

        if (state.getValue(HONEY_LEVEL) >= 5) {
            boolean isShear = CommonConfig.ALLOW_SHEARS.get() && itemstack.getItem().is(ModTags.Items.SHEARS);
            boolean isScraper = itemstack.getItem().equals(ModItems.SCRAPER.get());

            if (isShear || isScraper) {
                ActionResultType success = performHoneyHarvest(state, world, pos, player, handIn, itemstack, isScraper);
                if (success != null) return success;
            }
        }

        if (UpgradeItem.isUpgradeItem(itemstack) && UpgradeItem.getUpgradeType(itemstack).equals(NBTConstants.NBT_HIVE_UPGRADE)) {
            ActionResultType success = performHiveUpgrade(state, world, pos, itemstack);
            if (success != null) {
                return success;
            }
        }

        return ActionResultType.PASS;
    }

    @Nullable
    private ActionResultType performHiveUpgrade(@NotNull BlockState state, @NotNull World world, @NotNull BlockPos pos, ItemStack itemstack) {
        CompoundNBT data = ((UpgradeItem) itemstack.getItem()).getUpgradeData();
        TileEntity tileEntity = world.getBlockEntity(pos);
        if (tileEntity instanceof TieredBeehiveTileEntity) {
            TieredBeehiveTileEntity beehiveTileEntity = (TieredBeehiveTileEntity) tileEntity;

            if (1 + beehiveTileEntity.getTier() == data.getInt(NBTConstants.NBT_TIER)) {
                int newTier = data.getInt(NBTConstants.NBT_TIER);
                beehiveTileEntity.setTier(newTier);
                beehiveTileEntity.setTierModifier(data.getFloat(NBTConstants.NBT_TIER_MODIFIER));
                beehiveTileEntity.recalculateHoneyLevel();
                itemstack.shrink(1);
                state = state.setValue(TIER_PROPERTY, newTier);
                world.setBlockAndUpdate(pos, state);
                return ActionResultType.SUCCESS;
            }
        }
        return null;
    }

    @Nullable
    private ActionResultType performHoneyHarvest(@NotNull BlockState state, @NotNull World world, @NotNull BlockPos pos, PlayerEntity player, @NotNull Hand handIn, ItemStack itemstack, boolean isScraper) {
        world.playSound(player, player.getX(), player.getY(), player.getZ(), SoundEvents.BEEHIVE_SHEAR, SoundCategory.NEUTRAL, 1.0F, 1.0F);
        dropResourceHoneycomb(world, pos, isScraper);
        itemstack.hurtAndBreak(1, player, player1 -> player1.broadcastBreakEvent(handIn));

        TileEntity tileEntity = world.getBlockEntity(pos);
        if (tileEntity instanceof TieredBeehiveTileEntity) {
            TieredBeehiveTileEntity beehiveTileEntity = (TieredBeehiveTileEntity) tileEntity;

            if (!beehiveTileEntity.hasCombs()) {
                if (isHiveSmoked(pos, world)) {
                    this.resetHoneyLevel(world, state, pos);
                } else {
                    if (beehiveTileEntity.hasBees() && !(player instanceof FakePlayer)) {
                        this.angerBeesNearby(world, pos);
                    }
                    this.releaseBeesAndResetHoneyLevel(world, state, pos, player, BeehiveTileEntity.State.EMERGENCY);
                }
                return ActionResultType.SUCCESS;
            }
        }
        return null;
    }

    private void angerBeesNearby(World world, BlockPos pos) {
        AxisAlignedBB aabb = new AxisAlignedBB(pos).inflate(8.0D, 6.0D, 8.0D);
        List<BeeEntity> beeEntityList = world.getEntitiesOfClass(BeeEntity.class, aabb);
        if (!beeEntityList.isEmpty()) {
            List<PlayerEntity> playerEntityList = world.getEntitiesOfClass(PlayerEntity.class, aabb);

            if (!playerEntityList.isEmpty()) {
                beeEntityList.stream()
                        .filter(beeEntity -> beeEntity.getTarget() == null)
                        .forEach(beeEntity -> {
                            PlayerEntity randomPlayer = playerEntityList.get(world.random.nextInt(playerEntityList.size()));
                            if (!(randomPlayer instanceof FakePlayer))
                                beeEntity.setTarget(randomPlayer);
                        });
            }
        }
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void appendHoverText(@NotNull ItemStack stack, @Nullable IBlockReader worldIn, @NotNull List<ITextComponent> tooltip, @NotNull ITooltipFlag flagIn) {
        if (Screen.hasShiftDown()) {
            createAdvancedTooltip(stack, tooltip);
        } else {
            createNormalTooltip(tooltip, stack);
        }

        super.appendHoverText(stack, worldIn, tooltip, flagIn);
    }

    private void createNormalTooltip(@NotNull List<ITextComponent> tooltip, ItemStack stack) {
        int localTier = this.tier;
        float localTierModifier = this.tierModifier;

        if (stack.hasTag()) {
            CompoundNBT stackTag = stack.getTag();
            if (stackTag != null && !stackTag.isEmpty() && stackTag.contains(NBTConstants.NBT_BLOCK_ENTITY_TAG)) {
                CompoundNBT blockEntityTag = stackTag.getCompound(NBTConstants.NBT_BLOCK_ENTITY_TAG);
                localTier = blockEntityTag.getInt(NBTConstants.NBT_TIER);
                localTierModifier = blockEntityTag.getFloat(NBTConstants.NBT_TIER_MODIFIER);
            }
        }


        tooltip.add(new TranslationTextComponent("resourcefulbees.shift_info").withStyle(TextFormatting.YELLOW));

        tooltip.addAll(new TooltipBuilder()
                .addTranslatableTip("block.resourcefulbees.beehive.tooltip.max_bees")
                .appendText(" " + Math.round(CommonConfig.HIVE_MAX_BEES.get() * localTierModifier))
                .applyStyle(TextFormatting.GOLD)
                .addTranslatableTip("block.resourcefulbees.beehive.tooltip.max_combs")
                .appendText(" " + Math.round(CommonConfig.HIVE_MAX_COMBS.get() * localTierModifier))
                .applyStyle(TextFormatting.GOLD)
                .build());

        if (localTier != 1) {
            int timeReduction = localTier > 1 ? (int) ((localTier * .05) * 100) : (int) (.05 * 100);
            String sign = localTier > 1 ? "-" : "+";
            tooltip.addAll(new TooltipBuilder()
                    .addTranslatableTip("block.resourcefulbees.beehive.tooltip.hive_time")
                    .appendText(" " + sign + timeReduction + "%")
                    .applyStyle(TextFormatting.GOLD)
                    .build());
        }
    }

    private void createAdvancedTooltip(@NotNull ItemStack stack, @NotNull List<ITextComponent> tooltip) {
        if (stack.hasTag()) {
            CompoundNBT stackTag = stack.getTag();
            if (stackTag != null && !stackTag.isEmpty() && stackTag.contains(NBTConstants.NBT_BLOCK_ENTITY_TAG)) {
                CompoundNBT blockEntityTag = stackTag.getCompound(NBTConstants.NBT_BLOCK_ENTITY_TAG);
                createBeesTooltip(tooltip, blockEntityTag);
                createHoneycombsTooltip(tooltip, blockEntityTag);
            }
        } else {
            tooltip.add(new TranslationTextComponent("block.resourcefulbees.beehive.tooltip.bees")
                    .withStyle(TextFormatting.GOLD));
            tooltip.add(NONE_TEXT);
            tooltip.add(StringTextComponent.EMPTY);
            tooltip.add(new TranslationTextComponent("block.resourcefulbees.beehive.tooltip.honeycombs")
                    .withStyle(TextFormatting.GOLD));
            tooltip.add(NONE_TEXT);
        }
    }

    @Override
    protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(HONEY_LEVEL, FACING, TIER_PROPERTY);
    }

    private void createHoneycombsTooltip(@NotNull List<ITextComponent> tooltip, CompoundNBT blockEntityTag) {
        tooltip.add(new TranslationTextComponent("block.resourcefulbees.beehive.tooltip.honeycombs")
                .withStyle(TextFormatting.GOLD));
        if (blockEntityTag.contains(NBTConstants.NBT_HONEYCOMBS_TE, Constants.NBT.TAG_LIST)) {
            HashMap<String, Integer> combs = new HashMap<>();
            ListNBT combList = blockEntityTag.getList(NBTConstants.NBT_HONEYCOMBS_TE, Constants.NBT.TAG_COMPOUND);

            for (int i = 0; i < combList.size(); i++) {
                String id = combList.getCompound(i).getString("id");
                String comb = id.substring(id.indexOf(":") + 1).replace("_", " ");
                combs.merge(comb, 1, Integer::sum);
            }

            combs.forEach((comb, count) -> tooltip.add(new StringTextComponent("     ")
                    .append(String.valueOf(count))
                    .append("x ")
                    .append(WordUtils.capitalize(comb))));
        } else {
            tooltip.add(NONE_TEXT);
        }
    }

    private void createBeesTooltip(@NotNull List<ITextComponent> tooltip, CompoundNBT blockEntityTag) {
        tooltip.add(new TranslationTextComponent("block.resourcefulbees.beehive.tooltip.bees")
                .withStyle(TextFormatting.GOLD));

        if (blockEntityTag.contains("Bees", Constants.NBT.TAG_LIST)) {
            HashMap<String, Integer> bees = new HashMap<>();
            ListNBT beeList = blockEntityTag.getList(NBTConstants.NBT_BEES, Constants.NBT.TAG_COMPOUND);

            for (int i = 0; i < beeList.size(); i++) {
                CompoundNBT entityData = beeList.getCompound(i).getCompound("EntityData");
                String id = entityData.getString("id");
                String beeType = id.substring(id.indexOf(":") + 1).replace("_", " ");
                if (beeType.length() == 3 && beeType.matches("Bee")) {
                    bees.merge("Minecraft Bee", 1, Integer::sum);
                } else {
                    bees.merge(beeType, 1, Integer::sum);
                }
            }

            bees.forEach((name, count) -> tooltip.add(new StringTextComponent("     ")
                    .append(String.valueOf(count))
                    .append("x ")
                    .append(WordUtils.capitalize(name))));
        } else {
            tooltip.add(NONE_TEXT);
        }
        tooltip.add(StringTextComponent.EMPTY);
    }

    public boolean dropResourceHoneycomb(World world, BlockPos pos, boolean useScraper) {
        TileEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity instanceof TieredBeehiveTileEntity) {
            TieredBeehiveTileEntity hive = (TieredBeehiveTileEntity) blockEntity;
            while (hive.hasCombs()) {
                popResource(world, pos, hive.getResourceHoneycomb());
                if (useScraper) break;
            }
            return !hive.hasCombs();
        }
        return false;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return new TieredBeehiveTileEntity(tier, tierModifier);
    }
}
