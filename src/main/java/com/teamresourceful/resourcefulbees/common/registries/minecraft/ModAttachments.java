package com.teamresourceful.resourcefulbees.common.registries.minecraft;

import com.teamresourceful.resourcefulbees.common.lib.constants.ModConstants;
import com.teamresourceful.resourcefulbees.common.resources.storage.beepedia.BeeDiscoveryData;
import com.teamresourceful.resourcefullib.common.exceptions.UtilityClassException;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.function.Supplier;

public class ModAttachments {

    private ModAttachments() throws UtilityClassException {
        throw new UtilityClassException();
    }

    public static final DeferredRegister<AttachmentType<?>> ATTACHMENTS = DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, ModConstants.MOD_ID);

    public static final Supplier<AttachmentType<BeeDiscoveryData>> BEE_DISCOVERY = ATTACHMENTS.register("bee_discovery", () -> AttachmentType.builder((Supplier<BeeDiscoveryData>) BeeDiscoveryData::new)
            .serialize(BeeDiscoveryData.CODEC)
            .copyOnDeath()
            .sync((holder, player) -> holder == player, BeeDiscoveryData.STREAM_CODEC)
            .build()
    );
}
