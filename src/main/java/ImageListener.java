
import energy.BackwardsEnergy;
import energy.EnergyStrategy;
import energy.ForwardsEnergy;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import pathfinder.DefaultPathfinder;
import pathfinder.ForwardsPathfinder;

import java.io.IOException;
import java.io.File;
import java.util.*;
import java.util.concurrent.ExecutionException;

public class ImageListener extends ListenerAdapter {

    public void onMessageReceived(MessageReceivedEvent event) {
        if (event.getAuthor().isBot()) return;
        Message message = event.getMessage();
        String content = message.getContentRaw();
        MessageChannel channel = event.getChannel();
        String[] splitContent = content.split(" ");

        if ((splitContent[0].equals("carve") || splitContent[0].equals("fcarve")) && !message.getAttachments().isEmpty()) {
            try {
                if (!message.getAttachments().get(0).isImage()) {
                    channel.sendMessage("smoh.... (i dont recognize this format...)");
                }

                channel.sendMessage("SMOH!!! (begins chopping)")
                        .addFile(new File("src/main/resources/graphics/small_chop.gif")).queue();
                message.getAttachments().get(0).downloadToFile("src/main/resources/images/download.png").get();
                System.out.println("File downloaded!");
                String path = "src/main/resources/images/download.png";

                ModularCarver carver = null;
                if (splitContent[0].equals("carve")) {
                    carver = new ModularCarver(path, new BackwardsEnergy(), new DefaultPathfinder());
                } else if (splitContent[0].equals("fcarve")) {
                    carver = new ModularCarver(path, new ForwardsEnergy(), new ForwardsPathfinder());
                } else { // invalid command!
                    channel.sendMessage("smoh.... (i dont know what that word means...)").queue();
                    return;
                }
                System.out.println("Constructed carver!");

                double xCut = 0.25;
                double yCut = 0;
                if (splitContent.length == 2) {
                    xCut = Double.parseDouble(splitContent[1]);
                } else if (splitContent.length == 3) {
                    xCut = Double.parseDouble(splitContent[1]);
                    yCut = Double.parseDouble(splitContent[2]);
                }

                if (xCut >= 1 && yCut >= 1) { // TODO: allow combination of pixel and ratio cuts
                    carver.carve((int) xCut, (int) yCut);
                } else if (xCut >= 0 && yCut >= 0 && xCut < 1 && yCut < 1) {
                    System.out.println("Smoo.. beginning ratio cut!");
                    carver.carve(xCut, yCut);
                } else { // Invalid cut specification!
                    channel.sendMessage("smoh.... (the cut numbers you gave dont make any sense..)").queue();
                    return;
                }
                channel.sendMessage("SMOHOHO!!!") // TODO: add error handling and timing to ModularCarver
                        .addFile(new File("src/main/resources/images/carved.PNG")).queue();
            } catch (InterruptedException e) {

            } catch (ExecutionException e) {

            }
        }
    }
}
