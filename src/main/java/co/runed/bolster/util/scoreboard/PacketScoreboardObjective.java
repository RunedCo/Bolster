package co.runed.bolster.util.scoreboard;

import co.runed.bolster.util.ProtocolUtil;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.EnumWrappers.ScoreboardAction;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;

import javax.annotation.Nullable;
import java.util.*;

/**
 * Implements {@link ScoreboardObjective} using ProtocolUtilLib.
 */
public class PacketScoreboardObjective implements ScoreboardObjective
{
    // the "Entity name" in the Update Score packet is limited to 40 chars
    private static final int MAX_SCORE_LENGTH = 40;

    private static String trimScore(String name)
    {
        return name.length() > MAX_SCORE_LENGTH ? name.substring(0, MAX_SCORE_LENGTH) : name;
    }

    // the "Objective Value" in the ScoreboardObjective packet is limited to 32 chars
    private static final int MAX_NAME_LENGTH = 32;

    private static String trimName(String name)
    {
        return name.length() > MAX_NAME_LENGTH ? name.substring(0, MAX_NAME_LENGTH) : name;
    }

    // the id of this objective
    private final String id;
    // if players should be automatically subscribed
    private boolean autoSubscribe;

    // the current scores being shown
    private final Map<String, Integer> scores = Collections.synchronizedMap(new HashMap<>());
    // a set of the players subscribed to & receiving updates for this objective
    private final Set<Player> subscribed = Collections.synchronizedSet(new HashSet<>());

    // the current display name
    private String displayName;
    // the current display slot
    private DisplaySlot displaySlot;

    /**
     * Creates a new scoreboard objective
     *
     * @param id            the id of this objective
     * @param displayName   the initial display name
     * @param displaySlot   the initial display slot
     * @param autoSubscribe if players should be automatically subscribed
     */
    public PacketScoreboardObjective(String id, String displayName, DisplaySlot displaySlot, boolean autoSubscribe)
    {
        Objects.requireNonNull(id, "id");
        Preconditions.checkArgument(id.length() <= 16, "id cannot be longer than 16 characters");

        this.id = id;
        this.displayName = trimName(Objects.requireNonNull(displayName, "displayName"));
        this.displaySlot = Objects.requireNonNull(displaySlot, "displaySlot");
        this.autoSubscribe = autoSubscribe;
    }

    /**
     * Creates a new scoreboard objective
     *
     * @param id          the id of this objective
     * @param displayName the initial display name
     * @param displaySlot the initial display slot
     */
    public PacketScoreboardObjective(String id, String displayName, DisplaySlot displaySlot)
    {
        this(id, displayName, displaySlot, true);
    }

    @Override
    public String getId()
    {
        return this.id;
    }

    @Override
    public boolean shouldAutoSubscribe()
    {
        return this.autoSubscribe;
    }

    @Override
    public void setAutoSubscribe(boolean autoSubscribe)
    {
        this.autoSubscribe = autoSubscribe;
    }

    @Override
    public String getDisplayName()
    {
        return this.displayName;
    }

    @Override
    public void setDisplayName(String displayName)
    {
        Objects.requireNonNull(displayName, "displayName");
        displayName = trimName(displayName);
        if (this.displayName.equals(displayName))
        {
            return;
        }

        this.displayName = displayName;
        ProtocolUtil.broadcastPacket(this.subscribed, newObjectivePacket(UpdateType.UPDATE));
    }

    @Override
    public DisplaySlot getDisplaySlot()
    {
        return this.displaySlot;
    }

    @Override
    public void setDisplaySlot(DisplaySlot displaySlot)
    {
        Objects.requireNonNull(displaySlot, "displaySlot");
        if (this.displaySlot == displaySlot)
        {
            return;
        }

        this.displaySlot = displaySlot;

        ProtocolUtil.broadcastPacket(this.subscribed, newDisplaySlotPacket(displaySlot));
    }

    @Override
    public Map<String, Integer> getScores()
    {
        return ImmutableMap.copyOf(this.scores);
    }

    @Override
    public boolean hasScore(String name)
    {
        Objects.requireNonNull(name, "name");
        return this.scores.containsKey(trimScore(name));
    }

    @Nullable
    @Override
    public Integer getScore(String name)
    {
        Objects.requireNonNull(name, "name");
        return this.scores.get(trimScore(name));
    }

    @Override
    public void setScore(String name, int value)
    {
        Objects.requireNonNull(name, "name");
        name = trimScore(name);

        Integer oldValue = this.scores.put(name, value);
        if (oldValue != null && oldValue == value)
        {
            return;
        }

        ProtocolUtil.broadcastPacket(this.subscribed, newScorePacket(name, value, ScoreboardAction.CHANGE));
    }

    @Override
    public boolean removeScore(String name)
    {
        Objects.requireNonNull(name, "name");
        name = trimScore(name);

        if (this.scores.remove(name) == null)
        {
            return false;
        }

        ProtocolUtil.broadcastPacket(this.subscribed, newScorePacket(name, 0, ScoreboardAction.REMOVE));
        return true;
    }

