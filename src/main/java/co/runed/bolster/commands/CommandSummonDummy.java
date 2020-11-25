package co.runed.bolster.commands;

import co.runed.bolster.Bolster;
import co.runed.bolster.classes.TargetDummyClass;
import co.runed.bolster.util.NetworkUtil;
import dev.jorel.commandapi.CommandAPICommand;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;
import org.bukkit.Location;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class CommandSummonDummy extends CommandBase
{
    public CommandSummonDummy()
    {
        super("dummy");
    }

    @Override
    public CommandAPICommand build()
    {
        return new CommandAPICommand(this.command)
                .withPermission("bolster.commands.dummy")
                .executesPlayer((sender, args) -> {
                    Location loc = sender.getLocation();

                    TargetDummyClass.summon(loc);

                    sender.sendMessage("Summoned Target Dummy");
                });
    }
}
