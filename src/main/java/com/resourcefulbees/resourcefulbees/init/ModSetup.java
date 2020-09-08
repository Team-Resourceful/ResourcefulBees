package com.resourcefulbees.resourcefulbees.init;

import com.resourcefulbees.resourcefulbees.ResourcefulBees;
import com.resourcefulbees.resourcefulbees.block.TieredBeehiveBlock;
import com.resourcefulbees.resourcefulbees.utils.RainbowColor;
import net.minecraft.block.BeehiveBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.DispenserBlock;
import net.minecraft.client.Minecraft;
import net.minecraft.dispenser.IBlockSource;
import net.minecraft.dispenser.OptionalDispenseBehavior;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.resources.FolderPack;
import net.minecraft.resources.IPackFinder;
import net.minecraft.resources.IPackNameDecorator;
import net.minecraft.resources.ResourcePackInfo;
import net.minecraft.tags.BlockTags;
import net.minecraft.tileentity.BeehiveTileEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.common.util.FakePlayerFactory;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.loading.FMLPaths;

import javax.annotation.Nonnull;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.function.Consumer;

import static com.resourcefulbees.resourcefulbees.ResourcefulBees.LOGGER;
import static com.resourcefulbees.resourcefulbees.config.BeeSetup.BEE_PATH;
import static com.resourcefulbees.resourcefulbees.config.BeeSetup.RESOURCE_PATH;

public class ModSetup {

    public static void initialize(){
        setupPaths();
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> ModSetup::loadResources);
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> RainbowColor::init);
    }

    private static void setupPaths(){
        Path configPath = FMLPaths.CONFIGDIR.get();
        Path rbBeesPath = Paths.get(configPath.toAbsolutePath().toString(), ResourcefulBees.MOD_ID, "bees");
        Path rbAssetsPath = Paths.get(configPath.toAbsolutePath().toString(),ResourcefulBees.MOD_ID, "resources");
        BEE_PATH = rbBeesPath;
        RESOURCE_PATH = rbAssetsPath;

        try { Files.createDirectories(rbBeesPath);
        } catch (FileAlreadyExistsException ignored) {
        } catch (IOException e) { LOGGER.error("failed to create resourcefulbees config directory");}

        try { Files.createDirectory(rbAssetsPath);
        } catch (FileAlreadyExistsException ignored) {
        } catch (IOException e) { LOGGER.error("Failed to create assets directory");}

        try {
            FileWriter file = new FileWriter(Paths.get(rbAssetsPath.toAbsolutePath().toString(), "pack.mcmeta").toFile());
            String mcMetaContent = "{\"pack\":{\"pack_format\":6,\"description\":\"Resourceful Bees resource pack used for lang purposes for the user to add lang for bee/items.\"}}";
            file.write(mcMetaContent);
            file.close();
        } catch (FileAlreadyExistsException ignored){
        } catch (IOException e) { LOGGER.error("Failed to create pack.mcmeta file for resource loading");}
    }

    public static void setupDispenserCollectionBehavior() {
        DispenserBlock.registerDispenseBehavior(Items.SHEARS.asItem(), new OptionalDispenseBehavior() {
            @Nonnull
            protected ItemStack dispenseStack(@Nonnull IBlockSource source, @Nonnull ItemStack stack) {
                World world = source.getWorld();
                if (!world.isRemote()) {
                    this.setSuccess(false);
                    BlockPos blockpos = source.getBlockPos().offset(source.getBlockState().get(DispenserBlock.FACING));

                    for(net.minecraft.entity.Entity entity : world.getEntitiesInAABBexcluding(null, new AxisAlignedBB(blockpos), e -> !e.isSpectator() && e instanceof net.minecraftforge.common.IForgeShearable)) {
                        net.minecraftforge.common.IForgeShearable target = (net.minecraftforge.common.IForgeShearable)entity;
                        if (target.isShearable(stack, world, blockpos)) {
                            FakePlayer fakie = FakePlayerFactory.getMinecraft((ServerWorld) world);
                            java.util.List<ItemStack> drops = target.onSheared(fakie, stack, entity.world, blockpos,
                                    net.minecraft.enchantment.EnchantmentHelper.getEnchantmentLevel(net.minecraft.enchantment.Enchantments.FORTUNE, stack));
                            java.util.Random rand = new java.util.Random();
                            drops.forEach(d -> {
                                net.minecraft.entity.item.ItemEntity ent = entity.entityDropItem(d, 1.0F);
                                if (ent != null)
                                    ent.setMotion(ent.getMotion().add((rand.nextFloat() - rand.nextFloat()) * 0.1F, rand.nextFloat() * 0.05F, (rand.nextFloat() - rand.nextFloat()) * 0.1F));
                            });
                            if (stack.attemptDamageItem(1, world.rand, null)) {
                                stack.setCount(0);
                            }

                            this.setSuccess(true);
                            break;
                        }
                    }

                    if (!this.isSuccess()) {
                        BlockState blockstate = world.getBlockState(blockpos);
                        if (blockstate.getBlock() instanceof TieredBeehiveBlock) {
                            int i = blockstate.get(BeehiveBlock.HONEY_LEVEL);
                            if (i >= 5) {
                                if (stack.attemptDamageItem(1, world.rand, null)) {
                                    stack.setCount(0);
                                }

                                TieredBeehiveBlock.dropResourceHoneycomb((TieredBeehiveBlock) blockstate.getBlock(), world, blockpos);
                                ((BeehiveBlock) blockstate.getBlock()).takeHoney(world, blockstate, blockpos, null,
                                        BeehiveTileEntity.State.BEE_RELEASED);
                                this.setSuccess(true);
                            }
                        } else if (blockstate.isIn(BlockTags.BEEHIVES)) {
                            int i = blockstate.get(BeehiveBlock.HONEY_LEVEL);
                            if (i >= 5) {
                                if (stack.attemptDamageItem(1, world.rand, null)) {
                                    stack.setCount(0);
                                }

                                BeehiveBlock.dropHoneycomb(world, blockpos);
                                ((BeehiveBlock)blockstate.getBlock()).takeHoney(world, blockstate, blockpos, null, BeehiveTileEntity.State.BEE_RELEASED);
                                this.setSuccess(true);
                            }
                        }
                    }
                }
                return stack;
            }
        });
    }

    public static void loadResources() {
        Minecraft.getInstance().getResourcePackList().addPackFinder((p_230230_1_, factory) -> {
            final ResourcePackInfo packInfo = ResourcePackInfo.createResourcePack(
                    ResourcefulBees.MOD_ID,
                    true,
                    () -> new FolderPack(RESOURCE_PATH.toFile()),
                    factory,
                    ResourcePackInfo.Priority.TOP,
                    IPackNameDecorator.method_29485()
            );
            if (packInfo == null) {
                LOGGER.error("Failed to load resource pack, some things may not work.");
                return;
            }
            p_230230_1_.accept(packInfo);
        });
    }
}
