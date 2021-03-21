
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
        MessageChannel channel = event.getChannel();
        if (event.getAuthor().isBot()) return;
        if (event.getMessage().getContentRaw().equals("clean up, kibby")) {
            deleteFromUser(event.getChannel(), event.getJDA().getSelfUser(), 5);
            channel.sendMessage("SMOOO!! (cleaning)").queue();
            return;
        }
        if (event.getMessage().getContentRaw().equals("!carve")) {
<<<<<<< Updated upstream
            if (event.getMessage().getAttachments().get(0).isImage()) {
                // Save image to a path
                // Pass image path to SeamCarver
            }
=======
            String fileName = event.getMessage().getAttachments().get(0).getFileName();
            System.out.println("Received " + fileName + " from " + event.getAuthor().getName());
            event.getMessage().getAttachments().get(0).downloadToFile("src/main/resources/images/" + fileName);
            System.out.println("Smohohmo! (saved)");
>>>>>>> Stashed changes
        }

        channel.sendMessage(genResponse()).queue();
    }

    private String genResponse() {
        String msg = "";
        double random = Math.random();
        if (random < 0.33) {
            msg = "smo.";
        } else if (random >= 0.33 && random < 0.66) {
            msg = "smoh!";
        } else if (random >= 0.66 && random < 0.9) {
            msg = "smoh....";
        } else if (random >= 0.9 && random < 0.95) {
            msg = "SMOH!!?";
        } else {
            msg = "SMIMAMO!!! SMIMAMO!!! (doing a rare kibby dance)";
        }
        return msg;
    }

    private void deleteFromUser(MessageChannel channel, User author, int amount) {
        List<Message> messages = new ArrayList<>();
        channel.getIterableHistory()
                .forEachAsync(m -> {
                    if (m.getAuthor().equals(author)) messages.add(m);
                    return messages.size() < amount;
                })
                .thenRun(() -> channel.purgeMessages(messages));
    }
}
