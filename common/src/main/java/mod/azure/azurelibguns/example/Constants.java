package mod.azure.azurelibguns.example;

import mod.azure.azurelibguns.example.damagetypes.Ballistic;
import mod.azure.azurelibguns.example.damagetypes.Energy;
import mod.azure.azurelibguns.example.damagetypes.Explosive;
import mod.azure.azurelibguns.example.damagetypes.Incendiary;
import mod.azure.azurelibguns.example.firemodes.Automatic;
import mod.azure.azurelibguns.example.firemodes.Burst;
import mod.azure.azurelibguns.example.firemodes.SemiAutomatic;
import mod.azure.azurelibguns.example.firemodes.Single;

public record Constants() {
    public static final String FIREMODES_STRING = "firemodes";
    public static final String IS_AIMING_STRING = "isAiming";
    public static final String RELOADING_STRING = "reloading";
    public static final String FIRINGAUTO_STRING = "firingauto";
    public static final String FIRINGFULLAUTO_STRING = "firingfullauto";
    public static final String AIMFIRING_STRING = "aimfiring";
    public static final String AIMFIRINGAUTO_STRING = "aimfiringauto";
    public static final String IDLE_STRING = "idle";
    public static final String FIRING_STRING = "firing";
    public static final String RELOAD_STRING = "reload";
    public static final String AMMO_STRING = "ammo";
    public static final String RELOADTHIRD_STRING = "reload3rd";
    public static final String AIM_STRING = "aim";
    public static final String DAMAGETYPE_STRING = "damagetype";
    public static final String CONTROLLER_STRING = "controller";
    // Damage types
    public static Ballistic ballistic = new Ballistic();
    public static Energy energy = new Energy();
    public static Explosive explosive = new Explosive();
    public static Incendiary incendiary = new Incendiary();
    // Fire Modes
    public static Burst burst = new Burst();
    public static SemiAutomatic semiAutomatic = new SemiAutomatic();
    public static Automatic automatic = new Automatic();
    public static Single single = new Single();
}
