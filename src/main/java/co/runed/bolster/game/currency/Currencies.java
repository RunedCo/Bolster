package co.runed.bolster.game.currency;

import co.runed.bolster.fx.Glyphs;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class Currencies
{
    public static final Currency GOLD = new Currency("gold", "Gold", Glyphs.GOLD_COIN, new ItemStack(Material.GOLD_INGOT), false, false);
    public static final Currency EMERALD = new Currency("emerald", "Emerald", String.valueOf('\uE000'), new ItemStack(Material.EMERALD), true, false);
    public static final Currency DIAMOND = new Currency("diamond", "Diamond", String.valueOf('\uE000'), new ItemStack(Material.DIAMOND), true, false);
}
