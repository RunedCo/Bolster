/*
 * This file is part of helper, licensed under the MIT License.
 *
 *  Copyright (c) lucko (Luck) <luck@lucko.me>
 *  Copyright (c) contributors
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in all
 *  copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 *  SOFTWARE.
 */

package co.runed.bolster.util.scoreboard;

import co.runed.bolster.util.network.NetworkUtil;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.utility.MinecraftReflection;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableSet;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.*;

/**
 * Implements {@link ScoreboardTeam} using ProtocolUtilLib.
 */
public class PacketScoreboardTeam implements ScoreboardTeam {

    // the display name value in teams if limited to 32 chars
    private static final int MAX_NAME_LENGTH = 32;

    private static String trimName(String name) {
        return name.length() > MAX_NAME_LENGTH ? name.substring(0, MAX_NAME_LENGTH) : name;
    }

    // the prefix/suffix value in the Teams packet is limited to 16 chars
    private static final int MAX_PREFIX_SUFFIX_LENGTH = 16;

    private static String trimPrefixSuffix(String name) {
        return name.length() > MAX_PREFIX_SUFFIX_LENGTH ? name.substring(0, MAX_PREFIX_SUFFIX_LENGTH) : name;
    }

    // the display name value in teams if limited to 40 chars
    private static final int MAX_TEAM_MEMBER_LENGTH = 40;

    private static String trimMember(String name) {
        return name.length() > MAX_TEAM_MEMBER_LENGTH ? name.substring(0, MAX_TEAM_MEMBER_LENGTH) : name;
    }

    // the id of this team
    private final String id;
    // if players should be automatically subscribed
    private boolean autoSubscribe;

    // the members of this team
    private final Set<String> players = Collections.synchronizedSet(new HashSet<>());
    // a set of the players subscribed to & receiving updates for this team
    private final Set<Player> subscribed = Collections.synchronizedSet(new HashSet<>());

    // the current display name
    private String displayName;
    // the current prefix
    private String prefix = "";
    // the current suffix
    private String suffix = "";
    // if friendly fire is allowed
    private boolean allowFriendlyFire = true;
    // if members of this team can see invisible members on the same team
    private boolean canSeeFriendlyInvisibles = true;
    // the current nametag visibility
    private NameTagVisibility nameTagVisibility = NameTagVisibility.ALWAYS;
    // the current collision rule
    private CollisionRule collisionRule = CollisionRule.ALWAYS;
    // color
    private ChatColor color = ChatColor.RESET;

    /**
     * Creates a new scoreboard team
     *
     * @param id            the id of this team
     * @param displayName   the initial display name
     * @param autoSubscribe if players should be automatically subscribed
     */
    public PacketScoreboardTeam(String id, String displayName, boolean autoSubscribe) {
        Objects.requireNonNull(id, "id");
        Preconditions.checkArgument(id.length() <= 16, "id cannot be longer than 16 characters");

        this.id = id;
        this.displayName = trimName(Objects.requireNonNull(displayName, "displayName"));
        this.autoSubscribe = autoSubscribe;
    }

    /**
     * Creates a new scoreboard team
     *
     * @param id          the id of this team
     * @param displayName the initial display name
     */
    public PacketScoreboardTeam(String id, String displayName) {
        this(id, displayName, true);
    }

    @Override
    public String getId() {
        return this.id;
    }

    @Override
    public boolean shouldAutoSubscribe() {
        return this.autoSubscribe;
    }

    @Override
    public void setAutoSubscribe(boolean autoSubscribe) {
        this.autoSubscribe = autoSubscribe;
    }

    @Override
    public String getDisplayName() {
        return this.displayName;
    }

    @Override
    public void setDisplayName(String displayName) {
        Objects.requireNonNull(displayName, "displayName");
        displayName = trimName(displayName);
        if (this.displayName.equals(displayName)) {
            return;
        }

        this.displayName = displayName;
        NetworkUtil.broadcastPacket(this.subscribed, newUpdatePacket());
    }

    @Override
    public String getPrefix() {
        return this.prefix;
    }

    @Override
    public void setPrefix(String prefix) {
        Objects.requireNonNull(prefix, "prefix");
        prefix = trimPrefixSuffix(prefix);
        if (this.prefix.equals(prefix)) {
            return;
        }

        this.prefix = prefix;
        NetworkUtil.broadcastPacket(this.subscribed, newUpdatePacket());
    }

