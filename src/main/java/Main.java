import net.dv8tion.jda.api.AccountType;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.*;

import javax.security.auth.login.LoginException;

public class Main extends ListenerAdapter {
    public static void main(String[] args)
            throws LoginException, InterruptedException
    {
        String token = "ODE3MjgwNjk2ODI4NTU5Mzgw.YEHN9g.xX6mfDGlUOIcnMCdDq0JIQD0_pc";
        JDABuilder jda = JDABuilder.createDefault(token)
                .addEventListeners(new Main());
        jda.build();

    }

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
