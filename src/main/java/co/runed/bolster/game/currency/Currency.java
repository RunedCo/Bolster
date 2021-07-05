package co.runed.bolster.game.currency;

import co.runed.bolster.util.ICategorised;
import co.runed.bolster.util.ItemBuilder;
import co.runed.bolster.util.registries.IRegisterable;
import co.runed.bolster.util.registries.Registries;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Currency implements IRegisterable, ICategorised
{
    String id;
    String name;
    String shortName;
    ItemStack icon;
    boolean isItem;
    boolean pluralize;

    public Currency(String id, String name, String shortName, ItemStack itemStack, boolean pluralize, boolean isItem)
    {
        this.id = id;
        this.name = name;
        this.shortName = shortName;
        this.pluralize = pluralize;
        this.icon = itemStack;
        this.isItem = isItem;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getName()
    {
        return name;
    }

    public String getPluralisedName()
    {
        return this.getName() + (this.shouldPluralize() ? "s" : "");
    }

    public String getShortName()
    {
        return shortName;
    }

    public void setShortName(String shortName)
    {
        this.shortName = shortName;
    }

    public boolean shouldPluralize()
    {
        return pluralize;
    }

    @Override
    public String getId()
    {
        return this.id != null ? this.id : Registries.CURRENCIES.getId(this);
    }

    public void setIcon(ItemStack icon)
    {
        this.icon = icon;
    }

    @Override
    public ItemStack getIcon()
    {
        ItemBuilder builder = new ItemBuilder(icon)
                .addAllItemFlags()
                .setDisplayName(Component.text(this.getName(), NamedTextColor.WHITE));

        return builder.build();
    }

    public static Map<Currency, Integer> fromList(List<String> costs)
    {
        Map<Currency, Integer> output = new HashMap<>();

        if (costs == null) return output;

        for (String cost : costs)
        {
            String[] splitCost = cost.split(" ");
            int amount = Integer.parseInt(splitCost[0]);
            String costId = splitCost[1];
            Currency currency = Registries.CURRENCIES.get(costId);

            output.put(currency, amount);
        }

        return output;
    }
}