    @Override
    public String getSuffix() {
        return this.suffix;
    }

    @Override
    public void setSuffix(String suffix) {
        Objects.requireNonNull(suffix, "suffix");
        suffix = trimPrefixSuffix(suffix);
        if (this.suffix.equals(suffix)) {
            return;
        }

        this.suffix = suffix;
        NetworkUtil.broadcastPacket(this.subscribed, newUpdatePacket());
    }

    @Override
    public boolean isAllowFriendlyFire() {
        return this.allowFriendlyFire;
    }

    @Override
    public void setAllowFriendlyFire(boolean allowFriendlyFire) {
        if (this.allowFriendlyFire == allowFriendlyFire) {
            return;
        }

        this.allowFriendlyFire = allowFriendlyFire;
        NetworkUtil.broadcastPacket(this.subscribed, newUpdatePacket());
    }

    @Override
    public boolean isCanSeeFriendlyInvisibles() {
        return this.canSeeFriendlyInvisibles;
    }

    @Override
    public void setCanSeeFriendlyInvisibles(boolean canSeeFriendlyInvisibles) {
        if (this.canSeeFriendlyInvisibles == canSeeFriendlyInvisibles) {
            return;
        }

        this.canSeeFriendlyInvisibles = canSeeFriendlyInvisibles;
        NetworkUtil.broadcastPacket(this.subscribed, newUpdatePacket());
    }

    @Override
    public NameTagVisibility getNameTagVisibility() {
        return this.nameTagVisibility;
    }

    @Override
    public void setNameTagVisibility(NameTagVisibility nameTagVisibility) {
        Objects.requireNonNull(nameTagVisibility, "nameTagVisibility");
        if (this.nameTagVisibility == nameTagVisibility) {
            return;
        }

        this.nameTagVisibility = nameTagVisibility;
        NetworkUtil.broadcastPacket(this.subscribed, newUpdatePacket());
    }

    @Override
    public CollisionRule getCollisionRule() {
        return this.collisionRule;
    }

    @Override
    public void setCollisionRule(CollisionRule collisionRule) {
        Objects.requireNonNull(collisionRule, "collisionRule");
        if (this.collisionRule == collisionRule) {
            return;
        }

        this.collisionRule = collisionRule;
        NetworkUtil.broadcastPacket(this.subscribed, newUpdatePacket());
    }

    @Override
    public ChatColor getColor() {
        return this.color;
    }

    @Override
    public void setColor(ChatColor color) {
        Objects.requireNonNull(color, "color");
        if (this.color == color) {
            return;
        }

        this.color = color;
        NetworkUtil.broadcastPacket(this.subscribed, newUpdatePacket());
    }

    @Override
    public boolean addPlayer(String player) {
        Objects.requireNonNull(player, "player");
        player = trimMember(player);
        if (!this.players.add(player)) {
            return false;
        }

        NetworkUtil.broadcastPacket(this.subscribed, newTeamMemberUpdatePacket(player, MemberAction.ADD));
        return true;
    }

    @Override
    public boolean removePlayer(String player) {
        Objects.requireNonNull(player, "player");
        player = trimMember(player);
        if (!this.players.remove(player)) {
            return false;
        }

        NetworkUtil.broadcastPacket(this.subscribed, newTeamMemberUpdatePacket(player, MemberAction.REMOVE));
        return true;
    }

    @Override
    public boolean hasPlayer(String player) {
        Objects.requireNonNull(player, "player");
        return this.players.contains(trimMember(player));
    }

    @Override
    public Set<String> getPlayers() {
        return ImmutableSet.copyOf(this.players);
    }

    @Override
    public void subscribe(Player player) {
        NetworkUtil.sendPacket(player, newCreatePacket());
        this.subscribed.add(player);
    }

    @Override
    public void unsubscribe(Player player) {
        unsubscribe(player, false);
    }

    @Override
    public void unsubscribe(Player player, boolean fast) {
        if (!this.subscribed.remove(player) || fast) {
            return;
        }

        NetworkUtil.sendPacket(player, newRemovePacket());
    }

