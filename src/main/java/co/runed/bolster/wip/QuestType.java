package co.runed.bolster.wip;

public enum QuestType
{
    DAILY("Daily"),
    WEEKLY("Weekly"),
    EVENT("Event"),
    LEVEL("Level"),
    ONE_OFF("One-Off");

    String displayName;

    QuestType(String displayName)
    {
        this.displayName = displayName;
    }

    public String getDisplayName()
    {
        return displayName;
    }
}
