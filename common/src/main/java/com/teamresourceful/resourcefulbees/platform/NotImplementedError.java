package com.teamresourceful.resourcefulbees.platform;

import dev.architectury.injectables.targets.ArchitecturyTarget;

public class NotImplementedError extends RuntimeException {
    public NotImplementedError() {
        super("Not implemented on " + ArchitecturyTarget.getCurrentTarget());
    }
}
