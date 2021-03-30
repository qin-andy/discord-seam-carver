
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.io.IOException;
import java.io.File;
import java.util.*;
import java.util.concurrent.ExecutionException;

public class Listener extends ListenerAdapter {

    public void onMessageReceived(MessageReceivedEvent event) {
        if (event.getAuthor().isBot()) return;
        System.out.println("Message from " +
                event.getAuthor().getName() +
                ": " +
                event.getMessage().getContentRaw()
        );
        Message message = event.getMessage();
        String content = message.getContentRaw();
        MessageChannel channel = event.getChannel();

        String[] splitContent = content.split(" ");
        if (splitContent[0].equals("carve") && !message.getAttachments().isEmpty()) {
            try {
                message.getAttachments().get(0).downloadToFile("src/main/resources/images/download.png").get();

            } catch (InterruptedException e) {

            } catch (ExecutionException e) {

            }

            Carver carver = new Carver();
            int i = 0;
            try {
                channel.sendMessage("SMOH!!! (begins chopping)")
                        .addFile(new File("src/main/resources/graphics/small_chop.gif")).queue();
                if (splitContent.length > 1) {
                    int sizeX = Integer.parseInt(splitContent[1]);
                    int sizeY = 0;
                    if (splitContent.length > 2) {
                        sizeY = Integer.parseInt(splitContent[2]);
                    }
                    i = carver.carve("src/main/resources/images/download.png", sizeX, sizeY);
                } else {
                    Message.Attachment attachment = message.getAttachments().get(0);
                    i = carver.carve("src/main/resources/images/download.png",
                            (int) (attachment.getWidth()*0.25), 0);
                }
            } catch (IOException e) { //Check this later
                channel.sendMessage("smoh...,, (there was an error downloading the image!)").queue();
            } catch (NumberFormatException e) {
                channel.sendMessage("smoh.,,, (invalid size provided");
            }

            if (i == -1) {
                channel.sendMessage("Smoh..... (cut size is too big...)").queue();
            } else if (i == -2) {
                channel.sendMessage("smoh.. (unable to read file..)").queue();
            }
             else {
                channel.sendMessage("SMOHOHO!!! (finished in " + i + " ms!)")
                    .addFile(new File("src/main/resources/images/carved.PNG")).queue();
            }
        }
    }
}
