package net.ineter.beaconbauble.item;

import net.minecraft.potion.Effect;

public interface IBeaconHandler {

    boolean isActivated();
    int getLevel();
    Effect getPrimaryEffect();
    Effect getSecondaryEffect();
}
