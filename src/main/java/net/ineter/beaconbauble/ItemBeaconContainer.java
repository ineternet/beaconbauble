package net.ineter.beaconbauble;

import net.ineter.beaconbauble.item.BeaconItemCapabilityProvider;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.BeaconContainer;
import net.minecraft.util.IWorldPosCallable;

public class ItemBeaconContainer extends BeaconContainer {
    public ItemBeaconContainer(int p_i50099_1_, IInventory p_i50099_2_, BeaconItemCapabilityProvider bi) {
        super(p_i50099_1_, p_i50099_2_, bi.data, IWorldPosCallable.NULL);
    }
}
