package mod.azure.azurelibguns.api.common;

import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.item.Item;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

public record GunData(String gunName, Set<FireMode> allowedFireModes, Set<DamageType> damageTypes, @Nullable Item reloadItem, int magSize, float damage,
                      int fireRate, double maxRange, float recoilX, float recoilY, int reloadTicks, SoundEvent fireSound, SoundEvent reloadSound,
                      @Nullable SimpleParticleType tracerParticle) {

}