    @Override
    public void clearScores()
    {
        this.scores.clear();

        ProtocolUtil.broadcastPacket(this.subscribed, newObjectivePacket(UpdateType.REMOVE));
        for (Player player : this.subscribed)
        {
            subscribe(player);
        }
    }

    @Override
    public void applyScores(Map<String, Integer> scores)
    {
        Objects.requireNonNull(scores, "scores");

        Set<String> toRemove = new HashSet<>(getScores().keySet());
        for (Map.Entry<String, Integer> score : scores.entrySet())
        {
            toRemove.remove(trimScore(score.getKey()));
        }

        for (String name : toRemove)
        {
            removeScore(name);
        }

        for (Map.Entry<String, Integer> score : scores.entrySet())
        {
            setScore(score.getKey(), score.getValue());
        }
    }

    @Override
    public void applyLines(String... lines)
    {
        applyLines(Arrays.asList(lines));
    }

    @Override
    public void applyLines(Collection<String> lines)
    {
        Objects.requireNonNull(lines, "lines");
        Map<String, Integer> scores = new HashMap<>();
        int i = lines.size();
        for (String line : lines)
        {
            scores.put(line, i--);
        }
        applyScores(scores);
    }

    @Override
    public void subscribe(Player player)
    {
        Objects.requireNonNull(player, "player");
        ProtocolUtil.sendPacket(player, newObjectivePacket(UpdateType.CREATE));
        ProtocolUtil.sendPacket(player, newDisplaySlotPacket(getDisplaySlot()));

        for (Map.Entry<String, Integer> score : getScores().entrySet())
        {
            ProtocolUtil.sendPacket(player, newScorePacket(score.getKey(), score.getValue(), ScoreboardAction.CHANGE));
        }

        this.subscribed.add(player);
    }

    @Override
    public void unsubscribe(Player player)
    {
        unsubscribe(player, false);
    }

    @Override
    public void unsubscribe(Player player, boolean fast)
    {
        Objects.requireNonNull(player, "player");
        if (!this.subscribed.remove(player) || fast)
        {
            return;
        }

        ProtocolUtil.sendPacket(player, newObjectivePacket(UpdateType.REMOVE));
    }

    @Override
    public void unsubscribeAll()
    {
        ProtocolUtil.broadcastPacket(this.subscribed, newObjectivePacket(UpdateType.REMOVE));
        this.subscribed.clear();
    }

    private PacketContainer newObjectivePacket(UpdateType mode)
    {
        // http://wiki.vg/ProtocolUtil#Scoreboard_Objective
        PacketContainer packet = new PacketContainer(PacketType.Play.Server.SCOREBOARD_OBJECTIVE);

        // set name - limited to String(16)
        packet.getStrings().write(0, getId());

        // set mode - 0 to create the scoreboard. 1 to remove the scoreboard. 2 to update the display text.
        packet.getIntegers().write(0, mode.getCode());

        // set display name - Component
        packet.getChatComponents().write(0, PacketScoreboard.toComponent(getDisplayName()));

        // set type - either "integer" or "hearts"
        packet.getEnumModifier(HealthDisplay.class, 2).write(0, HealthDisplay.INTEGER);

        return packet;
    }

    private PacketContainer newScorePacket(String name, int value, ScoreboardAction action)
    {
        // http://wiki.vg/ProtocolUtil#Update_Score
        PacketContainer packet = new PacketContainer(PacketType.Play.Server.SCOREBOARD_SCORE);

        // set "Entity name" - aka the name of the score - limited to 40.
        packet.getStrings().write(0, name);

        // set the action - 0 to create/update an item. 1 to remove an item.
        packet.getScoreboardActions().write(0, action);

        // set objective name - The name of the objective the score belongs to
        packet.getStrings().write(1, getId());

        // set value of the score- The score to be displayed next to the entry. Only sent when Action does not equal 1.
        packet.getIntegers().write(0, value);

        return packet;
    }

    private PacketContainer newDisplaySlotPacket(DisplaySlot displaySlot)
    {
        // http://wiki.vg/ProtocolUtil#Display_Scoreboard
        PacketContainer packet = new PacketContainer(PacketType.Play.Server.SCOREBOARD_DISPLAY_OBJECTIVE);

        // set position
        final int slot;
        switch (displaySlot)
        {
            case PLAYER_LIST:
                slot = 0;
                break;
            case SIDEBAR:
                slot = 1;
                break;
            case BELOW_NAME:
                slot = 2;
                break;
            default:
                throw new RuntimeException();
        }

        packet.getIntegers().write(0, slot);

        // set objective name - The unique name for the scoreboard to be displayed.
        packet.getStrings().write(0, getId());

        return packet;
    }

    private enum UpdateType
    {
        CREATE(0),
        REMOVE(1),
        UPDATE(2);

        private final int code;

        UpdateType(int code)
        {
            this.code = code;
        }

        public int getCode()
        {
            return this.code;
        }
    }

    private enum HealthDisplay
    {
        INTEGER, HEARTS
    }
}
