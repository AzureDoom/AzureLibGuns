package mod.azure.azurelibguns.example;

import mod.azure.azurelib.common.api.client.helper.ClientUtils;
import mod.azure.azurelib.common.api.common.animatable.GeoItem;
import mod.azure.azurelib.common.internal.client.RenderProvider;
import mod.azure.azurelib.common.internal.common.animatable.SingletonGeoAnimatable;
import mod.azure.azurelib.common.internal.common.core.animatable.instance.AnimatableInstanceCache;
import mod.azure.azurelib.common.internal.common.core.animation.AnimatableManager;
import mod.azure.azurelib.common.internal.common.core.animation.Animation;
import mod.azure.azurelib.common.internal.common.core.animation.AnimationController;
import mod.azure.azurelib.common.internal.common.core.animation.RawAnimation;
import mod.azure.azurelib.common.internal.common.core.object.PlayState;
import mod.azure.azurelib.common.internal.common.util.AzureLibUtil;
import mod.azure.azurelibguns.api.client.GunRender;
import mod.azure.azurelibguns.api.client.functions.AnimationFunctions;
import mod.azure.azurelibguns.api.common.DamageType;
import mod.azure.azurelibguns.api.common.FireMode;
import mod.azure.azurelibguns.api.common.GunData;
import mod.azure.azurelibguns.api.common.functions.GunFunctions;
import mod.azure.azurelibguns.example.registry.ModSounds;
import net.minecraft.ChatFormatting;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import org.jetbrains.annotations.NotNull;

import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * TODO:
 * - Add example of single fire mode
 * - Add example of scoped weapon
 * - Add a system for setting a weapon being ballistic, energy, or explosive from the ammo item used
 * - Add a system for checking if damage type is isarmorPiercing and if so, damage armor of entity has it.
 * - Add example of a energy weapon
 * - Add example of a explosive weapon
 */
public abstract class BaseGunItem extends Item implements GeoItem {

    protected int burstTicks = 0;
    protected int beamTicks = 0;
    protected int attempts = 0;
    public final GunData gunData;

    public BaseGunItem(GunData gunData) {
        super(new Properties().stacksTo(1));
        this.gunData = gunData;
        SingletonGeoAnimatable.registerSyncedAnimatable(this);
    }

    public String getGunName() {
        return this.gunData.gunName();
    }

    public DamageType getCurrentDamageType(ItemStack stack) {
        var tag = stack.getOrCreateTag();
        if (tag.contains(Constants.DAMAGETYPE_STRING)) {
            var modeID = tag.getInt(Constants.DAMAGETYPE_STRING);
            for (var mode : gunData.damageTypes()) {
                if (mode.getName().hashCode() == modeID) {
                    return mode;
                }
            }
        }
        return this.gunData.damageTypes().iterator().next();
    }

    public FireMode getCurrentFireMode(ItemStack stack) {
        var tag = stack.getOrCreateTag();
        if (tag.contains(Constants.FIREMODES_STRING)) {
            var modeID = tag.getInt(Constants.FIREMODES_STRING);
            for (var mode : gunData.allowedFireModes()) {
                if (mode.getName().hashCode() == modeID) {
                    return mode;
                }
            }
        }
        return this.gunData.allowedFireModes().iterator().next();
    }

    public void cycleFireMode(ItemStack stack) {
        var currentMode = getCurrentFireMode(stack);
        if (currentMode == null) {
            currentMode = gunData.allowedFireModes().iterator().next();
        }
        var nextMode = currentMode;
        for (var mode : gunData.allowedFireModes()) {
            if (mode.equals(currentMode)) {
                break;
            }
            this.attempts++;
        }
        for (var i = 0; i < gunData.allowedFireModes().size(); i++) {
            nextMode = getNextMode(nextMode);
            if (gunData.allowedFireModes().contains(nextMode)) {
                break;
            }
            this.attempts++;
        }
        assert nextMode != null;
        stack.getOrCreateTag().putInt(Constants.FIREMODES_STRING, nextMode.getName().hashCode());
    }

