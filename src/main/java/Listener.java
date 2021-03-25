
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
        // if (event.getAuthor().isBot()) return;
        System.out.println("Message from " +
                event.getAuthor().getName() +
                ": " +
                event.getMessage()
        );
        String fileName = event.getMessage().getAttachments().get(0).getFileName();
        System.out.println("Received " + fileName + " from " + event.getAuthor().getName());

        try {
            event.getMessage().getAttachments().get(0).downloadToFile("src/main/resources/images/" + fileName).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        System.out.println("Smohohmo! (saved)");
        Carver carver = new Carver();
        try {
            System.out.println(fileName);
            carver.createEnergyArray("src/main/resources/images/" + fileName);
        } catch (IOException e) { //Check this later
            System.out.print("File not found!");
        }
    }
}
