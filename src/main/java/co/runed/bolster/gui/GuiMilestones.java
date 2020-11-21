package co.runed.bolster.gui;

import co.runed.bolster.items.LevelableItem;
import co.runed.bolster.util.ItemBuilder;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.ipvp.canvas.Menu;
import org.ipvp.canvas.mask.BinaryMask;
import org.ipvp.canvas.mask.Mask;
import org.ipvp.canvas.paginate.PaginatedMenuBuilder;
import org.ipvp.canvas.slot.SlotSettings;
import org.ipvp.canvas.template.StaticItemTemplate;
import org.ipvp.canvas.type.ChestMenu;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class GuiMilestones extends Gui
{
    LevelableItem item;

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

        Collection<LevelableItem.MilestoneData> milestones = item.getMilestones().values().stream().sorted((m, m2) -> m.getLevel() - m2.getLevel()).collect(Collectors.toList());

        for (LevelableItem.MilestoneData milestone : milestones)
        {
            ItemBuilder milestoneIcon = new ItemBuilder(milestone.getIcon());

            if (this.item.getLevel() < milestone.getLevel())
            {
                milestoneIcon = new ItemBuilder(GuiConstants.GUI_LOCK)
                        .setDisplayName(milestone.getName())
                        .setLore(milestone.getIcon().getItemMeta().getLore())
                        .addLore("")
                        .addLore(ChatColor.RED + "" + ChatColor.BOLD + "Unlocks at Level " + milestone.getLevel());
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
        Mask milestoneMask = BinaryMask.builder(menu.getDimensions())
                .pattern("111111111")
                .pattern("111111111")
                .item(GuiConstants.GUI_DIVIDER)
                .build();

        ItemBuilder builder = new ItemBuilder(this.item.getIcon());

        if (this.item.getLevel() < this.item.getMaxLevel())
        {
            builder = builder.addLore("")
                    .addLore(ChatColor.WHITE + "Next Level:");

            for (String tip : this.item.getUpgradeTooltip(this.item.getLevel() + 1))
            {
                builder = builder.addBullet(ChatColor.GRAY + tip);
            }

            builder = builder.addLore("")
                    .addLore(ChatColor.WHITE + "Cost to Level Up:");

            int num = 1;
            for (String cost : this.item.getUnmergedLevels().get(this.item.getLevel() + 1).getStringList("cost"))
            {
                builder = builder.addBullet((num < 3 ? ChatColor.GREEN : ChatColor.RED) + cost);

                num++;
            }
        }

        SlotSettings settings = SlotSettings.builder()
                .itemTemplate(new StaticItemTemplate(builder.build()))
                .clickHandler((p, info) -> {
                    this.item.setLevel(this.item.getLevel() + 1);
                    this.item.rebuild();

                    this.show(p);
                })
                .build();

        milestoneMask.apply(menu);

        menu.getSlot(4).setSettings(settings);
    }
}
