package mod.azure.azurelibguns.example.firemodes;

import mod.azure.azurelibguns.api.common.FireMode;

public class Single implements FireMode {

    @Override
    public String getName() {
        return "item.azurelibguns.disc.single";
    }
}
