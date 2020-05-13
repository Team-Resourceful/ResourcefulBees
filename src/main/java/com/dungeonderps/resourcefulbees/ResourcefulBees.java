package com.dungeonderps.resourcefulbees;

import com.dungeonderps.resourcefulbees.block.IronBeehiveBlock;
import com.dungeonderps.resourcefulbees.commands.ResourcefulBeeCommands;
import com.dungeonderps.resourcefulbees.compat.top.TopCompat;
import com.dungeonderps.resourcefulbees.config.BeeInfo;
import com.dungeonderps.resourcefulbees.config.ResourcefulBeesConfig;
import com.dungeonderps.resourcefulbees.data.DataGen;
import com.dungeonderps.resourcefulbees.data.RecipeBuilder;
import com.dungeonderps.resourcefulbees.entity.CustomBeeRenderer;
import com.dungeonderps.resourcefulbees.loot.function.BlockItemFunction;
import com.dungeonderps.resourcefulbees.screen.CentrifugeScreen;
import com.dungeonderps.resourcefulbees.utils.ColorHandler;
import net.minecraft.block.BeehiveBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.DispenserBlock;
import net.minecraft.client.gui.ScreenManager;
import net.minecraft.dispenser.IBlockSource;
import net.minecraft.dispenser.OptionalDispenseBehavior;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntitySpawnPlacementRegistry;
import net.minecraft.entity.passive.CustomBeeEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.resources.IReloadableResourceManager;
import net.minecraft.resources.IResourceManager;
import net.minecraft.tags.BlockTags;
import net.minecraft.tileentity.BeehiveTileEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.village.PointOfInterestType;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.storage.loot.functions.LootFunctionManager;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.InterModComms;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.fml.event.server.FMLServerAboutToStartEvent;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLPaths;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

@Mod("resourcefulbees")
public class ResourcefulBees
{
    public static final String MOD_ID = "resourcefulbees";

    public static final Logger LOGGER = LogManager.getLogger();

    public ResourcefulBees() {
        try {
            FileUtils.deleteDirectory(Paths.get(FMLPaths.CONFIGDIR.get().toString(), "resourcefulbees", "resources", "datapack", "data", "resourcefulbees","recipes").toFile());
        } catch (IOException e) {
            LOGGER.error("Failed to delete recipe directory.");
        }
        ResourcefulBeesConfig.setup();

        RegistryHandler.init();

        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(DataGen::gatherData);

        MinecraftForge.EVENT_BUS.addListener(this::serverStarting);
        DistExecutor.runWhenOn(Dist.DEDICATED_SERVER, () -> () -> {
            MinecraftForge.EVENT_BUS.addListener(EventPriority.HIGH, true, this::OnServerSetup);
        });
        DistExecutor.runWhenOn(Dist.CLIENT, () -> () -> {
            FMLJavaModLoadingContext.get().getModEventBus().addListener(this::onInterModEnqueue);
            FMLJavaModLoadingContext.get().getModEventBus().addListener(this::doClientStuff);
            FMLJavaModLoadingContext.get().getModEventBus().addListener(ColorHandler::onItemColors);
            FMLJavaModLoadingContext.get().getModEventBus().addListener(ColorHandler::onBlockColors);
        });


        
        MinecraftForge.EVENT_BUS.register(this);
    }


    public void OnServerSetup(FMLServerAboutToStartEvent event){
        LOGGER.info("recipe should be loaded");
        IResourceManager manager = event.getServer().getResourceManager();
        if (manager instanceof IReloadableResourceManager) {
            IReloadableResourceManager reloader = (IReloadableResourceManager)manager;
            reloader.addReloadListener(new RecipeBuilder());
        }
    }

    private void setup(final FMLCommonSetupEvent event){
        /*
        The 3 lines below are necessary for getting mod bees into mod beehive.
        We're basically pushing the mod data into the minecraft POI list
        because forge POI doesn't seem to have any impact.
        Not entirely sure if forge registered POI is even necessary
         */
        Map<BlockState, PointOfInterestType> pointOfInterestTypeMap = new HashMap<>();
        RegistryHandler.IRON_BEEHIVE.get().getStateContainer().getValidStates().forEach(blockState -> pointOfInterestTypeMap.put(blockState, RegistryHandler.IRON_BEEHIVE_POI.get()));
        PointOfInterestType.POIT_BY_BLOCKSTATE.putAll(pointOfInterestTypeMap);

        LootFunctionManager.registerFunction(new BlockItemFunction.Serializer());

        addDispenser();
        addBeeToSpawnList();
    }
    public void onInterModEnqueue(InterModEnqueueEvent event) {
        if (ModList.get().isLoaded("theoneprobe"))
            InterModComms.sendTo("theoneprobe", "getTheOneProbe", TopCompat::new);
    }

