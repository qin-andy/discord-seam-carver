
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.io.File;
import java.util.*;
public class Listener extends ListenerAdapter {
    private final SeamCarver carver;

    public Listener() {
        carver= new SeamCarver();
    }


    public void onMessageReceived(MessageReceivedEvent event) {
        // if (event.getAuthor().isBot()) return;
        System.out.println("Message from " +
                event.getAuthor().getName() +
                ": " +
                event.getMessage()
        );
        MessageChannel channel = event.getChannel();
        if (event.getMessage().getContentRaw().equals("!carve")) {
            String fileName = event.getMessage().getAttachments().get(0).getFileName();
            System.out.println(fileName);
            File attachment = new File("src/main/resources/images/" + fileName);
            event.getMessage().getAttachments().get(0).downloadToFile(attachment);
            System.out.println("Smohohmo! (saved)");
        }
    }
}
