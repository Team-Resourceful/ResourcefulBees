package com.resourcefulbees.resourcefulbees.block;

import com.resourcefulbees.resourcefulbees.config.Config;
import com.resourcefulbees.resourcefulbees.item.UpgradeItem;
import com.resourcefulbees.resourcefulbees.lib.NBTConstants;
import com.resourcefulbees.resourcefulbees.registry.ModItems;
import com.resourcefulbees.resourcefulbees.tileentity.TieredBeehiveTileEntity;
import com.resourcefulbees.resourcefulbees.utils.BeeInfoUtils;
import com.resourcefulbees.resourcefulbees.utils.TooltipBuilder;
import net.minecraft.block.BeehiveBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.resources.I18n;
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
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
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

    private static final String SHEARS_TAG = "forge:shears";
    public static final IntegerProperty TIER_PROPERTY = IntegerProperty.create("tier", 0, 4);
    private final int tier;
    private final float tierModifier;

    public TieredBeehiveBlock(final int tier, final float tierModifier, Properties properties) {
        super(properties);
        this.tier = tier;
        this.tierModifier = tierModifier;
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
        if (context.getPlayer() != null && context.getPlayer().isShiftKeyDown()) {
            return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection()).setValue(TIER_PROPERTY, tier);
        }
        return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite()).setValue(TIER_PROPERTY, tier);
    }

    @Nullable
    @Override
    public TileEntity newBlockEntity(@NotNull IBlockReader reader) { return null; }

    public boolean isHiveSmoked(BlockPos pos, World world) {
        TileEntity tileEntity = world.getBlockEntity(pos);
        return tileEntity instanceof TieredBeehiveTileEntity && ((TieredBeehiveTileEntity) tileEntity).isSedated();
    }

    @Override
    public @NotNull ActionResultType use(@NotNull BlockState state, @NotNull World world, @NotNull BlockPos pos, PlayerEntity player, @NotNull Hand handIn, @NotNull BlockRayTraceResult hit) {
        ItemStack itemstack = player.getItemInHand(handIn);

        if (state.getValue(HONEY_LEVEL) >= 5) {
            boolean isShear = Config.ALLOW_SHEARS.get() && itemstack.getItem().is(BeeInfoUtils.getItemTag(SHEARS_TAG));
            boolean isScraper = itemstack.getItem().equals(ModItems.SCRAPER.get());

            if (isShear || isScraper) {
                ActionResultType success = performHoneyHarvest(state, world, pos, player, handIn, itemstack, isScraper);
                if (success != null) return success;
            }
        }

        if (UpgradeItem.isUpgradeItem(itemstack) && UpgradeItem.getUpgradeType(itemstack).equals(NBTConstants.NBT_HIVE_UPGRADE)) {
            ActionResultType success = performHiveUpgrade(world, pos, itemstack);
            if (success != null) return success;
        }

        return ActionResultType.PASS;
    }

    @Nullable
    private ActionResultType performHiveUpgrade(@NotNull World world, @NotNull BlockPos pos, ItemStack itemstack) {
        CompoundNBT data = ((UpgradeItem) itemstack.getItem()).getUpgradeData();
        TileEntity tileEntity = world.getBlockEntity(pos);
        if (tileEntity instanceof TieredBeehiveTileEntity) {
            TieredBeehiveTileEntity beehiveTileEntity = (TieredBeehiveTileEntity) tileEntity;

            if (1 + beehiveTileEntity.getTier() == data.getInt(NBTConstants.NBT_TIER)) {
                beehiveTileEntity.setTier(data.getInt(NBTConstants.NBT_TIER));
                beehiveTileEntity.setTierModifier(data.getInt(NBTConstants.NBT_TIER_MODIFIER));
                beehiveTileEntity.recalculateHoneyLevel();
                itemstack.shrink(1);
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
            createNormalTooltip(tooltip);
        }

        super.appendHoverText(stack, worldIn, tooltip, flagIn);
    }

    private void createNormalTooltip(@NotNull List<ITextComponent> tooltip) {
        tooltip.add(new StringTextComponent(TextFormatting.YELLOW + I18n.get("resourcefulbees.shift_info")));

        tooltip.addAll(new TooltipBuilder()
                .addTip(new TranslationTextComponent("block.resourcefulbees.beehive.tooltip.max_bees").getString())
                .appendText(" " + Math.round(Config.HIVE_MAX_BEES.get() * tierModifier))
                .applyStyle(TextFormatting.GOLD)
                .addTip(new TranslationTextComponent("block.resourcefulbees.beehive.tooltip.max_combs").getString())
                .appendText(" " + Math.round(Config.HIVE_MAX_COMBS.get() * tierModifier))
                .applyStyle(TextFormatting.GOLD)
                .build());

        if (tier != 1) {
            int timeReduction = tier > 1 ? (int) ((tier * .05) * 100) : (int) (.05 * 100);
            String sign = tier > 1 ? "-" : "+";
            tooltip.addAll(new TooltipBuilder()
                    .addTip(new TranslationTextComponent("block.resourcefulbees.beehive.tooltip.hive_time").getString())
                    .appendText(" " + sign + timeReduction + "%")
                    .applyStyle(TextFormatting.GOLD)
                    .build());
        }
    }

    private void createAdvancedTooltip(@NotNull ItemStack stack, @NotNull List<ITextComponent> tooltip) {
        if (stack.hasTag()) {
            CompoundNBT stackTag = stack.getTag();
            if (stackTag != null && !stackTag.isEmpty() && stackTag.contains("BlockEntityTag")) {
                CompoundNBT blockEntityTag = stackTag.getCompound("BlockEntityTag");
                createBeesTooltip(tooltip, blockEntityTag);
                createHoneycombsTooltip(tooltip, blockEntityTag);
            }
        } else {
            tooltip.add(new TranslationTextComponent("block.resourcefulbees.beehive.tooltip.bees")
                    .withStyle(TextFormatting.AQUA, TextFormatting.RESET));
            tooltip.add(new TranslationTextComponent("block.resourcefulbees.beehive.tooltip.honeycombs")
                    .withStyle(TextFormatting.AQUA, TextFormatting.RESET));
        }
    }

    @Override
    protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> stateBuilder) {
        stateBuilder.add(HONEY_LEVEL, FACING, TIER_PROPERTY);
    }

    private void createHoneycombsTooltip(@NotNull List<ITextComponent> tooltip, CompoundNBT blockEntityTag) {
        if (blockEntityTag.contains(NBTConstants.NBT_HONEYCOMBS_TE, Constants.NBT.TAG_LIST)) {
            HashMap<String, Integer> combs = new HashMap<>();
            tooltip.add(new TranslationTextComponent("block.resourcefulbees.beehive.tooltip.honeycombs")
                    .withStyle(TextFormatting.AQUA, TextFormatting.RESET));
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
        }
    }

    private void createBeesTooltip(@NotNull List<ITextComponent> tooltip, CompoundNBT blockEntityTag) {
        if (blockEntityTag.contains("Bees", Constants.NBT.TAG_LIST)) {
            HashMap<String, Integer> bees = new HashMap<>();

            tooltip.add(new TranslationTextComponent("block.resourcefulbees.beehive.tooltip.bees")
                    .withStyle(TextFormatting.AQUA, TextFormatting.RESET));
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
        }
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
