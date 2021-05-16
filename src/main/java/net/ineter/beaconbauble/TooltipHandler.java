package net.ineter.beaconbauble;

import net.ineter.beaconbauble.item.BeaconItemCapabilityProvider;
import net.ineter.beaconbauble.item.IBeaconHandler;
import net.minecraft.item.Item;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.HashMap;
import java.util.Map;

public class TooltipHandler {
    private final static Map<Item, Tooltip> items = new HashMap<>();

    public static void addTooltip(Item item, Style textStyle, String text) {
        Tooltip t = new Tooltip(textStyle, text);
        items.put(item, t);
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void tooltipEvent(ItemTooltipEvent event) {
        Item item = event.getItemStack().getItem();
        Tooltip t = items.get(event.getItemStack().getItem());
        if (item == ItemRegistrar.NETHER_STAR_NECKLACE) {
            LazyOptional<IBeaconHandler> cap = event.getItemStack().getCapability(BeaconItemCapabilityProvider.CAPABILITY_BEACON);
            if (cap.isPresent() && cap.resolve().isPresent()) {
                event.getToolTip().add(new TranslationTextComponent(t.text, cap.resolve().get().getLevel()).setStyle(t.style));
                event.getToolTip().add(new TranslationTextComponent("tooltip.beaconbauble.upgrade_instructions").setStyle(t.style));
            }
            else
                event.getToolTip().add(new TranslationTextComponent(t.text, "Error").setStyle(t.style));
        }
        else if (t != null)
            event.getToolTip().add(new TranslationTextComponent(t.text).setStyle(t.style));
    }

    private static class Tooltip {

        final Style style;
        private final String text;

        public Tooltip(Style textStyle, String textComponent) {
            this.style = textStyle;
            this.text = textComponent;
        }
    }
}
