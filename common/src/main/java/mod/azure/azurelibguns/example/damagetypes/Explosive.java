package mod.azure.azurelibguns.example.damagetypes;

import mod.azure.azurelibguns.api.common.DamageType;

public class Explosive implements DamageType {

    @Override
    public String getName() {
        return "item.azurelibguns.disc.ballistic";
    }

    @Override
    public boolean isarmorPiercing() {
        return false;
    }
}
