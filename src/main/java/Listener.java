
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.*;
public class Listener extends ListenerAdapter {
    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        // if (event.getAuthor().isBot()) return;
        System.out.println("Message from " +
                event.getAuthor().getName() +
                ": " +
                event.getMessage()
        );
            String fileName = event.getMessage().getAttachments().get(0).getFileName();
            System.out.println("Received " + fileName + " from " + event.getAuthor().getName());
            event.getMessage().getAttachments().get(0).downloadToFile("src/main/resources/images/" + fileName);
            System.out.println("Smohohmo! (saved)");
    }
}
