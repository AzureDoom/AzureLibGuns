package mod.azure.azurelibguns.api.common.functions;

import commonnetwork.api.Network;
import mod.azure.azurelib.common.api.client.helper.ClientUtils;
import mod.azure.azurelibguns.api.common.GunData;
import mod.azure.azurelibguns.core.AimingPacket;
import mod.azure.azurelibguns.core.WeaponModePacket;
import mod.azure.azurelibguns.example.Constants;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public record GunFunctions() {

    public static void spawnBeam(Level level, Player player, double maxRange, ParticleOptions particleTypes, boolean isAiming) {
        var bodyYawToRads = Math.toRadians(Objects.requireNonNull((LivingEntity) player).yBodyRot);
        var radius = isAiming ? -0.15D : 0.0D + 5;
        var playerX = player.getX() + radius * Math.cos(bodyYawToRads);
        var playerY = player.getY(0.8);
        var playerZ = player.getZ() + radius * Math.sin(bodyYawToRads);

        // Calculate beam direction
        var lookVector = player.getLookAngle();
        var arrowX = playerX + lookVector.x * 10; // Adjust distance as needed
        var arrowY = playerY + lookVector.y * 10; // Adjust distance as needed
        var arrowZ = playerZ + lookVector.z * 10; // Adjust distance as needed

        // Calculate direction vector between player and target
        var directionX = arrowX - playerX;
        var directionY = arrowY - playerY;
        var directionZ = arrowZ - playerZ;

        // Calculate step values for particle interpolation
        var stepX = directionX / 15; // 10 particles between player and arrow
        var stepY = directionY / 15;
        var stepZ = directionZ / 15;

        // Spawn custom particles between player and target position
        for (var i = 0; i < maxRange; i++) {
            var offsetX = level.random.nextDouble() * 0.2 - 0.1; // Randomize X offset
            var offsetY = level.random.nextDouble() * 0.2 - 0.1; // Randomize Y offset
            var offsetZ = level.random.nextDouble() * 0.2 - 0.1; // Randomize Z offset

            var particleX = playerX + stepX * i + offsetX;
            var particleY = playerY + stepY * i + offsetY;
            var particleZ = playerZ + stepZ * i + offsetZ;

            level.addParticle(particleTypes, particleX, particleY, particleZ, 0, 0, 0);
        }
    }

    public static void spawnBulletTracer(Level level, Player player, double maxRange, ParticleOptions particleTypes) {

        // Calculate end coordinates based on player's viewing direction
        var pitch = Math.toRadians(player.getXRot()); // Player's pitch (vertical viewing angle)
        var yaw = Math.toRadians(player.getYRot()); // Player's yaw (horizontal viewing angle)

        // Calculate the direction vector components
        var cosPitch = Math.cos(pitch);
        var xDirection = -Math.sin(yaw) * cosPitch; // Negate X for Minecraft's coordinate system
        var yDirection = -Math.sin(pitch); // Negate Y for Minecraft's coordinate system
        var zDirection = Math.cos(yaw) * cosPitch;

        var endX = player.getX() + maxRange * xDirection;
        var endY = player.getEyeY() + maxRange * yDirection;
        var endZ = player.getZ() + maxRange * zDirection;

        var deltaX = (endX - player.getX()) / maxRange; // X increment per step
        var deltaY = (endY - player.getEyeY()) / maxRange; // Y increment per step
        var deltaZ = (endZ - player.getZ()) / maxRange; // Z increment per step
        for (var i = 0; i < maxRange; i++) {
            var posX = player.getX() + (i + 1) * deltaX; // Adjusted to spawn 1 block in front
            var posY = player.getEyeY() + (i + 1) * deltaY; // Adjusted to spawn 1 block in front
            var posZ = player.getZ() + (i + 1) * deltaZ; // Adjusted to spawn 1 block in front
            // Spawn tracer particles at each step along the trajectory
            level.addParticle(particleTypes, posX, posY, posZ, 0, 0, 0);
        }
    }

    public static float getRecoilX(Player player, boolean isAiming, GunData gunData) {
        var baseRecoilX = player.level().getRandom().nextBoolean() ? gunData.recoilX() : -gunData.recoilX();
        return isAiming ? baseRecoilX / 2 : baseRecoilX;
    }

    public static float getRecoilY(boolean isAiming, GunData gunData) {
        return isAiming ? gunData.recoilY() / 2 : gunData.recoilY();
    }

    public static void damageEntity(@NotNull Level level, Player player, HitResult hitResult, GunData gunData) {
        if (hitResult.getType() == HitResult.Type.ENTITY && ((EntityHitResult) hitResult).getEntity() instanceof LivingEntity livingEntity) {
            livingEntity.invulnerableTime = 0;
            if (livingEntity.getArmorValue() > 0.5F) {
                livingEntity.getArmorSlots().forEach(itemStack -> itemStack.hurtAndBreak((int) gunData.damage(), player,
                        player1 -> player1.broadcastBreakEvent(LivingEntity.getEquipmentSlotForItem(itemStack))));
                livingEntity.hurt(level.damageSources().magic(),
                        gunData.damageTypes().iterator().next().isarmorPiercing() ? gunData.damage() : gunData.damage() / 2.0f);
            } else {
                livingEntity.hurt(level.damageSources().playerAttack(player), gunData.damage());
            }
        }
    }

    public static void removeAmmo(Item ammo, Player player) {
        if (!player.isCreative()) {
            for (var item : player.getInventory().offhand) {
                if (item.getItem() == ammo) {
                    item.shrink(1);
                    break;
                }
                for (var item1 : player.getInventory().items) {
                    if (item1.getItem() == ammo) {
                        item1.shrink(1);
                        break;
                    }
                }
            }
        }
    }

    public static void tagCreationBackup(CompoundTag compoundTag, GunData gunData, boolean isReloadAnimationPlaying, boolean selected) {
        if (!compoundTag.contains(Constants.AMMO_STRING)) {
            compoundTag.putInt(Constants.AMMO_STRING, gunData.magSize());
        }
        if (!compoundTag.contains(Constants.FIREMODES_STRING)) {
            compoundTag.putInt(Constants.FIREMODES_STRING,
                    gunData.allowedFireModes().iterator().next().getName().hashCode());
        }
        if (compoundTag.getBoolean(Constants.RELOADING_STRING) && isReloadAnimationPlaying && selected) {
            compoundTag.putBoolean(Constants.RELOADING_STRING, false);
        }
        if (!compoundTag.contains(Constants.DAMAGETYPE_STRING)) {
            compoundTag.putInt(Constants.DAMAGETYPE_STRING,
                    gunData.damageTypes().iterator().next().getName().hashCode());
        }
    }

    public static void sendClientPackets() {
        if (ClientUtils.SCOPE.consumeClick()) {
            Network.getNetworkHandler().sendToServer(new AimingPacket());
        }
        if (ClientUtils.FIRE_WEAPON.consumeClick()) {
            Network.getNetworkHandler().sendToServer(new WeaponModePacket());
        }
    }
}
