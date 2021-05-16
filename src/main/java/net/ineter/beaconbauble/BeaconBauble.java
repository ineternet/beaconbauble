package net.ineter.beaconbauble;

import net.ineter.beaconbauble.item.BeaconItemCapabilityProvider;
import net.ineter.beaconbauble.item.DefaultBeacon;
import net.ineter.beaconbauble.item.IBeaconHandler;
import net.ineter.beaconbauble.item.NetherStarNecklace;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;
import net.minecraft.util.Direction;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.InterModComms;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotTypeMessage;
import top.theillusivec4.curios.api.SlotTypePreset;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

@Mod(BeaconBauble.MODID)
public class BeaconBauble
{
    public static final String MODID = "beaconbauble";

    public static final ItemGroup CREATIVE_GROUP = new ItemGroup(MODID) {
        @Override
        public ItemStack makeIcon() {
            return new ItemStack(()->ItemRegistrar.NETHER_STAR_NECKLACE);
        }
    };

    public BeaconBauble() {
        ItemRegistrar.registerAll();
        ContainerRegistrar.registerAll();
        EntityRegistrar.registerAll();

        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::enqueueIMC);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::commonSetup);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::clientSetup);
        MinecraftForge.EVENT_BUS.register(this);
        MinecraftForge.EVENT_BUS.register(TooltipHandler.class);
    }

    private void clientSetup(FMLClientSetupEvent e) {
        ContainerRegistrar.registerGuis();
    }

    private void commonSetup(FMLCommonSetupEvent e) {
        CapabilityManager.INSTANCE.register(IBeaconHandler.class, new Capability.IStorage<IBeaconHandler>() {
                    @Override
                    public INBT writeNBT(Capability<IBeaconHandler> capability, IBeaconHandler instance, Direction side) {
                        CompoundNBT data = new CompoundNBT();
                        data.putInt("levels", instance.getLevel());
                        data.putInt("primary", Effect.getId(instance.getPrimaryEffect()));
                        data.putInt("secondary", Effect.getId(instance.getSecondaryEffect()));
                        data.putBoolean("activated", instance.isActivated());
                        return data;
                    }

                    @Override
                    public void readNBT(Capability<IBeaconHandler> capability, IBeaconHandler instance, Direction side, INBT nbt) {
                        if (instance instanceof BeaconItemCapabilityProvider && nbt instanceof CompoundNBT) {
                            CompoundNBT data = (CompoundNBT) nbt;
                            BeaconItemCapabilityProvider bc = (BeaconItemCapabilityProvider) instance;
                            bc.setActive(data.getBoolean("activated"));
                            bc.setLevels(data.getInt("levels"));
                            bc.setPrimary(Effect.byId(data.getInt("primary")));
                            bc.setSecondary(Effect.byId(data.getInt("secondary")));
                        }
                    }
                },
                DefaultBeacon::new);
    }

    private void enqueueIMC(final InterModEnqueueEvent event)
    {
        InterModComms.sendTo("curios", SlotTypeMessage.REGISTER_TYPE, () -> SlotTypePreset.NECKLACE.getMessageBuilder().build());
    }

    @SubscribeEvent
    public void playerTicking(final TickEvent.PlayerTickEvent event) {
        if (event.side != LogicalSide.SERVER)
            return;
        PlayerInventory inv = event.player.inventory;
        List<ItemStack> validSlots = new ArrayList<>();
        validSlots.addAll(inv.items);
        validSlots.addAll(inv.offhand);
        validSlots.addAll(inv.armor); //maybe in the future the necklace can go in the helmet slot
        CuriosApi.getCuriosHelper().getEquippedCurios(event.player).ifPresent(x->{
            for (int i = 0; i < x.getSlots(); i++) {
                validSlots.add(x.getStackInSlot(i));
            }
        });
        for (ItemStack stack : validSlots)
            if (stack.getItem().equals(ItemRegistrar.NETHER_STAR_NECKLACE)) {
                LazyOptional<IBeaconHandler> cap = stack.getCapability(BeaconItemCapabilityProvider.CAPABILITY_BEACON);
                if (cap.isPresent() && cap.resolve().isPresent())
                    if (cap.resolve().get().isActivated()) {
                        IBeaconHandler bh = cap.resolve().get();
                        boolean canSecondary = bh.getLevel() >= 4;
                        boolean otherEffect = bh.getSecondaryEffect() != bh.getPrimaryEffect();
                        int primaryEffectLevel = (canSecondary && !otherEffect) ? 1 : 0;
                        event.player.addEffect(new EffectInstance(bh.getPrimaryEffect(), 2, primaryEffectLevel, false, false));
                        if (canSecondary && otherEffect)
                            event.player.addEffect(new EffectInstance(bh.getSecondaryEffect(), 2));
                    }
            }
    }
}
