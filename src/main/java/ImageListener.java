
import energy.BackwardsEnergy;
import energy.EnergyStrategy;
import energy.ForwardsEnergy;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import pathfinder.DefaultPathfinder;
import pathfinder.ForwardsPathfinder;

import java.io.IOException;
import java.io.File;
import java.nio.channels.Channel;
import java.util.*;
import java.util.concurrent.ExecutionException;

public class ImageListener extends ListenerAdapter {

    public void onMessageReceived(MessageReceivedEvent event) {
        if (event.getAuthor().isBot()) return;

        Message message = event.getMessage();
        String content = message.getContentRaw();
        MessageChannel channel = event.getChannel();

        if (false) { //TODO: add working flags during carving
            channel.sendMessage("smoh.... (i'm busy!!! try again later..)").queue();
            channel.sendTyping().queue();
            return;
        }


        String[] splitContent = content.split(" ");
        String userCommand = splitContent[0];

        if (isValidCommand(userCommand) && !message.getAttachments().isEmpty()) {
            Message.Attachment attachment = message.getAttachments().get(0);
            try {
                if (!attachment.isImage()) {
                    sendSadSmoh(channel, "smoh.... (i dont recognize this file format..)");
                    return;
                }

                String path = "src/main/resources/images/download.png";
                attachment.downloadToFile(path).get();

                channel.sendMessage("SMOH!!! (begins chopping)")
                        .addFile(new File("src/main/resources/graphics/small_chop.gif")).queue();
                channel.sendTyping().queue(); // TOOD: double check if this actually works?

                ModularCarver carver = null;
                switch (userCommand) {
                    case "carve" -> carver = new ModularCarver(path, new BackwardsEnergy(), new DefaultPathfinder());
                    case "fcarve" -> carver = new ModularCarver(path, new ForwardsEnergy(), new ForwardsPathfinder());
                    default -> {
                        sendSadSmoh(channel, "smoh.... (i dont know how to do that... yet!)");
                        return;
                    }
                }
                System.out.println("Constructed carver!");

                double xCut = 0; // TODO: whats the convention on initializing empty variables?
                double yCut = 0;
                switch (splitContent.length) {
                    case 1 -> xCut = 0.25;
                    case 2 -> xCut = Double.parseDouble(splitContent[1]);
                    case 3 -> {
                        xCut = Double.parseDouble(splitContent[1]);
                        yCut = Double.parseDouble(splitContent[2]);
                        if (xCut >= attachment.getWidth() || yCut >= attachment.getHeight()) {
                            sendSadSmoh(channel, "smoh.... (the cut size is too big!)");
                            return;
                        }
                    }
                    default -> {
                        sendSadSmoh(channel, "smoh.... (too many arguments!)");
                        return;
                    }
                }

                if (xCut >= 1 && yCut >= 1) { // TODO: allow combination of pixel and ratio cuts
                    carver.carve((int) xCut, (int) yCut);
                } else if (xCut >= 0 && yCut >= 0 && xCut < 1 && yCut < 1) {
                    System.out.println("Smoo.. beginning ratio cut!");
                    carver.carve(xCut, yCut);
                } else { // Invalid cut specification!
                    sendSadSmoh(channel, "smoh.... (the cut numbers you gave dont make any sense..)");
                    return;
                }
                channel.sendMessage("SMOHOHO!!!") // TODO: add error handling and timing to ModularCarver
                        .addFile(new File("src/main/resources/images/carved.PNG")).queue();
            } catch (InterruptedException e) {

            } catch (ExecutionException e) {

            } catch (NumberFormatException e) {
                sendSadSmoh(channel, "smoh.... (please specify actual numbers!!)");
            }
        }
    }

    private boolean isValidCommand(String command) {
        return command.equals("carve") || command.equals(("fcarve")); //TODO: replace with Set contains call?
    }

    private void sendSadSmoh(MessageChannel channel, String msg) {
        channel.sendMessage(msg)
                .addFile(new File("src/main/resources/graphics/smoh_apology.jpg")).queue();
    }
}
