package com.teamresourceful.resourcefulbees.block;

import com.teamresourceful.resourcefulbees.config.Config;
import com.teamresourceful.resourcefulbees.item.UpgradeItem;
import com.teamresourceful.resourcefulbees.lib.NBTConstants;
import com.teamresourceful.resourcefulbees.registry.ModItems;
import com.teamresourceful.resourcefulbees.tileentity.TieredBeehiveTileEntity;
import com.teamresourceful.resourcefulbees.utils.BeeInfoUtils;
import com.teamresourceful.resourcefulbees.utils.TooltipBuilder;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
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
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
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
    public static boolean dropResourceHoneycomb(TieredBeehiveBlock block, Level world, BlockPos pos, boolean useScraper) {
        return block.dropResourceHoneycomb(world, pos, useScraper);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        Direction direction = context.getPlayer() != null && context.getPlayer().isShiftKeyDown() ? context.getHorizontalDirection() : context.getHorizontalDirection().getOpposite();
        if (context.getItemInHand().getTag() != null){
            return this.defaultBlockState().setValue(FACING, direction).setValue(TIER_PROPERTY, context.getItemInHand().getTag().getCompound(NBTConstants.NBT_BLOCK_ENTITY_TAG).getInt(NBTConstants.NBT_TIER));
        }
        return this.defaultBlockState().setValue(FACING, direction).setValue(TIER_PROPERTY, tier);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(@NotNull BlockGetter reader) {
        return null;
    }

    public boolean isHiveSmoked(BlockPos pos, Level world) {
        BlockEntity tileEntity = world.getBlockEntity(pos);
        return tileEntity instanceof TieredBeehiveTileEntity && ((TieredBeehiveTileEntity) tileEntity).isSedated();
    }

    @Override
    public @NotNull InteractionResult use(@NotNull BlockState state, @NotNull Level world, @NotNull BlockPos pos, Player player, @NotNull InteractionHand handIn, @NotNull BlockHitResult hit) {
        ItemStack itemstack = player.getItemInHand(handIn);

        if (state.getValue(HONEY_LEVEL) >= 5) {
            boolean isShear = Config.ALLOW_SHEARS.get() && itemstack.getItem().is(BeeInfoUtils.getItemTag(SHEARS_TAG));
            boolean isScraper = itemstack.getItem().equals(ModItems.SCRAPER.get());

            if (isShear || isScraper) {
                InteractionResult success = performHoneyHarvest(state, world, pos, player, handIn, itemstack, isScraper);
                if (success != null) return success;
            }
        }

        if (UpgradeItem.isUpgradeItem(itemstack) && UpgradeItem.getUpgradeType(itemstack).equals(NBTConstants.NBT_HIVE_UPGRADE)) {
            InteractionResult success = performHiveUpgrade(state, world, pos, itemstack);
            if (success != null) {
                return success;
            }
        }

        return InteractionResult.PASS;
    }

    @Nullable
    private InteractionResult performHiveUpgrade(@NotNull BlockState state, @NotNull Level world, @NotNull BlockPos pos, ItemStack itemstack) {
        CompoundTag data = ((UpgradeItem) itemstack.getItem()).getUpgradeData();
        BlockEntity tileEntity = world.getBlockEntity(pos);
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
                return InteractionResult.SUCCESS;
            }
        }
        return null;
    }

    @Nullable
    private InteractionResult performHoneyHarvest(@NotNull BlockState state, @NotNull Level world, @NotNull BlockPos pos, Player player, @NotNull InteractionHand handIn, ItemStack itemstack, boolean isScraper) {
        world.playSound(player, player.getX(), player.getY(), player.getZ(), SoundEvents.BEEHIVE_SHEAR, SoundSource.NEUTRAL, 1.0F, 1.0F);
        dropResourceHoneycomb(world, pos, isScraper);
        itemstack.hurtAndBreak(1, player, player1 -> player1.broadcastBreakEvent(handIn));

        BlockEntity tileEntity = world.getBlockEntity(pos);
        if (tileEntity instanceof TieredBeehiveTileEntity) {
            TieredBeehiveTileEntity beehiveTileEntity = (TieredBeehiveTileEntity) tileEntity;

            if (!beehiveTileEntity.hasCombs()) {
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
            createNormalTooltip(tooltip, stack);
        }

        super.appendHoverText(stack, worldIn, tooltip, flagIn);
    }

    private void createNormalTooltip(@NotNull List<Component> tooltip, ItemStack stack) {
        int localTier = this.tier;
        float localTierModifier = this.tierModifier;

        if (stack.hasTag()) {
            CompoundTag stackTag = stack.getTag();
            if (stackTag != null && !stackTag.isEmpty() && stackTag.contains(NBTConstants.NBT_BLOCK_ENTITY_TAG)) {
                CompoundTag blockEntityTag = stackTag.getCompound(NBTConstants.NBT_BLOCK_ENTITY_TAG);
                localTier = blockEntityTag.getInt(NBTConstants.NBT_TIER);
                localTierModifier = blockEntityTag.getFloat(NBTConstants.NBT_TIER_MODIFIER);
            }
        }


        tooltip.add(new TextComponent(ChatFormatting.YELLOW + I18n.get("resourcefulbees.shift_info")));

        tooltip.addAll(new TooltipBuilder()
                .addTranslatableTip("gui.resourcefulbees.beehive.tier")
                .appendText(String.valueOf(localTier))
                .applyStyle(ChatFormatting.GOLD)
                .addTranslatableTip("block.resourcefulbees.beehive.tooltip.max_bees")
                .appendText(" " + Math.round(Config.HIVE_MAX_BEES.get() * localTierModifier))
                .applyStyle(ChatFormatting.GOLD)
                .addTranslatableTip("block.resourcefulbees.beehive.tooltip.max_combs")
                .appendText(" " + Math.round(Config.HIVE_MAX_COMBS.get() * localTierModifier))
                .applyStyle(ChatFormatting.GOLD)
                .build());

        if (localTier != 1) {
            int timeReduction = localTier > 1 ? (int) ((localTier * .05) * 100) : (int) (.05 * 100);
            String sign = localTier > 1 ? "-" : "+";
            tooltip.addAll(new TooltipBuilder()
                    .addTranslatableTip("block.resourcefulbees.beehive.tooltip.hive_time")
                    .appendText(" " + sign + timeReduction + "%")
                    .applyStyle(ChatFormatting.GOLD)
                    .build());
        }
    }

    private void createAdvancedTooltip(@NotNull ItemStack stack, @NotNull List<Component> tooltip) {
        if (stack.hasTag()) {
            CompoundTag stackTag = stack.getTag();
            if (stackTag != null && !stackTag.isEmpty() && stackTag.contains(NBTConstants.NBT_BLOCK_ENTITY_TAG)) {
                CompoundTag blockEntityTag = stackTag.getCompound(NBTConstants.NBT_BLOCK_ENTITY_TAG);
                createBeesTooltip(tooltip, blockEntityTag);
                createHoneycombsTooltip(tooltip, blockEntityTag);
            }
        } else {
            tooltip.add(new TranslatableComponent("block.resourcefulbees.beehive.tooltip.bees")
                    .withStyle(ChatFormatting.AQUA, ChatFormatting.RESET));
            tooltip.add(new TranslatableComponent("block.resourcefulbees.beehive.tooltip.honeycombs")
                    .withStyle(ChatFormatting.AQUA, ChatFormatting.RESET));
        }
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(HONEY_LEVEL, FACING, TIER_PROPERTY);
    }

    private void createHoneycombsTooltip(@NotNull List<Component> tooltip, CompoundTag blockEntityTag) {
        if (blockEntityTag.contains(NBTConstants.NBT_HONEYCOMBS_TE, Constants.NBT.TAG_LIST)) {
            HashMap<String, Integer> combs = new HashMap<>();
            tooltip.add(new TranslatableComponent("block.resourcefulbees.beehive.tooltip.honeycombs")
                    .withStyle(ChatFormatting.AQUA, ChatFormatting.RESET));
            ListTag combList = blockEntityTag.getList(NBTConstants.NBT_HONEYCOMBS_TE, Constants.NBT.TAG_COMPOUND);

            for (int i = 0; i < combList.size(); i++) {
                String id = combList.getCompound(i).getString("id");
                String comb = id.substring(id.indexOf(":") + 1).replace("_", " ");
                combs.merge(comb, 1, Integer::sum);
            }

            combs.forEach((comb, count) -> tooltip.add(new TextComponent("     ")
                    .append(String.valueOf(count))
                    .append("x ")
                    .append(WordUtils.capitalize(comb))));
        }
    }

    private void createBeesTooltip(@NotNull List<Component> tooltip, CompoundTag blockEntityTag) {
        if (blockEntityTag.contains("Bees", Constants.NBT.TAG_LIST)) {
            HashMap<String, Integer> bees = new HashMap<>();

            tooltip.add(new TranslatableComponent("block.resourcefulbees.beehive.tooltip.bees")
                    .withStyle(ChatFormatting.AQUA, ChatFormatting.RESET));
            ListTag beeList = blockEntityTag.getList(NBTConstants.NBT_BEES, Constants.NBT.TAG_COMPOUND);

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
        }
    }

    public boolean dropResourceHoneycomb(Level world, BlockPos pos, boolean useScraper) {
        BlockEntity blockEntity = world.getBlockEntity(pos);
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
    public BlockEntity createTileEntity(BlockState state, BlockGetter world) {
        return new TieredBeehiveTileEntity(tier, tierModifier);
    }
}