    private FireMode getNextMode(FireMode currentMode) {
        Iterator<FireMode> iterator = gunData.allowedFireModes().iterator();
        FireMode firstMode = null;
        while (iterator.hasNext()) {
            FireMode mode = iterator.next();
            if (firstMode == null) {
                firstMode = mode; // Store the first mode encountered
            }
            if (mode.equals(currentMode)) {
                if (iterator.hasNext()) {
                    return iterator.next();
                } else {
                    // If the current mode is the last one, return the first one
                    return firstMode;
                }
            }
        }
        return iterator.next();
    }

    @Override
    public boolean isPerspectiveAware() {
        return true;
    }

    @Override
    public int getUseDuration(@NotNull ItemStack stack) {
        return 72000;
    }

    @Override
    public boolean isEnchantable(@NotNull ItemStack stack) {
        return false;
    }

    @Override
    public boolean mineBlock(@NotNull ItemStack stack, @NotNull Level level, @NotNull BlockState state, @NotNull BlockPos blockPos, @NotNull LivingEntity livingEntity) {
        return false;
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level level, Player player, @NotNull InteractionHand hand) {
        var itemStack = player.getItemInHand(hand);
        final var id = GeoItem.getId(itemStack);
        var isAiming = itemStack.getOrCreateTag().getBoolean(Constants.IS_AIMING_STRING);
        player.startUsingItem(hand);
        // Resets ticks calls to 0 when cooldown or when you let go
        if ((beamTicks > 0 || burstTicks > 0) && player.getCooldowns().isOnCooldown(this)) {
            beamTicks = 0;
            burstTicks = 0;
            this.triggerAnim(player, id, Constants.CONTROLLER_STRING,
                    isAiming ? Constants.AIM_STRING : Constants.IDLE_STRING);
        }
        return InteractionResultHolder.consume(itemStack);
    }

    @Override
    public void onUseTick(@NotNull Level level, @NotNull LivingEntity livingEntity, ItemStack itemStack, int ticks) {
        var ammunition = itemStack.getOrCreateTag().getInt(Constants.AMMO_STRING);
        if (livingEntity instanceof Player player && (!player.getCooldowns().isOnCooldown(itemStack.getItem()))) {
            var hitResult = ProjectileUtil.getHitResultOnViewVector(player, entity -> true, this.gunData.maxRange());
            if (ammunition == 0) {
                this.reload(level, player, itemStack);
                return;
            } else if (!player.getCooldowns().isOnCooldown(itemStack.getItem())) {
                // Check if the hit result is a block hit and if so, adjust it to skip non-solid blocks
                if (hitResult.getType() == HitResult.Type.BLOCK) {
                    var blockHitResult = (BlockHitResult) hitResult;
                    var blockPos = blockHitResult.getBlockPos();
                    if (!level.getBlockState(blockPos).isSolidRender(level, blockPos)) {
                        // The block is not solid, adjust the hit result to the next block
                        var hitVec = blockHitResult.getLocation();
                        var direction = blockHitResult.getLocation().subtract(
                                player.getEyePosition(1.0f)).normalize().multiply(this.gunData.maxRange(),
                                this.gunData.maxRange(), this.gunData.maxRange());
                        var nextBlockPos = blockPos.offset((int) direction.x(), (int) direction.y(),
                                (int) direction.z());
                        hitResult = new BlockHitResult(hitVec, blockHitResult.getDirection(), nextBlockPos, false);
                    }
                }
                fire(level, player, itemStack, hitResult);
            }
        }
        super.onUseTick(level, livingEntity, itemStack, ticks);
    }

