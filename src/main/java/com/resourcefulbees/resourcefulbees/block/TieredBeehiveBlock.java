package com.resourcefulbees.resourcefulbees.block;

import com.resourcefulbees.resourcefulbees.config.Config;
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
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.BeeEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
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
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.FakePlayer;
import org.apache.commons.lang3.text.WordUtils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.List;

public class TieredBeehiveBlock extends BeehiveBlock {

    private static final String SHEARS_TAG = "forge:shears";
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

    public BlockState getStateForPlacement(BlockItemUseContext context) {
        if (context.getPlayer() != null && context.getPlayer().isSneaking()) {
            return this.getDefaultState().with(FACING, context.getPlacementHorizontalFacing());
        }
        return this.getDefaultState().with(FACING, context.getPlacementHorizontalFacing().getOpposite());
    }

    @Override
    protected void fillStateContainer(@Nonnull StateContainer.Builder<Block, BlockState> builder) {
        super.fillStateContainer(builder);
    }

    @Nullable
    @Override
    public TileEntity createNewTileEntity(@Nonnull IBlockReader reader) { return null; }

    protected void smokeHive(BlockPos pos, World world) {
        TileEntity blockEntity = world.getTileEntity(pos);
        if (blockEntity instanceof TieredBeehiveTileEntity) {
            ((TieredBeehiveTileEntity) blockEntity).smokeHive();
        }
    }

    public boolean isHiveSmoked(BlockPos pos, World world) {
        TileEntity tileEntity = world.getTileEntity(pos);
        return tileEntity instanceof TieredBeehiveTileEntity && ((TieredBeehiveTileEntity) tileEntity).isSmoked();
    }

    @Nonnull
    public ActionResultType onUse(@Nonnull BlockState state, @Nonnull World world, @Nonnull BlockPos pos, PlayerEntity player, @Nonnull Hand handIn, @Nonnull BlockRayTraceResult hit) {
        ItemStack itemstack = player.getHeldItem(handIn);

        if (itemstack.getItem() == ModItems.SMOKER.get() && itemstack.getDamage() < itemstack.getMaxDamage()) {
            smokeHive(pos, world);
            return ActionResultType.SUCCESS;
        }

        if (state.get(HONEY_LEVEL) >= 5) {
            boolean isShear = Config.ALLOW_SHEARS.get() && itemstack.getItem().isIn(BeeInfoUtils.getItemTag(SHEARS_TAG));
            boolean isScraper = itemstack.getItem().equals(ModItems.SCRAPER.get());

            if (isShear || isScraper) {
                world.playSound(player, player.getX(), player.getY(), player.getZ(), SoundEvents.BLOCK_BEEHIVE_SHEAR, SoundCategory.NEUTRAL, 1.0F, 1.0F);
                dropResourceHoneycomb(world, pos, isScraper);
                itemstack.damageItem(1, player, player1 -> player1.sendBreakAnimation(handIn));

                TileEntity tileEntity = world.getTileEntity(pos);
                if (tileEntity instanceof TieredBeehiveTileEntity) {
                    TieredBeehiveTileEntity beehiveTileEntity = (TieredBeehiveTileEntity) tileEntity;

                    if (!beehiveTileEntity.hasCombs()) {
                        if (isHiveSmoked(pos, world)) {
                            this.takeHoney(world, state, pos);
                        } else {
                            if (beehiveTileEntity.hasBees() && !(player instanceof FakePlayer)) {
                                this.angerNearbyBees(world, pos);
                            }
                            this.takeHoney(world, state, pos, player, BeehiveTileEntity.State.EMERGENCY);
                        }
                        return ActionResultType.SUCCESS;
                    }
                }
            }
        }

        return ActionResultType.PASS;
    }

    public void angerNearbyBees(World world, BlockPos pos) {
        AxisAlignedBB aabb = new AxisAlignedBB(pos).grow(8.0D, 6.0D, 8.0D);
        List<BeeEntity> beeEntityList = world.getEntitiesWithinAABB(BeeEntity.class, aabb);
        if (!beeEntityList.isEmpty()) {
            List<PlayerEntity> playerEntityList = world.getEntitiesWithinAABB(PlayerEntity.class, aabb);

            if (!playerEntityList.isEmpty()) {
                beeEntityList.stream()
                        .filter(beeEntity -> beeEntity.getAttackTarget() == null)
                        .forEach(beeEntity -> {
                            PlayerEntity randomPlayer = playerEntityList.get(world.rand.nextInt(playerEntityList.size()));
                            if (!(randomPlayer instanceof FakePlayer))
                                beeEntity.setAttackTarget(randomPlayer);
                        });
            }
        }
    }

