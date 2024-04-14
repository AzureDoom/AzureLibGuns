package mod.azure.azurelibguns.example.damagetypes;

import mod.azure.azurelibguns.api.common.DamageType;

public class Energy implements DamageType {

    @Override
    public String getName() {
        return "item.azurelibguns.disc.energy";
    }

    @Override
    public boolean isarmorPiercing() {
        return false;
    }
}
