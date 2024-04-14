package mod.azure.azurelibguns.mixins;

import mod.azure.azurelibguns.example.BaseGunItem;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;

/**
 * Disables the guns from swinging, which currently breaks the arm animations
 */
@Mixin(Player.class)
public abstract class PlayerMixin extends LivingEntity {

    protected PlayerMixin(EntityType<? extends LivingEntity> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    public void swing(InteractionHand hand) {
        if (!(this.getUseItem().getItem() instanceof BaseGunItem)) super.swing(hand);
    }
}
