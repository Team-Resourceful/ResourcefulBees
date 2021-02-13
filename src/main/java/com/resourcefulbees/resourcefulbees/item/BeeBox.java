package com.resourcefulbees.resourcefulbees.item;

import com.resourcefulbees.resourcefulbees.ResourcefulBees;
import com.resourcefulbees.resourcefulbees.api.ICustomBee;
import com.resourcefulbees.resourcefulbees.lib.BeeConstants;
import com.resourcefulbees.resourcefulbees.lib.NBTConstants;
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
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class BeeBox extends Item {

    boolean isTemp;

    public BeeBox(Properties p_i48487_1_, boolean isTemp) {
        super(p_i48487_1_);
        this.isTemp = isTemp;
    }

    @Nonnull
    @Override
    public ActionResultType onItemUse(ItemUseContext context) {
        PlayerEntity player = context.getPlayer();
        if (player != null) {
            World playerWorld = context.getPlayer().getEntityWorld();
            ItemStack stack = context.getItem();
            if (!context.getPlayer().isSneaking()) return ActionResultType.FAIL;
            if (playerWorld.isRemote() || !isFilled(stack)) return ActionResultType.FAIL;
            World worldIn = context.getWorld();
            BlockPos pos = context.getPos();
            List<Entity> entities = getEntitiesFromStack(stack, worldIn, true);
            for (Entity entity : entities) {
                if (entity != null) {
                    BlockPos blockPos = pos.offset(context.getFace());
                    entity.setPositionAndRotation(blockPos.getX() + 0.5, blockPos.getY(), blockPos.getZ() + 0.5, 0, 0);
                    worldIn.addEntity(entity);
                    if (entity instanceof BeeEntity) {
                        BeeEntity beeEntity = (BeeEntity) entity;
                        beeEntity.hivePos = null;
                        beeEntity.flowerPos = null;
                    }
                }
            }
            if (isTemp) stack.shrink(1);
            else stack.setTag(null);
            return ActionResultType.SUCCESS;
        }
        return ActionResultType.FAIL;
    }

    @Nullable
    public List<Entity> getEntitiesFromStack(ItemStack stack, World world, boolean withInfo) {
        CompoundNBT tag = stack.getTag();
        List<Entity> entities = new ArrayList<>();
        if (tag != null) {
            ListNBT bees = tag.getList(NBTConstants.NBT_BEES, 10);
            List<CompoundNBT> beeNbt = bees.stream().map(b -> (CompoundNBT) b).collect(Collectors.toList());
            for (CompoundNBT bee : beeNbt) {
                EntityType<?> type = EntityType.byKey(bee.getCompound("EntityData").getString("id")).orElse(null);
                if (type != null) {
                    Entity entity = type.create(world);
                    if (entity != null && withInfo) entity.read(stack.getTag());
                    entities.add(entity);
                }
            }
        }
        return entities;
    }

    @Nonnull
    @Override
    public ActionResultType itemInteractionForEntity(@Nonnull ItemStack stack, @Nonnull PlayerEntity player, LivingEntity targetIn, @Nonnull Hand hand) {
        if (targetIn.getEntityWorld().isRemote() || (!(targetIn instanceof BeeEntity) || !targetIn.isAlive())) {
            return ActionResultType.FAIL;
        }
        if (isTemp) return ActionResultType.FAIL;
        BeeEntity target = (BeeEntity) targetIn;
        CompoundNBT tag = stack.getTag();
        if (tag == null) tag = new CompoundNBT();
        ListNBT bees;
        if (tag.contains(NBTConstants.NBT_BEES)) {
            bees = tag.getList(NBTConstants.NBT_BEES, 10);
        } else {
            bees = new ListNBT();
        }
        if (bees.size() == BeeConstants.MAX_BEES_BEE_BOX) return ActionResultType.FAIL;
        CompoundNBT entityData = new CompoundNBT();
        entityData.put("EntityData", createTag(target));
        bees.add(entityData);
        tag.put(NBTConstants.NBT_BEES, bees);
        stack.setTag(tag);
        player.setHeldItem(hand, stack);
        player.swingArm(hand);
        target.remove(true);
        return ActionResultType.PASS;
    }

    public static CompoundNBT createTag(BeeEntity beeEntity) {
        String type = EntityType.getKey(beeEntity.getType()).toString();
        CompoundNBT nbt = new CompoundNBT();
        nbt.putString("id", type);
        beeEntity.writeWithoutTypeId(nbt);

        String beeColor = BeeConstants.VANILLA_BEE_COLOR;

        if (beeEntity instanceof ICustomBee) {
            ICustomBee iCustomBee = (ICustomBee) beeEntity;
            nbt.putString(NBTConstants.NBT_BEE_TYPE, iCustomBee.getBeeType());
            if (iCustomBee.getBeeData().getColorData().hasPrimaryColor()) {
                beeColor = iCustomBee.getBeeData().getColorData().getPrimaryColor();
            } else if (iCustomBee.getBeeData().getColorData().isRainbowBee()) {
                beeColor = BeeConstants.RAINBOW_COLOR;
            } else if (iCustomBee.getBeeData().getColorData().hasHoneycombColor()) {
                beeColor = iCustomBee.getBeeData().getColorData().getHoneycombColor();
            }
        }

        nbt.putString(NBTConstants.NBT_COLOR, beeColor);

        return nbt;
    }


    public static boolean isFilled(ItemStack stack) {
        return !stack.isEmpty() && stack.hasTag() && stack.getTag() != null && stack.getTag().contains(NBTConstants.NBT_BEES);
    }

    @Override
    public void addInformation(ItemStack stack, @org.jetbrains.annotations.Nullable World world, List<ITextComponent> tooltip, ITooltipFlag tooltipFlag) {
        super.addInformation(stack, world, tooltip, tooltipFlag);
        if (isTemp) {
            tooltip.add(new TranslationTextComponent("item." + ResourcefulBees.MOD_ID + ".information.bee_box.temp_info").formatted(TextFormatting.GOLD));
        } else {
            tooltip.add(new TranslationTextComponent("item." + ResourcefulBees.MOD_ID + ".information.bee_box.info").formatted(TextFormatting.GOLD));
        }
        if (BeeInfoUtils.isShiftPressed() && isFilled(stack)) {
            List<CompoundNBT> bees = stack.getTag().getList(NBTConstants.NBT_BEES, 10).stream().map(b -> (CompoundNBT) b).collect(Collectors.toList());
            tooltip.add(new TranslationTextComponent("item." + ResourcefulBees.MOD_ID + ".information.bee_box.bees").formatted(TextFormatting.YELLOW));
            for (CompoundNBT bee : bees) {
                String id = bee.getCompound("EntityData").getString("id");
                String translation = ForgeRegistries.ENTITIES.getValue(new ResourceLocation(id)).getTranslationKey();
                tooltip.add(new StringTextComponent("  - ").append(new TranslationTextComponent(translation)).formatted(TextFormatting.WHITE));
            }
        } else if (isFilled(stack)) {
            tooltip.add(new TranslationTextComponent("item." + ResourcefulBees.MOD_ID + ".information.bee_box.more_info").formatted(TextFormatting.YELLOW));
        }
    }
}
