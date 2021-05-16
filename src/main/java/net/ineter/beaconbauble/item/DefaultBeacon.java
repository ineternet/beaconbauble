package net.ineter.beaconbauble.item;

import net.minecraft.potion.Effect;
import net.minecraft.potion.Effects;

public class DefaultBeacon implements IBeaconHandler {
    @Override
    public boolean isActivated() {
        return false;
    }

    @Override
    public int getLevel() {
        return 0;
    }

    @Override
    public Effect getPrimaryEffect() {
        return Effects.MOVEMENT_SPEED;
    }

    @Override
    public Effect getSecondaryEffect() {
        return Effects.MOVEMENT_SPEED;
    }
}
