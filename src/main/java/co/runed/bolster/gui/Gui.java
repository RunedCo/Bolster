package co.runed.bolster.gui;

import org.bukkit.entity.Player;
import org.ipvp.canvas.Menu;

public abstract class Gui {
    public Gui previousGui;

    public Gui(Gui previousGui) {
        this.previousGui = previousGui;
    }

    public abstract String getTitle(Player player);

    protected abstract Menu draw(Player player);

    public Menu show(Player player) {
        var menu = this.draw(player);

        menu.open(player);

        return menu;
    }
}
