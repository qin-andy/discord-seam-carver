
import energy.BackwardsEnergy;
import energy.EnergyStrategy;
import energy.ForwardsEnergy;
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
                    case "carve" -> carver = new ModularCarver(path, new BackwardsEnergy(), new DefaultPathfinder());
                    case "fcarve" -> carver = new ModularCarver(path, new ForwardsEnergy(), new ForwardsPathfinder());
                    default -> {
                        sendSadSmoh(channel, "smoh.... (i dont know how to do that... yet!)");
                        return;
                    }
                }

                double xCut = 0; // TODO: whats the convention on initializing empty variables?
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
                } else { // Invalid cut specification!
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

    private boolean isValidCommand(String command) {
        return command.equals("carve") || command.equals(("fcarve")); //TODO: replace with Set contains call?
    }


    private void sendSadSmoh(MessageChannel channel, String msg) {
        channel.sendMessage(msg)
                .addFile(new File("src/main/resources/assets/smoh_apology.jpg")).queue();
    }
}
