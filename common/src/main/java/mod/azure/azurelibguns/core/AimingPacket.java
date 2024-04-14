package mod.azure.azurelibguns.core;

import commonnetwork.networking.data.PacketContext;
import commonnetwork.networking.data.Side;
import mod.azure.azurelibguns.CommonMod;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

public class AimingPacket {
    public static final ResourceLocation CHANNEL = CommonMod.modResource("aimingmode");

    public AimingPacket() {
    }

    public static AimingPacket decode(FriendlyByteBuf buf) {
        return new AimingPacket();
    }

    public void encode(FriendlyByteBuf buf) {

    }

    public static void handle(PacketContext<AimingPacket> ctx) {
        if (Side.SERVER.equals(ctx.side())) {
            ctx.sender().getMainHandItem().getOrCreateTag().putBoolean("isAiming", !ctx.sender().getMainHandItem().getOrCreateTag().getBoolean("isAiming"));
        }
    }
}