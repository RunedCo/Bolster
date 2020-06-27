package co.runed.bolster.abilities;

public enum AbilityTrigger {
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
    ON_KILL_ENTITY("on_kill_entity", "Passive"),
    ON_SNEAK("on_sneak", "Shift"),
    PASSIVE("passive", "Passive");

    private final String id;
    private final String displayName;

    private AbilityTrigger(String id, String displayName) {
        this.id = id;
        this.displayName = displayName;
    }

    public String getId() {
        return id;
    }

    public String getDisplayName() {
        return displayName;
    }
}
