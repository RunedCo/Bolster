package co.runed.bolster.commands;

import dev.jorel.commandapi.CommandAPICommand;

public abstract class CommandBase {
    public String command;

    public CommandBase(String command) {
        this.command = command;
    }

    public abstract CommandAPICommand build();
}
