package net.ineter.beaconbauble.item;

import net.ineter.beaconbauble.ItemBeaconContainer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Effect;
import net.minecraft.potion.Effects;
import net.minecraft.util.Direction;
import net.minecraft.util.IIntArray;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class BeaconItemCapabilityProvider implements IBeaconHandler, ICapabilityProvider, INamedContainerProvider {
    private final ItemStack item;

    @CapabilityInject(IBeaconHandler.class)
    public static Capability<IBeaconHandler> CAPABILITY_BEACON = null;

    private final LazyOptional<IBeaconHandler> handler = LazyOptional.of(()->this);

    public BeaconItemCapabilityProvider(@Nonnull ItemStack stack) {
        item = stack;
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        return CAPABILITY_BEACON.orEmpty(cap, handler);
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public void setLevels(int levels) {
        this.levels = levels;
    }

    public void setPrimary(Effect primary) {
        this.primary = primary;
    }

    public void setSecondary(Effect secondary) {
        this.secondary = secondary;
    }

    public IIntArray data = new IIntArray() {
        @Override
        public int get(int p_221476_1_) {
            switch(p_221476_1_) {
                case 0: return getLevel();
                case 1: return Effect.getId(getPrimaryEffect());
                case 2: return Effect.getId(getSecondaryEffect());
            }
            return 0;
        }

        @Override
        public void set(int p_221477_1_, int p_221477_2_) {
            switch(p_221477_1_) {
                case 0: setLevels(p_221477_2_);
                case 1: setPrimary(Effect.byId(p_221477_2_));
                case 2: setSecondary(Effect.byId(p_221477_2_));
            }
        }

        @Override
        public int getCount() {
            return 3;
        }
    };

    private boolean active = false;
    private int levels = 0;
    private Effect primary = Effects.MOVEMENT_SPEED;
    private Effect secondary = Effects.MOVEMENT_SPEED;

    @Override
    public boolean isActivated() {
        return active;
    }

    @Override
    public int getLevel() {
        return levels;
    }

    @Override
    @Nonnull
    public Effect getPrimaryEffect() {
        return primary;
    }

    @Override
    @Nonnull
    public Effect getSecondaryEffect() {
        return secondary;
    }

    @Override
    @Nonnull
    public ITextComponent getDisplayName() {
        return new TranslationTextComponent("container.beaconbauble.item_beacon");
    }

    @Nullable
    @Override
    public Container createMenu(int wid, @Nonnull PlayerInventory inventory, @Nonnull PlayerEntity player) {
        return new ItemBeaconContainer(wid, inventory, this);
    }
}
