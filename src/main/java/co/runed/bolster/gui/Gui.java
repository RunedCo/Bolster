package co.runed.bolster.gui;

import org.bukkit.entity.Player;
import org.ipvp.canvas.Menu;

public abstract class Gui
{
    public abstract String getTitle(Player player);

    protected abstract Menu draw(Player player);

    public Menu show(Player player)
    {
        Menu menu = this.draw(player);

        menu.open(player);

        return menu;
    }
}
