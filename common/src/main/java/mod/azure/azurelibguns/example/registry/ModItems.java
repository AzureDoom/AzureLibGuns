package mod.azure.azurelibguns.example.registry;

import mod.azure.azurelibguns.api.common.GunData;
import mod.azure.azurelibguns.example.BaseGunItem;
import mod.azure.azurelibguns.example.WeaponTypes;
import net.minecraft.world.item.Item;

public class ModItems {
    public static final Item PEACEMAKER = gunItem(WeaponTypes.PEACEMAKER);
    public static final Item REDEEMER = gunItem(WeaponTypes.REDEEMER);
    public static final Item SENATOR = gunItem(WeaponTypes.SENATOR);
    public static final Item DAGGER = gunItem(WeaponTypes.DAGGER);

    static Item gunItem(GunData gunData) {
        return new BaseGunItem(gunData) {
        };
    }
}