    public void reload(Level level, Player player, ItemStack itemStack) {
        if (gunData.reloadItem() != null) {
            if (player.getInventory().countItem(gunData.reloadItem()) > 0) {
                var ammoTag = itemStack.getOrCreateTag();
                GunFunctions.removeAmmo(gunData.reloadItem(), player);
                player.getCooldowns().addCooldown(this, this.gunData.reloadTicks());
                ammoTag.putInt(Constants.AMMO_STRING, ammoTag.getInt(Constants.AMMO_STRING) + this.gunData.magSize());
                if (ammoTag.getBoolean(Constants.IS_AIMING_STRING))
                    itemStack.getOrCreateTag().putBoolean(Constants.RELOADING_STRING, true);
                this.triggerAnim(player, GeoItem.getId(itemStack), Constants.CONTROLLER_STRING,
                        Constants.RELOAD_STRING);
                if (!level.isClientSide()) {
                    burstTicks = 0;
                    beamTicks = 0;
                }
            } else {
                level.playSound(player, player.blockPosition(), SoundEvents.COMPARATOR_CLICK, SoundSource.PLAYERS,
                        0.25F, 1.3F);
            }
        }
    }

    private void fire(@NotNull Level level, @NotNull Player player, ItemStack itemStack, HitResult hitResult) {
        final var id = GeoItem.getId(itemStack);
        var isAiming = itemStack.getOrCreateTag().getBoolean(Constants.IS_AIMING_STRING);
        var fireMode = itemStack.getOrCreateTag().getInt(Constants.FIREMODES_STRING);
        var animationToTrigger = AnimationFunctions.getFiringAnimation(isAiming, fireMode, burstTicks, this.gunData);
        // Uses the vanilla cool-down system to fire rate
        if (itemStack.getOrCreateTag().getInt(Constants.FIREMODES_STRING) != 2 && itemStack.getOrCreateTag().getInt(
                Constants.FIREMODES_STRING) != 0) player.getCooldowns().addCooldown(this, this.gunData.fireRate());
        if (itemStack.getOrCreateTag().getInt(
                Constants.FIREMODES_STRING) == 0 && burstTicks <= (this.gunData.magSize() / 3) && !player.getCooldowns().isOnCooldown(
                this)) {
            if (!level.isClientSide()) burstTicks++;
            player.getCooldowns().addCooldown(this, this.gunData.fireRate());
            this.triggerAnim(player, id, Constants.CONTROLLER_STRING, animationToTrigger);
        }
        if (itemStack.getOrCreateTag().getInt(
                Constants.FIREMODES_STRING) == 0 && burstTicks > (this.gunData.magSize() / 3)) {
            burstTicks = 0;
            player.getCooldowns().addCooldown(this, this.gunData.fireRate() * 5);
            this.triggerAnim(player, id, Constants.CONTROLLER_STRING,
                    isAiming ? Constants.AIM_STRING : Constants.IDLE_STRING);
            return;
        }
        // Calls method to damage an entity if found
        GunFunctions.damageEntity(level, player, hitResult, this.gunData);
        // Spawns the tracer particle if set
        if (this.gunData.tracerParticle() != null) if (itemStack.getOrCreateTag().getInt(
                Constants.DAMAGETYPE_STRING) == 1 || itemStack.getOrCreateTag().getInt(
                Constants.DAMAGETYPE_STRING) == 2) {
            GunFunctions.spawnBulletTracer(level, player, this.gunData.maxRange(),
                    this.gunData.tracerParticle()); // Spawn bullet tracer particles
        } else if (itemStack.getOrCreateTag().getInt(Constants.DAMAGETYPE_STRING) == 2) {
            GunFunctions.spawnBeam(level, player, this.gunData.maxRange(), this.gunData.tracerParticle(),
                    isAiming); // Spawn beam
        }
        // Removes 1 tick of ammo, so basically 1 bullet each time this method is called
        if (itemStack.getOrCreateTag().getInt(Constants.DAMAGETYPE_STRING) != 2)
            itemStack.getOrCreateTag().putInt(Constants.AMMO_STRING,
                    itemStack.getOrCreateTag().getInt(Constants.AMMO_STRING) - 1);
        if (itemStack.getOrCreateTag().getInt(
                Constants.DAMAGETYPE_STRING) == 2 && beamTicks <= this.gunData.magSize() && !player.getCooldowns().isOnCooldown(
                this)) {
            player.sendSystemMessage(Component.literal(String.valueOf(beamTicks)));
            beamTicks++;
            this.triggerAnim(player, id, Constants.CONTROLLER_STRING, animationToTrigger);
        }
        var currentTime = System.currentTimeMillis();
        long lastUpdateTime = 0L;
        if (itemStack.getOrCreateTag().getInt(
                Constants.DAMAGETYPE_STRING) == 2 && beamTicks >= this.gunData.magSize() && currentTime - lastUpdateTime >= 1000L) {
            beamTicks = 0;
            player.getCooldowns().addCooldown(this, this.gunData.reloadTicks());
            this.triggerAnim(player, id, Constants.CONTROLLER_STRING,
                    isAiming ? Constants.AIM_STRING : Constants.IDLE_STRING);
            return;
        }
        // Sanity check for making sure the reload tag is false
        if (itemStack.getOrCreateTag().getBoolean(Constants.RELOADING_STRING))
            itemStack.getOrCreateTag().putBoolean(Constants.RELOADING_STRING, false);
        // Triggers the firing animation
        if (itemStack.getOrCreateTag().getInt(Constants.FIREMODES_STRING) != 0 || itemStack.getOrCreateTag().getInt(
                Constants.DAMAGETYPE_STRING) != 2) {
            this.triggerAnim(player, id, Constants.CONTROLLER_STRING, animationToTrigger);
        }
        if (this.gunData.fireSound() != null)
            level.playSound(null, player.getX(), player.getY(), player.getZ(), ModSounds.vigilanceFire.get(),
                    SoundSource.PLAYERS, 1, 1);
        if (level.isClientSide()) {
            ClientUtils.getClientPlayer().turn(
                    GunFunctions.getRecoilX(player, itemStack.getOrCreateTag().getBoolean(Constants.IS_AIMING_STRING),
                            this.gunData) * 5,
                    -GunFunctions.getRecoilY(itemStack.getOrCreateTag().getBoolean(Constants.IS_AIMING_STRING),
                            this.gunData) * 5);
        }
    }

