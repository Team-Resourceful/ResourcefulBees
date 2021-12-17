package com.resourcefulbees.resourcefulbees.item;

import com.resourcefulbees.resourcefulbees.ResourcefulBees;
import com.resourcefulbees.resourcefulbees.lib.BeeConstants;
import com.resourcefulbees.resourcefulbees.lib.NBTConstants;
import com.resourcefulbees.resourcefulbees.tileentity.multiblocks.apiary.ApiaryTileEntity;
import com.resourcefulbees.resourcefulbees.utils.BeeInfoUtils;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.BeeEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.tileentity.BeehiveTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class BeeBox extends Item {

    private static final String ITEM_DOT = "item.";

    boolean isTemp;

    public BeeBox(Properties properties, boolean isTemp) {
        super(properties);
        this.isTemp = isTemp;
    }


    @Override
    public @NotNull ActionResultType useOn(ItemUseContext context) {
        PlayerEntity player = context.getPlayer();
        if (player != null) {
            World playerWorld = context.getPlayer().getCommandSenderWorld();
            ItemStack stack = context.getItemInHand();
            if (playerWorld.isClientSide()) return ActionResultType.FAIL;

            World worldIn = context.getLevel();
            BlockPos pos = context.getClickedPos();

            if ((worldIn.getBlockEntity(pos) instanceof ApiaryTileEntity ||
                    worldIn.getBlockEntity(pos) instanceof BeehiveTileEntity) &&
                    context.getPlayer().isShiftKeyDown()) {
                return stealBees(worldIn, stack, pos);
            }
            if (!isFilled(stack)) return ActionResultType.FAIL;
            if (context.getPlayer().isShiftKeyDown()) {
                releaseAll(worldIn, stack, pos, context, player);
            } else {
                releaseOne(worldIn, stack, pos, context, player);
            }
            return ActionResultType.SUCCESS;
        }
        return ActionResultType.FAIL;
    }

    private void releaseOne(World worldIn, ItemStack stack, BlockPos pos, ItemUseContext context, PlayerEntity player) {
        assert stack.getTag() != null;
        ListNBT listNBT = stack.getTag().getList(NBTConstants.NBT_BEES, Constants.NBT.TAG_COMPOUND);
        CompoundNBT tag = (CompoundNBT) listNBT.remove(0);
        EntityType.byString(tag.getCompound(NBTConstants.ENTITY_DATA).getString("id")).ifPresent(type -> {
            Entity entity = type.create(worldIn);
            if (entity != null) {
                entity.load(tag);
                loadEntity(pos, context, entity, worldIn, player);
            }
        });
        if (listNBT.isEmpty()) stack.setTag(null);
        else {
            CompoundNBT nbt = stack.getTag();
            nbt.put(NBTConstants.NBT_BEES, listNBT);
            stack.setTag(nbt);
        }
    }

    private void releaseAll(World worldIn, ItemStack stack, BlockPos pos, ItemUseContext context, PlayerEntity player) {
        List<Entity> entities = getEntitiesFromStack(stack, worldIn, true);
        for (Entity entity : entities) {
            if (entity != null) {
                loadEntity(pos, context, entity, worldIn, player);
            }
        }
        if (isTemp) stack.shrink(1);
        else stack.setTag(null);
    }

    private ActionResultType stealBees(World worldIn, ItemStack stack, BlockPos pos) {
        if (isTemp) return ActionResultType.FAIL;
        TileEntity tile = worldIn.getBlockEntity(pos);

        ListNBT list;
        if (stack.getTag() != null && stack.getTag().contains(NBTConstants.NBT_BEES)) {
            list = stack.getTag().getList(NBTConstants.NBT_BEES, Constants.NBT.TAG_COMPOUND);
        } else {
            list = new ListNBT();
        }

        int space = BeeConstants.MAX_BEES_BEE_BOX - list.size();

        if (tile instanceof ApiaryTileEntity) {
            ApiaryTileEntity apiary = (ApiaryTileEntity) tile;
            Map<String, ApiaryTileEntity.ApiaryBee> bees = apiary.bees;
            for (int i = 0; i < space; i++) {
                if (apiary.bees.isEmpty()) break;
                ApiaryTileEntity.ApiaryBee bee = bees.remove(bees.entrySet().iterator().next().getKey());
                CompoundNBT nbt = new CompoundNBT();
                nbt.put(NBTConstants.ENTITY_DATA, bee.entityData);
                list.add(nbt);
            }
        } else if (tile instanceof BeehiveTileEntity) {
            BeehiveTileEntity hive = (BeehiveTileEntity) tile;
            for (int i = 0; i < space; i++) {
                Iterator<BeehiveTileEntity.Bee> beeIterator = hive.stored.listIterator();
                if (beeIterator.hasNext()) {
                    BeehiveTileEntity.Bee bee = beeIterator.next();
                    beeIterator.remove();
                    CompoundNBT nbt = new CompoundNBT();
                    nbt.put(NBTConstants.ENTITY_DATA, bee.entityData);
                    list.add(nbt);
                } else break;
            }
        }
        CompoundNBT nbt = stack.getTag() == null ? new CompoundNBT() : stack.getTag();
        nbt.put(NBTConstants.NBT_BEES, list);
        stack.setTag(nbt);
        return ActionResultType.SUCCESS;
    }

    private void loadEntity(BlockPos pos, ItemUseContext context, Entity entity, World worldIn, PlayerEntity player) {
        BlockPos blockPos = pos.relative(context.getClickedFace());
        entity.absMoveTo(blockPos.getX() + 0.5, blockPos.getY(), blockPos.getZ() + 0.5, 0, 0);
        worldIn.addFreshEntity(entity);
        if (entity instanceof BeeEntity) {
            BeeEntity beeEntity = (BeeEntity) entity;
            BeeJar.resetBee(beeEntity);
            BeeJar.setBeeAngry(beeEntity, player);
        }
    }

    public @NotNull List<Entity> getEntitiesFromStack(ItemStack stack, World world, boolean withInfo) {
        CompoundNBT tag = stack.getTag();
        List<Entity> entities = new ArrayList<>();
        if (tag != null) {
            ListNBT bees = tag.getList(NBTConstants.NBT_BEES, 10);
            bees.stream()
                    .map(CompoundNBT.class::cast)
                    .forEach(compoundNBT -> {
                        EntityType<?> type = EntityType.byString(compoundNBT.getCompound(NBTConstants.ENTITY_DATA).getString("id")).orElse(null);
                        if (type != null) {
                            Entity entity = type.create(world);
                            if (entity != null && withInfo)
                                entity.load(compoundNBT.getCompound(NBTConstants.ENTITY_DATA));
                            entities.add(entity);
                        }
                    });
        }
        return entities;
    }

    @NotNull
    @Override
    public ActionResultType interactLivingEntity(@NotNull ItemStack stack, @NotNull PlayerEntity player, LivingEntity targetIn, @NotNull Hand hand) {
        if (targetIn.getCommandSenderWorld().isClientSide() || (!(targetIn instanceof BeeEntity) || !targetIn.isAlive())) {
            return ActionResultType.FAIL;
        }
        if (isTemp) return ActionResultType.FAIL;
        BeeEntity target = (BeeEntity) targetIn;

        CompoundNBT tag = stack.getTag() == null
                ? new CompoundNBT()
                : stack.getTag();

        ListNBT bees = tag.contains(NBTConstants.NBT_BEES)
                ? tag.getList(NBTConstants.NBT_BEES, 10)
                : new ListNBT();

        if (bees.size() == BeeConstants.MAX_BEES_BEE_BOX) return ActionResultType.FAIL;
        CompoundNBT entityData = new CompoundNBT();
        entityData.put(NBTConstants.ENTITY_DATA, BeeInfoUtils.createJarBeeTag(target, NBTConstants.NBT_ID));
        bees.add(entityData);
        tag.put(NBTConstants.NBT_BEES, bees);
        stack.setTag(tag);
        player.setItemInHand(hand, stack);
        player.swing(hand);
        target.remove(true);
        return ActionResultType.PASS;
    }

    public static boolean isFilled(ItemStack stack) {
        return !stack.isEmpty() && stack.hasTag() && stack.getTag() != null && stack.getTag().contains(NBTConstants.NBT_BEES);
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @org.jetbrains.annotations.Nullable World world, @NotNull List<ITextComponent> tooltip, @NotNull ITooltipFlag tooltipFlag) {
        super.appendHoverText(stack, world, tooltip, tooltipFlag);
        if (isTemp) {
            tooltip.add(new TranslationTextComponent(ITEM_DOT + ResourcefulBees.MOD_ID + ".information.bee_box.temp_info").withStyle(TextFormatting.GOLD));
        } else {
            tooltip.add(new TranslationTextComponent(ITEM_DOT + ResourcefulBees.MOD_ID + ".information.bee_box.info").withStyle(TextFormatting.GOLD));
        }
        if (BeeInfoUtils.isShiftPressed() && isFilled(stack)) {
            tooltip.add(new TranslationTextComponent(ITEM_DOT + ResourcefulBees.MOD_ID + ".information.bee_box.bees").withStyle(TextFormatting.YELLOW));

            //noinspection ConstantConditions
            stack.getTag().getList(NBTConstants.NBT_BEES, 10).stream()
                    .map(CompoundNBT.class::cast)
                    .forEach(compoundNBT -> {
                        String id = compoundNBT.getCompound(NBTConstants.ENTITY_DATA).getString("id");
                        EntityType<?> entityType = BeeInfoUtils.getEntityType(id);
                        ITextComponent name = entityType == null ? new TranslationTextComponent("NULL_NAME") : entityType.getDescription();
                        tooltip.add(new StringTextComponent("  - ").append(name).withStyle(TextFormatting.WHITE));
                    });
        } else if (isFilled(stack)) {
            tooltip.add(new TranslationTextComponent(ITEM_DOT + ResourcefulBees.MOD_ID + ".information.bee_box.more_info").withStyle(TextFormatting.YELLOW));
        }
    }
}
