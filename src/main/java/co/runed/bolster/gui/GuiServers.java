package co.runed.bolster.gui;

import co.runed.bolster.Bolster;
import co.runed.bolster.common.ServerData;
import co.runed.bolster.util.BukkitUtil;
import co.runed.bolster.util.ItemBuilder;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.ipvp.canvas.Menu;
import org.ipvp.canvas.mask.BinaryMask;
import org.ipvp.canvas.mask.Mask;
import org.ipvp.canvas.paginate.PaginatedMenuBuilder;
import org.ipvp.canvas.slot.SlotSettings;
import org.ipvp.canvas.template.StaticItemTemplate;
import org.ipvp.canvas.type.ChestMenu;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GuiServers extends Gui
{
    List<String> gameModes = new ArrayList<>();

    public GuiServers(Gui previousGui, String gameMode)
    {
        this(previousGui, Arrays.asList(gameMode));
    }


    public GuiServers(Gui previousGui, List<String> gameModes)
    {
        super(previousGui);

        this.gameModes.addAll(gameModes);
    }

    @Override
    public String getTitle(Player player)
    {
        return "Servers";
    }

    @Override
    public Menu draw(Player player)
    {
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

        var servers = Bolster.getInstance().getServers();

        for (var entry : servers.entrySet())
        {
            ServerData server = entry.getValue();

            if (server.id.equals(Bolster.getInstance().getServerId())) continue;
            if (gameModes.size() > 0 && !gameModes.contains(server.gameMode)) continue;
            if (server.restricted && !player.hasPermission("bolster.servers.restricted")) continue;

            ItemBuilder itemBuilder = new ItemBuilder(Material.valueOf(server.iconMaterial))
                    .setDisplayName(Component.text(server.name))
                    .addLoreComponent(Component.text("/server " + server.id))
                    .addLoreComponent(Component.text(server.gameMode))
                    .addLoreComponent(Component.text(server.status));

            SlotSettings settings = SlotSettings.builder()
                    .itemTemplate(new StaticItemTemplate(itemBuilder.build()))
                    .clickHandler((p, info) -> {
                        try
                        {
                            BukkitUtil.sendPlayerToServer(p, server.id);
                        }
                        catch (IOException e)
                        {
                            e.printStackTrace();
                        }

                        p.sendMessage("Connecting to " + server.id);
                    })
                    .build();

            builder.addItem(settings);
        }

        List<Menu> pages = builder.build();

        return pages.get(0);
    }
}
