package com.teamresourceful.resourcefulbees.common.fabric;

public interface ReloadableServerResourcesHook {

    void rbees$setupReloadableServerResources(boolean settingUp);

    boolean rbees$isReloadableServerResourcesSettingUp();

}
