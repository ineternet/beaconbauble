package net.ineter.beaconbauble;

import net.ineter.beaconbauble.entity.BeaconItemEntity;
import net.ineter.beaconbauble.item.BeaconItemCapabilityProvider;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.common.extensions.IForgeContainerType;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class EntityRegistrar {
    private static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(ForgeRegistries.ENTITIES, BeaconBauble.MODID);

    public static final EntityType<BeaconItemEntity> NETHER_STAR_NECKLACE_ENTITY_TYPE = EntityType.Builder.of(BeaconItemEntity::new, EntityClassification.MISC)
            .build("nether_star_necklace_entity");

    static void registerAll() {
        ENTITIES.register("nether_star_necklace_entity", () -> NETHER_STAR_NECKLACE_ENTITY_TYPE);

        ENTITIES.register(FMLJavaModLoadingContext.get().getModEventBus());
    }
}
