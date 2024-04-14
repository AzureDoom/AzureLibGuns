package mod.azure.azurelibguns.neoforge;

import mod.azure.azurelib.common.platform.Services;
import mod.azure.azurelibguns.CommonMod;
import mod.azure.azurelibguns.example.registry.ModItems;
import mod.azure.azurelibguns.example.registry.ModSounds;
import net.minecraft.core.registries.Registries;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

//@Mod.EventBusSubscriber
@Mod(CommonMod.MOD_ID)
public final class NeoForgeMod {
    public static final DeferredRegister<SoundEvent> SOUNDS_DEFERRED_REGISTER = DeferredRegister.create(
            Registries.SOUND_EVENT, CommonMod.MOD_ID);
    public static final Supplier<SoundEvent> vigilanceFire = SOUNDS_DEFERRED_REGISTER.register("vigilance_fire",
            ModSounds.vigilanceFire);
    public static final Supplier<SoundEvent> vigilanceReload = SOUNDS_DEFERRED_REGISTER.register("vigilance_reload",
            ModSounds.vigilanceReload);

    public static final DeferredRegister<Item> ITEM_DEFERRED_REGISTER = DeferredRegister.create(Registries.ITEM,
            CommonMod.MOD_ID);
    public static final Supplier<Item> PEACEMAKER = ITEM_DEFERRED_REGISTER.register("peacemaker", () -> ModItems.PEACEMAKER);
    public static final Supplier<Item> REDEEMER = ITEM_DEFERRED_REGISTER.register("redeemer", () -> ModItems.REDEEMER);
    public static final Supplier<Item> SENATOR = ITEM_DEFERRED_REGISTER.register("senator", () -> ModItems.SENATOR);
    public static final Supplier<Item> DAGGER = ITEM_DEFERRED_REGISTER.register("dagger", () -> ModItems.DAGGER);

    public NeoForgeMod(IEventBus modEventBus) {
        CommonMod.commonMod = new CommonMod();
        if (Services.PLATFORM.isDevelopmentEnvironment()) {
            SOUNDS_DEFERRED_REGISTER.register(modEventBus);
            ITEM_DEFERRED_REGISTER.register(modEventBus);
            modEventBus.addListener(this::addCreativeTabs);
        }
    }

    public void addCreativeTabs(final BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.COMBAT) {
            event.accept(PEACEMAKER.get());
            event.accept(REDEEMER.get());
            event.accept(SENATOR.get());
            event.accept(DAGGER.get());
        }
    }
}
