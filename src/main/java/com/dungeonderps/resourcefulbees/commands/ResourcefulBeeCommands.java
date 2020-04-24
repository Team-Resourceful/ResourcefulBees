        package com.dungeonderps.resourcefulbees.commands;

        import com.dungeonderps.resourcefulbees.ItemGroupResourcefulBees;
        import com.dungeonderps.resourcefulbees.config.ResourcefulBeesConfig;
        import com.mojang.brigadier.Command;
        import com.mojang.brigadier.CommandDispatcher;
        import net.minecraft.command.CommandSource;
        import net.minecraft.command.Commands;
        import net.minecraft.entity.passive.CustomBeeEntity;
        import net.minecraft.entity.player.ServerPlayerEntity;
        import net.minecraft.item.ItemStack;
        import net.minecraft.util.Hand;
        import net.minecraft.util.ResourceLocation;
        import net.minecraft.util.text.ITextComponent;
        import net.minecraft.util.text.StringTextComponent;
        import net.minecraft.util.text.TextFormatting;
        import net.minecraft.util.text.event.ClickEvent;
        import net.minecraftforge.fml.loading.FMLPaths;
        import org.apache.commons.io.FileUtils;

        import java.io.IOException;
        import java.nio.file.FileAlreadyExistsException;
        import java.nio.file.Files;
        import java.nio.file.Paths;
        import java.util.ArrayList;
        import java.util.List;

        import static com.dungeonderps.resourcefulbees.ResourcefulBees.LOGGER;

public class ResourcefulBeeCommands
{
    public static void register(CommandDispatcher<CommandSource> dispatcher)
    {
        LOGGER.info("got to command dispatcher");
        dispatcher.register(Commands.literal("resourcefulbees")
                .then(Commands.literal("getItem")
                        .executes(context -> itemInfo(context.getSource().asPlayer()))
                )
                .then(Commands.literal("reloadBees")
                        .executes(context -> reloadBees(context.getSource().asPlayer()))
                )
        );
    }

    private static int itemInfo(ServerPlayerEntity player)
    {
        //TODO Update this with better way of sending data, the ability to click on it to copy to clipboard, and get item tags.
        ItemStack stack = player.getHeldItem(Hand.MAIN_HAND);
        player.sendMessage(new StringTextComponent("Item: " + stack.getItem().getRegistryName().toString()));
        return Command.SINGLE_SUCCESS;
    }

    private static int reloadBees(ServerPlayerEntity player) {
        //TODO Make it so only OPs can do this command right now anyone can :MonkaS:
        try {
            FileUtils.deleteDirectory(Paths.get(FMLPaths.CONFIGDIR.get().toString(), "resourcefulbees", "resources", "datapack", "data", "resourcefulbees","recipes").toFile());
        } catch (IOException e) {
            LOGGER.error("Failled to delete recipe directory.");
        }
        try { Files.createDirectories(ResourcefulBeesConfig.RECIPE_PATH);
        } catch (FileAlreadyExistsException e) { // do nothing
        } catch (IOException e) { LOGGER.error("Failed to create recipes directory");}
        ItemGroupResourcefulBees.bees.clear();
        CustomBeeEntity.BEE_INFO.clear();
        ResourcefulBeesConfig.addBees();
        player.getServer().reload();
        player.sendMessage(new StringTextComponent("RELOAD!, New bees added!").applyTextStyle(TextFormatting.RED));
        return Command.SINGLE_SUCCESS;
    }
}