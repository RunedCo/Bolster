package co.runed.bolster.gui;

import co.runed.bolster.Warps;
import org.bukkit.entity.Player;
import org.ipvp.canvas.Menu;
import org.ipvp.canvas.mask.BinaryMask;
import org.ipvp.canvas.mask.Mask;
import org.ipvp.canvas.paginate.PaginatedMenuBuilder;
import org.ipvp.canvas.slot.SlotSettings;
import org.ipvp.canvas.template.StaticItemTemplate;
import org.ipvp.canvas.type.ChestMenu;

public class GuiWarps extends Gui {
    public GuiWarps(Gui previousGui) {
        super(previousGui);
    }

    @Override
    public String getTitle(Player player) {
        return "Warps";
    }

    @Override
    public Menu draw(Player player) {
        var warps = Warps.getInstance().getWarps();

        var pageTemplate = ChestMenu.builder(6)
                .title(this.getTitle(player))
                .redraw(true);

        Mask itemSlots = BinaryMask.builder(pageTemplate.getDimensions())
                .pattern("111111111")
                .pattern("111111111")
                .pattern("111111111")
                .pattern("111111111")
                .pattern("111111111")
                .pattern("000000000")
                .build();

        var builder = PaginatedMenuBuilder.builder(pageTemplate)
                .slots(itemSlots)
                .nextButton(GuiConstants.GUI_ARROW_RIGHT)
                .nextButtonSlot(51)
                .previousButton(GuiConstants.GUI_ARROW_LEFT)
                .previousButtonSlot(47);

        for (var entry : warps.entrySet()) {
            var warp = entry.getValue();

            var settings = SlotSettings.builder()
                    .itemTemplate(new StaticItemTemplate(warp.getIcon()))
                    .clickHandler((p, info) -> {
                        warp.teleport(p);

                        p.sendMessage("Warping to " + (warp.name == null ? warp.id : warp.name));
                    })
                    .build();

            builder.addItem(settings);
        }

        var pages = builder.build();

        return pages.get(0);
    }
}
