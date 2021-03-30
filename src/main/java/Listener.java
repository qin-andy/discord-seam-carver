
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
                System.out.println("Message with image received!");
                channel.sendMessage("SMOH!!! (begins chopping)").queue();
                message.getAttachments().get(0).downloadToFile("src/main/resources/images/download.png").get();

            } catch (InterruptedException e) {

            } catch (ExecutionException e) {

            }

            Carver carver = new Carver();
            int i = 0;
            try {
                if (splitContent.length > 1) {
                    i = carver.carve("src/main/resources/images/download.png", Integer.parseInt(splitContent[1]));
                } else {
                    i = carver.carve("src/main/resources/images/download.png", (int) (message.getAttachments().get(0).getWidth()*0.25));
                }
            } catch (IOException e) { //Check this later
                System.out.print("File not found!");
            }

            if (i == -1) {
                channel.sendMessage("Smoh..... (cut size is too big...)").queue();
            } else {
                channel.sendMessage("SMOHOHO!!! (finished in " + i + " ms!)")
                        .addFile(new File("src/main/resources/images/carved.PNG")).queue();
            }
        }
    }
}
