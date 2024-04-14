package mod.azure.azurelibguns.example.registry;

import mod.azure.azurelibguns.CommonMod;
import net.minecraft.sounds.SoundEvent;

import java.util.function.Supplier;

public class ModSounds {

    public static final Supplier<SoundEvent> vigilanceFire = soundEventSupplier("vigilance_fire");
    public static final Supplier<SoundEvent> vigilanceReload = soundEventSupplier("vigilance_reload");

    static Supplier<SoundEvent> soundEventSupplier(String id) {
        return () -> SoundEvent.createVariableRangeEvent(CommonMod.modResource(id));
    }
}
