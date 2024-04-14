package mod.azure.azurelibguns.core;

import commonnetwork.networking.data.PacketContext;
import commonnetwork.networking.data.Side;
import mod.azure.azurelibguns.CommonMod;
import mod.azure.azurelibguns.example.BaseGunItem;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

public class WeaponModePacket {
    public static final ResourceLocation CHANNEL = CommonMod.modResource("firemodes");

    public WeaponModePacket() {
    }

    public static WeaponModePacket decode(FriendlyByteBuf buf) {
        return new WeaponModePacket();
    }

    public void encode(FriendlyByteBuf buf) {

    }

    public static void handle(PacketContext<WeaponModePacket> ctx) {
        if (Side.SERVER.equals(
                ctx.side()) && (ctx.sender().getMainHandItem().getItem() instanceof BaseGunItem gunItem)) {
            gunItem.cycleFireMode(ctx.sender().getMainHandItem());

        }
    }
}