    @Override
    public void appendHoverText(ItemStack itemStack, Level level, @NotNull List<Component> tooltip, @NotNull TooltipFlag tooltipFlag) {
        if (itemStack.getOrCreateTag().getInt(Constants.DAMAGETYPE_STRING) != Constants.energy.getName().hashCode())
            tooltip.add(Component.translatable("item.azurelibguns.disc.ammo").withStyle(ChatFormatting.ITALIC).append(
                    String.valueOf(itemStack.getOrCreateTag().getInt(Constants.AMMO_STRING))));
        if (itemStack.getOrCreateTag().getInt(Constants.DAMAGETYPE_STRING) == Constants.energy.getName().hashCode())
            tooltip.add(Component.translatable("item.azurelibguns.disc.heat").withStyle(ChatFormatting.ITALIC).append(
                    String.valueOf(itemStack.getOrCreateTag().getInt(String.valueOf(beamTicks)))));
        tooltip.add(Component.translatable("item.azurelibguns.disc.firemode").withStyle(ChatFormatting.ITALIC).append(
                Component.translatable(this.getCurrentFireMode(itemStack).getName())));
        tooltip.add(Component.translatable("item.azurelibguns.disc.damagetype").withStyle(ChatFormatting.ITALIC).append(
                Component.translatable(this.getCurrentDamageType(itemStack).getName())));
        super.appendHoverText(itemStack, level, tooltip, tooltipFlag);
    }

