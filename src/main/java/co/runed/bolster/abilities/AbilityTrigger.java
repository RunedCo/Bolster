package co.runed.bolster.abilities;

public enum AbilityTrigger
{
    LEFT_CLICK("left_click", "L-Click"),
    RIGHT_CLICK("right_click", "R-Click"),
    ON_SHOOT("on_shoot", "R-Click"),
    ON_SWAP_OFFHAND("on_swap_offhand", "F"),
    ON_DROP_ITEM("on_drop_item", "Q"),
    ON_PICKUP_ITEM("on_pickup_item", "Passive"),
    ON_BREAK_BLOCK("on_break_block", "L-Click"),
    ON_CONSUME_ITEM("on_consume_item", "R-Click"),
    ON_CATCH_FISH("on_catch_fish", "R-Click"),
    ON_TAKE_DAMAGE("on_take_damage", "Passive"),
    ON_TAKE_FATAL_DAMAGE("on_take_fatal_damage", "Passive"),
    ON_DAMAGE_ENTITY("on_damage_entity", "Passive"),
    ON_DEATH("on_death", "Passive"), // TODO
    ON_KILL_ENTITY("on_kill_entity", "Passive"),
    ON_SNEAK("on_sneak", "Shift"),
    ON_SPAWN("on_spawn", "Passive"),
    PASSIVE("passive", "Passive"),

    // CUSTOM TRIGGERS
    // You can assign these to a specific event to easily trigger all abilities
    // For example, DvZ assigns CUSTOM_1 to trigger when a shrine is destroyed
    CUSTOM_1("custom_1", "Passive"),
    CUSTOM_2("custom_2", "Passive"),
    CUSTOM_3("custom_3", "Passive"),
    CUSTOM_4("custom_4", "Passive"),
    CUSTOM_5("custom_5", "Passive"),
    CUSTOM_6("custom_6", "Passive"),
    CUSTOM_7("custom_7", "Passive"),
    CUSTOM_8("custom_8", "Passive"),
    CUSTOM_9("custom_9", "Passive"),
    CUSTOM_10("custom_10", "Passive");

    private final String id;
    private final String displayName;

    AbilityTrigger(String id, String displayName)
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
