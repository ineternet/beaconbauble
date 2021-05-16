package net.ineter.beaconbauble;

import net.ineter.beaconbauble.item.BeaconItemCapabilityProvider;
import net.minecraft.client.gui.ScreenManager;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.extensions.IForgeContainerType;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ContainerRegistrar {
    private static final DeferredRegister<ContainerType<?>> CONTAINERS = DeferredRegister.create(ForgeRegistries.CONTAINERS, BeaconBauble.MODID);

    public static final ContainerType<ItemBeaconContainer> ITEM_BEACON = IForgeContainerType.create((wid, inv, data) ->
            new ItemBeaconContainer(wid, inv, new BeaconItemCapabilityProvider(ItemStack.EMPTY)));

    static void registerAll() {
        CONTAINERS.register("item_beacon", ()->ITEM_BEACON);

        CONTAINERS.register(FMLJavaModLoadingContext.get().getModEventBus());
    }

    static void registerGuis() {

    }
}
