package mod.azure.azurelibguns.example;

import mod.azure.azurelibguns.api.common.GunData;
import mod.azure.azurelibguns.example.registry.ModSounds;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.item.Items;

import java.util.Set;

public record WeaponTypes() {
    public static final GunData PEACEMAKER = new GunData("peacemaker",
            Set.of(Constants.semiAutomatic), // Fire Modes
            Set.of(Constants.ballistic), // DamageTypes
            Items.DIRT, // Reload Item
            15, // mag size
            5F, // damage
            5, // firerate
            128, //range
            1f, // recoilX
            2.5f, // recoilY
            33, // Reload timer in ticks
            ModSounds.vigilanceFire.get(), // Firing sound
            ModSounds.vigilanceReload.get(), // Reload sound
            ParticleTypes.DUST_PLUME); // Particle for firing

    public static final GunData REDEEMER = new GunData("redeemer",
            Set.of(Constants.burst, Constants.semiAutomatic, Constants.automatic),
            Set.of(Constants.ballistic), Items.DIRT, 31, 5F, 3, 128, 0.5f, 1.25f, 44, ModSounds.vigilanceFire.get(),
            ModSounds.vigilanceReload.get(), ParticleTypes.DUST_PLUME);

    public static final GunData SENATOR = new GunData("senator", Set.of(Constants.single), Set.of(Constants.ballistic),
            Items.DIRT, 6, 10F,
            15, 128, 1.5f, 3.25f, 44, ModSounds.vigilanceFire.get(), ModSounds.vigilanceReload.get(),
            ParticleTypes.DUST_PLUME);

    public static final GunData DAGGER = new GunData("dagger", Set.of(Constants.automatic), Set.of(Constants.energy),
            Items.DIRT, 30, 1F,
            0, 128, 0.15f, 0.15f, 44, null, ModSounds.vigilanceReload.get(),
            ParticleTypes.ELECTRIC_SPARK);
}
