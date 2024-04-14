package mod.azure.azurelibguns;

import mod.azure.azurelibguns.core.PacketRegistration;
import net.minecraft.resources.ResourceLocation;

public class CommonMod {
    public static CommonMod commonMod;
    public static final String MOD_ID = "azurelibguns";

    public CommonMod() {
        // initialize and register packets
        new PacketRegistration().init();
    }

    public static final ResourceLocation modResource(String name) {
        return new ResourceLocation(MOD_ID, name);
    }
}