    @Override
    public void addInformation(@Nonnull ItemStack stack, @Nullable IBlockReader worldIn, @Nonnull List<ITextComponent> tooltip, @Nonnull ITooltipFlag flagIn) {
        if (Screen.hasShiftDown()) {
            if (stack.hasTag()) {
                CompoundNBT stackTag = stack.getTag();
                if (stackTag != null && !stackTag.isEmpty()) {
                    if (stackTag.contains("BlockEntityTag")) {
                        CompoundNBT blockEntityTag = stackTag.getCompound("BlockEntityTag");

                        if (blockEntityTag.contains("Bees", Constants.NBT.TAG_LIST)) {
                            HashMap<String, Integer> bees = new HashMap<>();

                            tooltip.add(new TranslationTextComponent("block.resourcefulbees.beehive.tooltip.bees")
                                    .formatted(TextFormatting.AQUA, TextFormatting.RESET));
                            ListNBT beeList = blockEntityTag.getList(NBTConstants.NBT_BEES, Constants.NBT.TAG_COMPOUND);

                            for (int i = 0; i < beeList.size(); i++) {
                                CompoundNBT entityData = beeList.getCompound(i).getCompound("EntityData");
                                String id = entityData.getString("id");
                                String beeType = id.substring(id.indexOf(":") + 1).replaceAll("_", " ");
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

                        if (blockEntityTag.contains(NBTConstants.NBT_HONEYCOMBS_TE, Constants.NBT.TAG_LIST)) {
                            HashMap<String, Integer> combs = new HashMap<>();
                            tooltip.add(new TranslationTextComponent("block.resourcefulbees.beehive.tooltip.honeycombs")
                                    .formatted(TextFormatting.AQUA, TextFormatting.RESET));
                            ListNBT combList = blockEntityTag.getList(NBTConstants.NBT_HONEYCOMBS_TE, Constants.NBT.TAG_COMPOUND);

                            for (int i = 0; i < combList.size(); i++) {
                                String id = combList.getCompound(i).getString("id");
                                String comb = id.substring(id.indexOf(":") + 1).replaceAll("_", " ");
                                combs.merge(comb, 1, Integer::sum);
                            }

                            combs.forEach((comb, count) -> tooltip.add(new StringTextComponent("     ")
                                    .append(String.valueOf(count))
                                    .append("x ")
                                    .append(WordUtils.capitalize(comb))));
                        }
                    }
                }
            } else {
                tooltip.add(new TranslationTextComponent("block.resourcefulbees.beehive.tooltip.bees")
                        .formatted(TextFormatting.AQUA, TextFormatting.RESET));
                tooltip.add(new TranslationTextComponent("block.resourcefulbees.beehive.tooltip.honeycombs")
                        .formatted(TextFormatting.AQUA, TextFormatting.RESET));
            }
        } else {
            tooltip.add(new StringTextComponent(TextFormatting.YELLOW + I18n.format("resourcefulbees.shift_info")));

            tooltip.addAll(new TooltipBuilder()
                    .addTip(I18n.format("block.resourcefulbees.beehive.tooltip.max_bees"))
                    .appendText(" " + Math.round(Config.HIVE_MAX_BEES.get() * tierModifier))
                    .applyStyle(TextFormatting.GOLD)
                    .addTip(I18n.format("block.resourcefulbees.beehive.tooltip.max_combs"))
                    .appendText(" " + Math.round(Config.HIVE_MAX_COMBS.get() * tierModifier))
                    .applyStyle(TextFormatting.GOLD)
                    .build());

            if (tier != 1) {
                int time_reduction = tier > 1 ? (int) ((tier * .05) * 100) : (int) (.05 * 100);
                String sign = tier > 1 ? "-" : "+";
                tooltip.addAll(new TooltipBuilder()
                        .addTip(I18n.format("block.resourcefulbees.beehive.tooltip.hive_time"))
                        .appendText(" " + sign + time_reduction + "%")
                        .applyStyle(TextFormatting.GOLD)
                        .build());
            }
        }

        super.addInformation(stack, worldIn, tooltip, flagIn);
    }

    public boolean dropResourceHoneycomb(World world, BlockPos pos, boolean useScraper) {
        TileEntity blockEntity = world.getTileEntity(pos);
        if (blockEntity instanceof TieredBeehiveTileEntity) {
            TieredBeehiveTileEntity hive = (TieredBeehiveTileEntity) blockEntity;
            while (hive.hasCombs()) {
                ItemStack comb = hive.getResourceHoneycomb();
                spawnAsEntity(world, pos, comb);
                if (useScraper) break;
            }
            return !hive.hasCombs();
        }
        return false;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return new TieredBeehiveTileEntity();
    }

    @Override
    public void onBlockPlacedBy(World worldIn, @Nonnull BlockPos pos, @Nonnull BlockState state, @Nullable LivingEntity placer, @Nonnull ItemStack stack) {
        TileEntity tile = worldIn.getTileEntity(pos);
        if (tile instanceof TieredBeehiveTileEntity) {
            TieredBeehiveTileEntity tieredBeehiveTileEntity = (TieredBeehiveTileEntity) tile;
            tieredBeehiveTileEntity.setTier(tier);
            tieredBeehiveTileEntity.setTierModifier(tierModifier);
        }
    }
}
