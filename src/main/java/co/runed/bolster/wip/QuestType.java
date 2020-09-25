package co.runed.bolster.wip;

public enum QuestType
{
    DAILY("quest_daily", "Daily"),
    WEEKLY("quest_weekly", "Weekly"),
    EVENT("quest_event", "Event"),
    LEVEL("quest_level", "Level"),
    ONE_OFF("quest_one_off", "One-Off"),
    QUEST("quest", "Quest");

    String id;
    String displayName;

    QuestType(String id, String displayName)
    {
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