    private void serverStarting(FMLServerStartingEvent event) {
        ResourcefulBeeCommands.register(event.getCommandDispatcher());
    }

    private void doClientStuff(final FMLClientSetupEvent event) {
        //new ItemGroupResourcefulBees();
        RenderingRegistry.registerEntityRenderingHandler(RegistryHandler.CUSTOM_BEE.get(), CustomBeeRenderer::new);
        ScreenManager.registerFactory(RegistryHandler.CENTRIFUGE_CONTAINER.get(), CentrifugeScreen::new);
    }

    public static void addDispenser() {
        DispenserBlock.registerDispenseBehavior(Items.SHEARS.asItem(), new OptionalDispenseBehavior() {
            protected ItemStack dispenseStack(IBlockSource source, ItemStack stack) {
                World world = source.getWorld();
                if (!world.isRemote()) {
                    this.successful = false;
                    BlockPos blockpos = source.getBlockPos().offset(source.getBlockState().get(DispenserBlock.FACING));

                    for(net.minecraft.entity.Entity entity : world.getEntitiesInAABBexcluding(null, new AxisAlignedBB(blockpos), e -> !e.isSpectator() && e instanceof net.minecraftforge.common.IShearable)) {
                        net.minecraftforge.common.IShearable target = (net.minecraftforge.common.IShearable)entity;
                        if (target.isShearable(stack, world, blockpos)) {
                            java.util.List<ItemStack> drops = target.onSheared(stack, entity.world, blockpos,
                                    net.minecraft.enchantment.EnchantmentHelper.getEnchantmentLevel(net.minecraft.enchantment.Enchantments.FORTUNE, stack));
                            java.util.Random rand = new java.util.Random();
                            drops.forEach(d -> {
                                net.minecraft.entity.item.ItemEntity ent = entity.entityDropItem(d, 1.0F);
                                ent.setMotion(ent.getMotion().add((rand.nextFloat() - rand.nextFloat()) * 0.1F, rand.nextFloat() * 0.05F, (rand.nextFloat() - rand.nextFloat()) * 0.1F));
                            });
                            if (stack.attemptDamageItem(1, world.rand, null)) {
                                stack.setCount(0);
                            }

                            this.successful = true;
                            break;
                        }
                    }

                    if (!this.successful) {
                        BlockState blockstate = world.getBlockState(blockpos);
                        if (blockstate.isIn(BlockTags.BEEHIVES)) {
                            int i = blockstate.get(BeehiveBlock.HONEY_LEVEL);
                            if (i >= 5) {
                                if (stack.attemptDamageItem(1, world.rand, null)) {
                                    stack.setCount(0);
                                }

                                BeehiveBlock.dropHoneyComb(world, blockpos);
                                ((BeehiveBlock)blockstate.getBlock()).takeHoney(world, blockstate, blockpos, null, BeehiveTileEntity.State.BEE_RELEASED);
                                this.successful = true;
                            }
                        }
                        else if (blockstate.getBlock() instanceof IronBeehiveBlock) {
                            int i = blockstate.get(BeehiveBlock.HONEY_LEVEL);
                            if (i >= 5) {
                                if (stack.attemptDamageItem(1, world.rand, null)) {
                                    stack.setCount(0);
                                }

                                IronBeehiveBlock.dropResourceHoneyComb((IronBeehiveBlock) blockstate.getBlock(), world, blockpos);
                                ((BeehiveBlock) blockstate.getBlock()).takeHoney(world, blockstate, blockpos, null,
                                        BeehiveTileEntity.State.BEE_RELEASED);
                                this.successful = true;
                            }
                        }
                    }
                }
                return stack;
            }
        });
    }

    public static void addBeeToSpawnList() {
        Iterator<Map.Entry<Biome, Set<String>>> spawnableBiomesIterator = BeeInfo.SPAWNABLE_BIOMES.entrySet().iterator();
        while (spawnableBiomesIterator.hasNext()) {
            Map.Entry<Biome, Set<String>> element = spawnableBiomesIterator.next();
            Biome biome = element.getKey();
            biome.getSpawns(EntityClassification.CREATURE).add(new Biome.SpawnListEntry(RegistryHandler.CUSTOM_BEE.get(),20,3,30));
        }

        EntitySpawnPlacementRegistry.register(RegistryHandler.CUSTOM_BEE.get(), EntitySpawnPlacementRegistry.PlacementType.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, CustomBeeEntity::canBeeSpawn);
    }
}
