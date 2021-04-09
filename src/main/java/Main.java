import net.dv8tion.jda.api.AccountType;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.io.IOException;
import java.util.*;

import javax.security.auth.login.LoginException;

public class Main {
    public static void main(String[] args)
            throws LoginException, InterruptedException, IOException
    {
        Scanner s = new Scanner(System.in);
        System.out.println("Enter your bot's authorization token: ");
        String token = "";
        while (!token.equalsIgnoreCase("quit")) {
            token = s.next();
            try {
                JDABuilder jda = JDABuilder.createDefault(token)
                        .addEventListeners(new ImageListener());
                jda.build();
                token = "quit";
            } catch (LoginException e) {
                System.out.println("Invalid login token! Try again (or 'quit' to quit): ");
            }
        }
    }
}
