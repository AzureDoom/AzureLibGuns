package mod.azure.azurelibguns.example.damagetypes;

import mod.azure.azurelibguns.api.common.DamageType;

public class Incendiary implements DamageType {

    @Override
    public String getName() {
        return "item.azurelibguns.disc.incendiary";
    }

    @Override
    public boolean isarmorPiercing() {
        return false;
    }
}