    @Override
    public void inventoryTick(@NotNull ItemStack itemStack, @NotNull Level level, @NotNull Entity entity, int slot, boolean selected) {
        final var id = GeoItem.getId(itemStack);
        var compoundTag = itemStack.getOrCreateTag();
        var animationController = getAnimatableInstanceCache().getManagerForId(id).getAnimationControllers().get(
                Constants.CONTROLLER_STRING);
        var isReloadAnimationPlaying = AnimationFunctions.isAnimationPlaying(animationController);
        if (selected && entity instanceof Player player) {
            var isPlayerUsingItem = player.isUsingItem();
            if (!isPlayerUsingItem && !player.getCooldowns().isOnCooldown(this)) {
                this.triggerAnim(player, GeoItem.getId(itemStack), Constants.CONTROLLER_STRING, !compoundTag.getBoolean(
                        Constants.IS_AIMING_STRING) ? Constants.IDLE_STRING : Constants.AIM_STRING);
            }
        }
        GunFunctions.tagCreationBackup(compoundTag, this.gunData, isReloadAnimationPlaying, selected);
        if (level.isClientSide() && selected && entity instanceof Player) {
            GunFunctions.sendClientPackets();
        }
        super.inventoryTick(itemStack, level, entity, slot, selected);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, Constants.CONTROLLER_STRING, 1, event -> {
            if (event.getController().getCurrentAnimation() == null || event.getController().getAnimationState() == AnimationController.State.STOPPED) {
                event.getController().tryTriggerAnimation(Constants.IDLE_STRING);
            }
            return PlayState.CONTINUE;
        }).setSoundKeyframeHandler(event -> {
                    var player = ClientUtils.getClientPlayer();
                    if (event.getKeyframeData().getSound().matches(Constants.RELOAD_STRING))
                        player.level().playSound(player, player.blockPosition(), ModSounds.vigilanceReload.get(),
                                SoundSource.PLAYERS, 1.0F, 1.0F);
                }).triggerableAnim(Constants.RELOADTHIRD_STRING,
                        RawAnimation.begin().thenLoop(Constants.RELOADTHIRD_STRING)) // 3rd Person reloading
                .triggerableAnim(Constants.AIM_STRING, RawAnimation.begin().thenLoop(Constants.AIM_STRING)) // ADS
                .triggerableAnim(Constants.AIMFIRING_STRING,
                        RawAnimation.begin().thenLoop(Constants.AIMFIRING_STRING)) //ADS Firing
                .triggerableAnim(Constants.FIRINGFULLAUTO_STRING,
                        RawAnimation.begin().then(Constants.FIRINGFULLAUTO_STRING,
                                Animation.LoopType.PLAY_ONCE)) // Full auto
                .triggerableAnim(Constants.FIRINGAUTO_STRING, RawAnimation.begin().then(Constants.FIRINGAUTO_STRING,
                        Animation.LoopType.PLAY_ONCE)) // Semi Auto
                .triggerableAnim(Constants.AIMFIRINGAUTO_STRING,
                        RawAnimation.begin().then(Constants.AIMFIRINGAUTO_STRING,
                                Animation.LoopType.PLAY_ONCE)) // Semi Auto
                .triggerableAnim(Constants.IDLE_STRING, RawAnimation.begin().thenLoop(Constants.IDLE_STRING)) // Idle
                .triggerableAnim(Constants.FIRING_STRING,
                        RawAnimation.begin().thenLoop(Constants.FIRING_STRING)) // basic firing
                .triggerableAnim(Constants.RELOAD_STRING,
                        RawAnimation.begin().then(Constants.RELOAD_STRING, Animation.LoopType.PLAY_ONCE)));// Reloading)
    }

    @Override
    public Supplier<Object> getRenderProvider() {
        return GeoItem.makeRenderer(this);
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return AzureLibUtil.createInstanceCache(this);
    }

    @Override
    public void createRenderer(Consumer<Object> consumer) {
        consumer.accept(new RenderProvider() {
            @Override
            public BlockEntityWithoutLevelRenderer getCustomRenderer() {
                return new GunRender<BaseGunItem>(getGunName());
            }
        });
    }
}
