package mod.azure.azurelibguns.example.damagetypes;

import mod.azure.azurelibguns.api.common.DamageType;

public class APBallistic implements DamageType {

    @Override
    public String getName() {
        return "item.azurelibguns.disc.apballistic";
    }

    @Override
    public boolean isarmorPiercing() {
        return true;
    }
}
