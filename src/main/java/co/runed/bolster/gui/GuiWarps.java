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

import java.util.List;
import java.util.Map;

public class GuiWarps extends Gui
{
    public GuiWarps(Gui previousGui)
    {
        super(previousGui);
    }

    @Override
    public String getTitle(Player player)
    {
        return "Warps";
    }

    @Override
    public Menu draw(Player player)
    {
        Map<String, Warps.Warp> warps = Warps.getInstance().getWarps();

        ChestMenu.Builder pageTemplate = ChestMenu.builder(6)
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

        PaginatedMenuBuilder builder = PaginatedMenuBuilder.builder(pageTemplate)
                .slots(itemSlots)
                .nextButton(GuiConstants.GUI_ARROW_RIGHT)
                .nextButtonSlot(51)
                .previousButton(GuiConstants.GUI_ARROW_LEFT)
                .previousButtonSlot(47);

        for (Map.Entry<String, Warps.Warp> entry : warps.entrySet())
        {
            Warps.Warp warp = entry.getValue();

            SlotSettings settings = SlotSettings.builder()
                    .itemTemplate(new StaticItemTemplate(warp.getIcon()))
                    .clickHandler((p, info) -> {
                        warp.teleport(p);

                        p.sendMessage("Warping to " + (warp.name == null ? warp.id : warp.name));
                    })
                    .build();

            builder.addItem(settings);
        }

        List<Menu> pages = builder.build();

        return pages.get(0);
    }
}
