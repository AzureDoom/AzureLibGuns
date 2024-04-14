package mod.azure.azurelibguns.fabric;

import mod.azure.azurelib.common.platform.Services;
import mod.azure.azurelibguns.CommonMod;
import mod.azure.azurelibguns.example.registry.ModItems;
import mod.azure.azurelibguns.example.registry.ModSounds;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.CreativeModeTabs;

public final class FabricLibMod implements ModInitializer, ClientModInitializer {

    @Override
    public void onInitialize() {
        if (Services.PLATFORM.isDevelopmentEnvironment()) {
            Registry.register(BuiltInRegistries.ITEM, CommonMod.modResource("peacemaker"), ModItems.PEACEMAKER);
            Registry.register(BuiltInRegistries.ITEM, CommonMod.modResource("redeemer"), ModItems.REDEEMER);
            Registry.register(BuiltInRegistries.ITEM, CommonMod.modResource("senator"), ModItems.SENATOR);
            Registry.register(BuiltInRegistries.ITEM, CommonMod.modResource("dagger"), ModItems.DAGGER);
            Registry.register(BuiltInRegistries.SOUND_EVENT, CommonMod.modResource("vigilance_fire"),
                    ModSounds.vigilanceFire.get());
            Registry.register(BuiltInRegistries.SOUND_EVENT, CommonMod.modResource("vigilance_reload"),
                    ModSounds.vigilanceReload.get());
            ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.COMBAT).register(entries -> {
                entries.accept(ModItems.PEACEMAKER);
                entries.accept(ModItems.REDEEMER);
                entries.accept(ModItems.SENATOR);
                entries.accept(ModItems.DAGGER);
            });
        }
        CommonMod.commonMod = new CommonMod();
    }

    @Override
    public void onInitializeClient() {
        CommonMod.commonMod = new CommonMod();
    }
}
