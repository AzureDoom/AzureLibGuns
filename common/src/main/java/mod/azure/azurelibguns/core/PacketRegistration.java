package mod.azure.azurelibguns.core;

import commonnetwork.api.Network;

public class PacketRegistration {
    public void init() {
        Network.registerPacket(AimingPacket.CHANNEL, AimingPacket.class, AimingPacket::encode, AimingPacket::decode,
                AimingPacket::handle);
        Network.registerPacket(WeaponModePacket.CHANNEL, WeaponModePacket.class, WeaponModePacket::encode, WeaponModePacket::decode,
                WeaponModePacket::handle);
    }
}
