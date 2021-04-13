package listener;

import commands.CarveCommand;
import commands.Command;
import commands.InfoCommand;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.HashMap;
import java.util.Map;

public class CommandListener extends ListenerAdapter {
    private Map<String, Command> commands;

    public CommandListener() {
        // TODO: parse command strings from Command.getString() methods
        super();
        commands = new HashMap<>();
        commands.put("!carve", new CarveCommand());
        commands.put("!fcarve", new CarveCommand());
        commands.put("!info", new InfoCommand());
    }

    public void onMessageReceived(MessageReceivedEvent event) {
        if (event.getAuthor().isBot()) return;

        User author = event.getAuthor();
        Message message = event.getMessage();
        String content = message.getContentRaw();
        MessageChannel channel = event.getChannel();

        if (content.isEmpty() || content.charAt(0) != '!') return;

        String[] args = content.split(" ");
        if (!isValidCommand(args[0])) {
            channel.sendMessage("Smoh... (I don't know how to do that! Type !info for valid commands!");
            return;
        }
        System.out.println("Executing: " + message);
        commands.get(args[0]).execute(channel, author, message);
    }

    // Checks for valid commands (excluding help)
    private boolean isValidCommand(String command) {
        return commands.containsKey(command);
    }

}
