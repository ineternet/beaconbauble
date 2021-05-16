package net.ineter.beaconbauble;

import net.ineter.beaconbauble.item.NetherStarNecklace;
import net.minecraft.item.Item;
import net.minecraft.util.text.Color;
import net.minecraft.util.text.Style;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.function.Supplier;

public class ItemRegistrar {
    private static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, BeaconBauble.MODID);

    public static final Style SUBTLE_TOOLTIP = Style.EMPTY.withItalic(true).withColor(Color.fromRgb(0xFF404040));
    public static final Style WARN_TOOLTIP = Style.EMPTY.withItalic(true).withColor(Color.fromRgb(0xFF8300));

    private static final Item.Properties BLANK_PROPERTIES = new Item.Properties().tab(BeaconBauble.CREATIVE_GROUP);

    private static final String[] GENERIC_ITEMS = {
            "obsidian_chain",
            "inhibited_nether_star",
            "beacon_amplifier_matrix"
    };

    static Supplier<Item> genericItemSupplier() {
        Item thisItem = new Item(BLANK_PROPERTIES);
        return ()->thisItem;
    }

    private static final String TOOLTIP_PREFIX = String.format("tooltip.%s.", BeaconBauble.MODID);
    public static final Item NETHER_STAR_NECKLACE = new NetherStarNecklace();
    static {
        TooltipHandler.addTooltip(NETHER_STAR_NECKLACE, SUBTLE_TOOLTIP, TOOLTIP_PREFIX + NetherStarNecklace.REGISTRY_NAME);
    }

    static void registerAll() {
        for (String item : GENERIC_ITEMS)
            ITEMS.register(item, genericItemSupplier());

        ITEMS.register(NetherStarNecklace.REGISTRY_NAME, ()->NETHER_STAR_NECKLACE);

        ITEMS.register(FMLJavaModLoadingContext.get().getModEventBus());
    }
}
