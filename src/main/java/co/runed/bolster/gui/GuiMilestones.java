package co.runed.bolster.gui;

import co.runed.bolster.game.PlayerData;
import co.runed.bolster.game.currency.Currency;
import co.runed.bolster.items.LevelableItem;
import co.runed.bolster.managers.PlayerManager;
import co.runed.bolster.util.ItemBuilder;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.ipvp.canvas.Menu;
import org.ipvp.canvas.mask.BinaryMask;
import org.ipvp.canvas.mask.Mask;
import org.ipvp.canvas.paginate.PaginatedMenuBuilder;
import org.ipvp.canvas.slot.SlotSettings;
import org.ipvp.canvas.template.StaticItemTemplate;
import org.ipvp.canvas.type.ChestMenu;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class GuiMilestones extends Gui
{
    PlayerData playerData;
    Player player;
    LevelableItem item;
    Map<Currency, Integer> costs = new HashMap<>();

    public GuiMilestones(Gui prevGui, LevelableItem item)
    {
        super(prevGui);

        this.item = item;
    }

    @Override
    public String getTitle(Player player)
    {
        return "Milestones for " + item.getName();
    }

    @Override
    protected Menu draw(Player player)
    {
        ChestMenu.Builder pageTemplate = ChestMenu.builder(6)
                .title(this.getTitle(player))
                .redraw(true);

        Mask milestoneMask = BinaryMask.builder(pageTemplate.getDimensions())
                .pattern("000000000")
                .pattern("000000000")
                .pattern("000000000")
                .pattern("010101010")
                .pattern("010101010")
                .pattern("000000000")
                .build();

        PaginatedMenuBuilder builder = PaginatedMenuBuilder.builder(pageTemplate)
                .slots(milestoneMask);

        this.player = player;
        this.playerData = PlayerManager.getInstance().getPlayerData(player);

        Collection<LevelableItem.MilestoneData> milestones = item.getMilestones().values().stream().sorted((m, m2) -> m.getLevel() - m2.getLevel()).collect(Collectors.toList());

        for (LevelableItem.MilestoneData milestone : milestones)
        {
            ItemBuilder milestoneIcon = new ItemBuilder(milestone.getIcon());

            if (this.item.getLevel() < milestone.getLevel())
            {
                milestoneIcon = new ItemBuilder(GuiConstants.GUI_LOCK)
                        .setDisplayName(ChatColor.GOLD + milestone.getName())
                        .setLoreComponent(milestone.getIcon().getItemMeta().lore())
                        .addLoreComponent(Component.newline())
                        .addLoreComponent(Component.text("Unlocks at Level " + milestone.getLevel(), NamedTextColor.RED, TextDecoration.BOLD));
            }

            SlotSettings settings = SlotSettings.builder()
                    .itemTemplate(new StaticItemTemplate(milestoneIcon.build()))
                    .build();

            builder.addItem(settings);
        }

        List<Menu> pages = builder.build();

        for (Menu page : pages)
        {
            this.drawBase(page);
        }

        return pages.get(0);
    }

    public void drawBase(Menu menu)
    {
        List<String> stringCosts = this.item.getUnmergedLevels().get(this.item.getLevel() + 1).getStringList("cost");
        this.costs = Currency.fromList(stringCosts);

        Mask milestoneMask = BinaryMask.builder(menu.getDimensions())
                .pattern("111111111")
                .pattern("111111111")
                .item(GuiConstants.GUI_DIVIDER)
                .build();

        ItemStack baseIcon = this.item.getIcon();
        List<String> stats = this.item.getStatsLore();
        ItemBuilder builder = new ItemBuilder(baseIcon.getType())
                .setDisplayName(baseIcon.getItemMeta().getDisplayName())
                .addItemFlags(baseIcon.getItemMeta().getItemFlags());

        if (stats.size() > 0)
        {
            builder = builder.addLore(stats)
                    .addLore("");
        }

        builder = builder.addLore(ChatColor.WHITE + this.item.getConfig().getString("unlock-tooltip", ""));

        if (this.canUpgrade())
        {
            builder = builder.addLore("")
                    .addLore(ChatColor.WHITE + "Next Level:");

            for (String tip : this.item.getUpgradeTooltip(this.item.getLevel() + 1))
            {
                builder = builder.addBullet(ChatColor.GRAY + tip);
            }

            builder = builder.addLore("")
                    .addLore(ChatColor.WHITE + "Cost to Level Up:");

            for (Map.Entry<Currency, Integer> cost : this.costs.entrySet())
            {
                builder = builder.addBullet((canAfford(this.player, cost.getKey(), cost.getValue()) ? ChatColor.GREEN : ChatColor.RED) + (cost.getValue() + " " + cost.getKey().getPluralisedName()));
            }

            builder = builder.addLore("");

            if (!this.canAfford())
            {
                builder = builder.addLore(GuiConstants.CANNOT_AFFORD_TO + "to level up!");
            }
            else
            {
                builder = builder.addLore(GuiConstants.CLICK_TO + "level up!");
            }
        }

        SlotSettings settings = SlotSettings.builder()
                .itemTemplate(new StaticItemTemplate(builder.build()))
                .clickHandler((p, info) -> {
                    if (this.canAfford() && this.canUpgrade())
                    {
                        this.item.setLevel(this.item.getLevel() + 1);
                        this.item.rebuild();

                        p.playSound(p.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, SoundCategory.PLAYERS, 1, 1);

                        this.show(p);
                    }
                })
                .build();

        milestoneMask.apply(menu);

        menu.getSlot(4).setSettings(settings);
    }

    private boolean canUpgrade()
    {
        return this.item.getLevel() < this.item.getMaxLevel();
    }

    private boolean canAfford()
    {
        return this.costs.entrySet().stream().allMatch((cost) -> canAfford(this.player, cost.getKey(), cost.getValue()));
    }

    public boolean canAfford(Player player, Currency currency, int amount)
    {
        return PlayerManager.getInstance().getPlayerData(player).getCurrency(currency) >= amount;
    }
}