    @Override
    public void unsubscribeAll() {
        NetworkUtil.broadcastPacket(this.subscribed, newRemovePacket());
        this.subscribed.clear();
    }

    private PacketContainer newCreatePacket() {
        // create an update packet (as that contains a number of values required by the create packet)
        var packet = newUpdatePacket();

        // set mode - byte
        packet.getIntegers().write(0, UpdateType.CREATE.getCode());

        // add player info - array of String(40)
        List<String> players = new ArrayList<>(getPlayers());

        // set players - ProtocolUtilLib handles setting 'Entity Count'
        packet.getSpecificModifier(Collection.class).write(0, players);

        return packet;
    }

    private PacketContainer newRemovePacket() {
        // http://wiki.vg/ProtocolUtil#Teams
        var packet = new PacketContainer(PacketType.Play.Server.SCOREBOARD_TEAM);

        // remove packet only has the default fields

        // set team name - 	String (16)
        packet.getStrings().write(0, getId());

        // set mode - byte
        packet.getIntegers().write(0, UpdateType.REMOVE.getCode());

        return packet;
    }

    private PacketContainer newUpdatePacket() {
        // http://wiki.vg/ProtocolUtil#Teams
        var packet = new PacketContainer(PacketType.Play.Server.SCOREBOARD_TEAM);

        // set team name - 	String (16)
        packet.getStrings().write(0, getId());

        // set mode - byte
        packet.getIntegers().write(0, UpdateType.UPDATE.getCode());

        // set display name - Component
        packet.getChatComponents().write(0, PacketScoreboard.toComponent(getDisplayName()));

        // set prefix - Component
        packet.getChatComponents().write(1, PacketScoreboard.toComponent(getPrefix()));

        // set suffix - Component
        packet.getChatComponents().write(2, PacketScoreboard.toComponent(getSuffix()));

        // set friendly flags - byte - Bit mask. 0x01: Allow friendly fire, 0x02: can see invisible entities on same team
        var data = 0;
        if (isAllowFriendlyFire()) {
            data |= 1;
        }
        if (isCanSeeFriendlyInvisibles()) {
            data |= 2;
        }

        packet.getIntegers().write(1, data);

        // set nametag visibility - String Enum (32)
        packet.getStrings().write(1, getNameTagVisibility().getProtocolName());

        // set collision rule - String Enum (32)
        packet.getStrings().write(2, getCollisionRule().getProtocolName());

        // set color - byte - For colors, the same Chat colors (0-15). -1 indicates RESET/no color.
        packet.getEnumModifier(ChatColor.class, MinecraftReflection.getMinecraftClass("EnumChatFormat")).write(0, getColor());

        return packet;
    }

    private PacketContainer newTeamMemberUpdatePacket(String player, MemberAction action) {
        // http://wiki.vg/ProtocolUtil#Teams
        var packet = new PacketContainer(PacketType.Play.Server.SCOREBOARD_TEAM);

        // set team name - 	String (16)
        packet.getStrings().write(0, getId());

        // set mode
        switch (action) {
            case ADD:
                packet.getIntegers().write(1, UpdateType.ADD_PLAYERS.getCode());
                break;
            case REMOVE:
                packet.getIntegers().write(1, UpdateType.REMOVE_PLAYERS.getCode());
                break;
            default:
                throw new RuntimeException();
        }

        // set players - Array of String (40)
        packet.getSpecificModifier(Collection.class).write(0, Collections.singletonList(player));

        return packet;
    }

    private enum MemberAction {
        ADD, REMOVE
    }

    private enum UpdateType {
        CREATE(0),
        REMOVE(1),
        UPDATE(2),
        ADD_PLAYERS(3),
        REMOVE_PLAYERS(4);

        private final int code;

        UpdateType(int code) {
            this.code = code;
        }

        public int getCode() {
            return this.code;
        }
    }

    // a map of colors --> their mojang code int
    private static final Map<ChatColor, Integer> COLOR_CODES;

    static {
        Map<ChatColor, Integer> codes = new EnumMap<>(ChatColor.class);
        try {
            var codeField = ChatColor.class.getDeclaredField("intCode");
            codeField.setAccessible(true);
            for (var color : ChatColor.values()) {
                if (color.isColor()) {
                    codes.put(color, codeField.getInt(color));
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        COLOR_CODES = codes;
    }

}