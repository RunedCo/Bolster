package co.runed.bolster.abilities;

public class AbilityTrigger
{
    // LEFT CLICK VARIANTS
    public static final AbilityTrigger LEFT_CLICK = new AbilityTrigger("left_click", "L-Click");
    public static final AbilityTrigger LEFT_CLICK_BLOCK = new AbilityTrigger("left_click_block", "L-Click");
    public static final AbilityTrigger LEFT_CLICK_AIR = new AbilityTrigger("left_click_air", "L-Click");

    // RIGHT CLICK VARIANTS
    public static final AbilityTrigger RIGHT_CLICK = new AbilityTrigger("right_click", "R-Click");
    public static final AbilityTrigger RIGHT_CLICK_BLOCK = new AbilityTrigger("right_click_block", "R-Click");
    public static final AbilityTrigger RIGHT_CLICK_AIR = new AbilityTrigger("right_click_air", "R-Click");

    // ACTIVE ACTIONS
    public static final AbilityTrigger ON_SHOOT = new AbilityTrigger("on_shoot", "R-Click");
    public static final AbilityTrigger ON_SWAP_OFFHAND = new AbilityTrigger("on_swap_offhand", "F");
    public static final AbilityTrigger ON_DROP_ITEM = new AbilityTrigger("on_drop_item", "Q");
    public static final AbilityTrigger ON_SNEAK = new AbilityTrigger("on_sneak", "Shift");
    public static final AbilityTrigger ON_BREAK_BLOCK = new AbilityTrigger("on_break_block", "L-Click");
    public static final AbilityTrigger ON_CONSUME_ITEM = new AbilityTrigger("on_consume_item", "R-Click");
    public static final AbilityTrigger ON_CATCH_FISH = new AbilityTrigger("on_catch_fish", "R-Click");

    // EVENT ACTIONS
    public static final AbilityTrigger ON_PROJECTILE_HIT = new AbilityTrigger("on_projectile_hit", "Passive");
    public static final AbilityTrigger ON_PICKUP_ITEM = new AbilityTrigger("on_pickup_item", "Passive");
    public static final AbilityTrigger ON_TAKE_DAMAGE = new AbilityTrigger("on_take_damage", "Passive");
    public static final AbilityTrigger ON_TAKE_FATAL_DAMAGE = new AbilityTrigger("on_take_fatal_damage", "Passive");
    public static final AbilityTrigger ON_DAMAGE_ENTITY = new AbilityTrigger("on_damage_entity", "Passive");
    public static final AbilityTrigger ON_DEATH = new AbilityTrigger("on_death", "Passive");
    public static final AbilityTrigger ON_KILL_ENTITY = new AbilityTrigger("on_kill_entity", "Passive");
    public static final AbilityTrigger ON_SELECT_ITEM = new AbilityTrigger("on_select_item", "Passive"); // TODO
    public static final AbilityTrigger ON_DESELECT_ITEM = new AbilityTrigger("on_deselect_item", "Passive"); // TODO
    public static final AbilityTrigger ON_INTERACT_ENTITY = new AbilityTrigger("on_interact_entity", "Passive");
    public static final AbilityTrigger ON_INTERACTED_WITH = new AbilityTrigger("on_interacted_with", "Passive");
    public static final AbilityTrigger ON_SPAWN = new AbilityTrigger("on_spawn", "Passive");
    public static final AbilityTrigger ON_ENTER_PORTAL = new AbilityTrigger("on_enter_portal", "Passive");

    // PASSIVE ACTIONS
    public static final AbilityTrigger TICK = new AbilityTrigger("tick", "Passive");
    public static final AbilityTrigger PASSIVE = new AbilityTrigger("passive", "Passive");

    private final String id;
    private final String displayName;

    public AbilityTrigger(String id, String displayName)
    {
        this.id = id;
        this.displayName = displayName;
    }

    public String getId()
    {
        return id;
    }

    public String getDisplayName()
    {
        return displayName;
    }
}
