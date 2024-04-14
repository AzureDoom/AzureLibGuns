package mod.azure.azurelibguns.mixins;

import mod.azure.azurelibguns.example.BaseGunItem;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Disables right-clicking on Villagers with the guns opening the trade menu
 */
@Mixin(Villager.class)
public class VillagerMixin {

    @Inject(at = @At("RETURN"), method = "mobInteract", cancellable = true)
    private void killVillager(Player player, InteractionHand hand, CallbackInfoReturnable<InteractionResult> ci) {
        final ItemStack itemStack = player.getItemInHand(hand);
        if (itemStack.getItem() instanceof BaseGunItem) {
            ci.setReturnValue(InteractionResult.FAIL);
        }
    }
}
