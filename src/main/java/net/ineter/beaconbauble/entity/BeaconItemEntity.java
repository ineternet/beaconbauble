package net.ineter.beaconbauble.entity;

import net.ineter.beaconbauble.EntityRegistrar;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.world.World;

public class BeaconItemEntity extends ItemEntity {

    public BeaconItemEntity(EntityType<? extends ItemEntity> rt, World world) {
        super(rt, world);
    }

    @Override
    public void tick() {
        //this.setNoPickUpDelay();
        super.tick();
    }
}
