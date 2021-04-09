
import energy.BackwardsEnergy;
import energy.EnergyStrategy;
import energy.ForwardsEnergy;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.restaction.MessageAction;
import org.jetbrains.annotations.NotNull;
import pathfinder.DefaultPathfinder;
import pathfinder.ForwardsPathfinder;

import java.awt.*;
import java.io.IOException;
import java.io.File;
import java.nio.channels.Channel;
import java.util.*;
import java.util.concurrent.ExecutionException;

public class ImageListener extends ListenerAdapter {
    private Boolean isWorking; // TODO: look into flag design patterns? action blocking?

    public void onMessageReceived(MessageReceivedEvent event) {
        if (event.getAuthor().isBot()) return;

        Message message = event.getMessage();
        String content = message.getContentRaw();
        MessageChannel channel = event.getChannel();

        String[] splitContent = content.split(" ");
        String userCommand = splitContent[0];

        if (content.equals("!help")) {
            sendHelp(channel);
            return;
        }

        if (isValidCommand(userCommand) && !message.getAttachments().isEmpty()) {
            // Retrieve file from message
            Message.Attachment attachment = message.getAttachments().get(0);
            try {
                if (!attachment.isImage()) {
                    sendSadSmoh(channel, "smoh.... (i dont recognize this file format..)");
                    return;
                }

                String path = "src/main/resources/images/download.png";
                attachment.downloadToFile(path).get();

                channel.sendMessage("SMOH!!! (begins chopping)")
                        .addFile(new File("src/main/resources/assets/small_chop.gif")).queue();
                channel.sendTyping().queue();

                ModularCarver carver = null;
                switch (userCommand) {
                    case "!carve" -> carver = new ModularCarver(path, new BackwardsEnergy(), new DefaultPathfinder());
                    case "!fcarve" -> carver = new ModularCarver(path, new ForwardsEnergy(), new ForwardsPathfinder());
                    default -> {
                        sendSadSmoh(channel, "smoh.... (i dont know how to do that... yet!)");
                        return;
                    }
                }

                double xCut = 0;
                double yCut = 0;
                switch (splitContent.length) {
                    case 1 -> xCut = 0.25;
                    case 2 -> xCut = Double.parseDouble(splitContent[1]);
                    case 3 -> {
                        xCut = Double.parseDouble(splitContent[1]);
                        yCut = Double.parseDouble(splitContent[2]);
                    }
                    default -> {
                        sendSadSmoh(channel, "smoh.... (too many arguments!)");
                        return;
                    }
                }

                if (xCut >= attachment.getWidth() || yCut >= attachment.getHeight()) {
                    sendSadSmoh(channel, "smoh.... (the cut size is too big!)");
                    return;
                }

                if (xCut > 1) { // Edgecase: how does this handle cutsizes of 1?
                    xCut /= attachment.getWidth();
                }
                if (yCut > 1) {
                    yCut /= attachment.getHeight();
                }

                if (xCut >= 0 && yCut >= 0) {
                    System.out.println("Smoo.. beginning ratio cut!");
                    carver.carve(xCut, yCut);
                } else {
                    sendSadSmoh(channel, "smoh.... (the cut numbers you gave dont make any sense..)");
                    return;
                }
                channel.sendMessage("SMOHOHO (image completed!!!)")
                        .addFile(new File("src/main/resources/images/carved.PNG")).queue();
            } catch (InterruptedException e) {
                sendSadSmoh(channel, "smoh.... (something got interrupted!)");
            } catch (ExecutionException e) {
                sendSadSmoh(channel, "smoh.... (something happened... (Execution Exception!))");
            } catch (NumberFormatException e) {
                sendSadSmoh(channel, "smoh.... (please specify valid numbers (doubles)!!)");
            }
        }
    }

    // Checks for valid commands (excluding help)
    private boolean isValidCommand(String command) {
        return command.equals("!carve") || command.equals(("!fcarve"));
    }

    // Sends an apology image for errors
    private void sendSadSmoh(MessageChannel channel, String msg) {
        channel.sendMessage(msg)
                .addFile(new File("src/main/resources/assets/smoh_apology.jpg")).queue();
    }

    // Sends information about the how to use the bot!
    private void sendHelp(MessageChannel channel) {
        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle("Smohbot Info", "https://github.com/qin-andy/Smohbot");
        eb.setColor(Color.red);
        eb.addField("Title", "Smohbot Info", false);

        String desc =
        "SMOHHH!!! (Hi!!!! My name is Smohbot!)\n"
        + "Use !carve with an image attachment to apply content aware image scaling!\n"
        + "Adjust the cut size by added x and y ratios, i.e. 'carve 0.3 0.3'!\n"
        + "(Experimental) Try !fcarve to reduce artifacts! (Might take longer)\n";
        eb.addField("Info", desc, true);

        channel.sendMessage(eb.build()).addFile(new File("src/main/resources/assets/smoh_help.jpg")).queue();
    }
}
