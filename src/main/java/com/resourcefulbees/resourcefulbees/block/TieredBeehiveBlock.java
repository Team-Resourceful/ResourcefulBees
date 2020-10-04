package com.resourcefulbees.resourcefulbees.block;

import com.resourcefulbees.resourcefulbees.config.Config;
import com.resourcefulbees.resourcefulbees.lib.NBTConstants;
import com.resourcefulbees.resourcefulbees.registry.RegistryHandler;
import com.resourcefulbees.resourcefulbees.tileentity.TieredBeehiveTileEntity;
import com.resourcefulbees.resourcefulbees.utils.BeeInfoUtils;
import com.resourcefulbees.resourcefulbees.utils.TooltipBuilder;
import mcjty.theoneprobe.api.ElementAlignment;
import net.minecraft.block.*;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.passive.BeeEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
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
import net.minecraft.world.GameRules;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import org.apache.commons.lang3.text.WordUtils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TieredBeehiveBlock extends BeehiveBlock {

    private final int TIER;
    private final float TIER_MODIFIER;

    public TieredBeehiveBlock(final int tier, final float tierModifier, Properties properties) {
        super(properties);
        TIER = tier;
        TIER_MODIFIER = tierModifier;
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
    protected void fillStateContainer(@Nonnull StateContainer.Builder<Block, BlockState> builder) {
        super.fillStateContainer(builder);
    }

    @Nullable
    @Override
    public TileEntity createNewTileEntity(@Nonnull IBlockReader reader) {
        return null;
    }

    public void smokeHive(BlockPos pos, World world) {
        TileEntity blockEntity = world.getTileEntity(pos);
        if (blockEntity instanceof TieredBeehiveTileEntity) {
            TieredBeehiveTileEntity hive = (TieredBeehiveTileEntity) blockEntity;
            hive.isSmoked = true;
        }
    }

    public boolean isHiveSmoked(BlockPos pos, World world) {
        TileEntity blockEntity = world.getTileEntity(pos);
        if (blockEntity instanceof TieredBeehiveTileEntity) {
            TieredBeehiveTileEntity hive = (TieredBeehiveTileEntity) blockEntity;
            return hive.isSmoked;
        } else
            return false;
    }

    @Nonnull
    public ActionResultType onUse(BlockState state, @Nonnull World world, @Nonnull BlockPos pos, PlayerEntity player, @Nonnull Hand handIn, @Nonnull BlockRayTraceResult hit) {
        ItemStack itemstack = player.getHeldItem(handIn);
        int honeyLevel = state.get(HONEY_LEVEL);
        boolean angerBees = false;
        if (itemstack.getItem() == RegistryHandler.SMOKER.get() && itemstack.getDamage() < itemstack.getMaxDamage()) {
            smokeHive(pos, world);
        } else if (honeyLevel >= 5) {
            boolean isShear = Config.ALLOW_SHEARS.get() && itemstack.getItem().isIn(BeeInfoUtils.getItemTag("forge:shears"));
            boolean isScraper = itemstack.getItem().equals(RegistryHandler.SCRAPER.get());

            if (isShear || isScraper) {
                world.playSound(player, player.getX(), player.getY(), player.getZ(), SoundEvents.BLOCK_BEEHIVE_SHEAR, SoundCategory.NEUTRAL, 1.0F, 1.0F);
                dropResourceHoneycomb(world, pos, isScraper);
                itemstack.damageItem(1, player, player1 -> player1.sendBreakAnimation(handIn));
                TieredBeehiveTileEntity hive = (TieredBeehiveTileEntity) world.getTileEntity(pos);
                angerBees = hive != null && !hive.hasCombs();
            }
        }

        if (angerBees) {
            if (isHiveSmoked(pos, world) || CampfireBlock.isLitCampfireInRange(world, pos)) {
                this.takeHoney(world, state, pos);
            } else {
                if (this.hasBees(world, pos)) {
                    this.angerNearbyBees(world, pos);
                }

                this.takeHoney(world, state, pos, player, BeehiveTileEntity.State.EMERGENCY);
            }
            return ActionResultType.SUCCESS;
        } else {
            return ActionResultType.PASS;
        }
    }

    public void angerNearbyBees(World world, BlockPos pos) {
        List<BeeEntity> beeEntityList = world.getEntitiesWithinAABB(BeeEntity.class, (new AxisAlignedBB(pos)).grow(8.0D, 6.0D, 8.0D));
        if (!beeEntityList.isEmpty()) {
            List<PlayerEntity> playerEntityList = world.getEntitiesWithinAABB(PlayerEntity.class, (new AxisAlignedBB(pos)).grow(8.0D, 6.0D, 8.0D));
            int size = playerEntityList.size();

            for (BeeEntity beeEntity : beeEntityList) {
                if (beeEntity.getAttackTarget() == null) {
                    beeEntity.setAttackTarget(playerEntityList.get(world.rand.nextInt(size)));
                }
            }
        }
    }

    public boolean hasBees(World world, BlockPos pos) {
        TileEntity tileEntity = world.getTileEntity(pos);
        if (tileEntity instanceof BeehiveTileEntity) {
            BeehiveTileEntity beehiveTileEntity = (BeehiveTileEntity) tileEntity;
            return !beehiveTileEntity.hasNoBees();
        } else {
            return false;
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
                    .appendText(" " + Math.round(Config.HIVE_MAX_BEES.get() * TIER_MODIFIER))
                    .applyStyle(TextFormatting.GOLD)
                    .addTip(I18n.format("block.resourcefulbees.beehive.tooltip.max_combs"))
                    .appendText(" " + Math.round(Config.HIVE_MAX_COMBS.get() * TIER_MODIFIER))
                    .applyStyle(TextFormatting.GOLD)
                    .build());

            if (TIER != 1) {
                int time_reduction = TIER > 1 ? (int) ((TIER * .05) * 100) : (int) (.05 * 100);
                String sign = TIER > 1 ? "-" : "+";
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
            tieredBeehiveTileEntity.setTier(TIER);
            tieredBeehiveTileEntity.setTierModifier(TIER_MODIFIER);
        }
    }
}
