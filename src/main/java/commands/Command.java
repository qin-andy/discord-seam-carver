package commands;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;

public abstract class Command {
    protected String name;
    public abstract void execute(MessageChannel channel, User author, Message message);
    public String getName() {
        return this.name;
    }
}
