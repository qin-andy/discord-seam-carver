import listener.CommandListener;
import net.dv8tion.jda.api.JDABuilder;

import java.io.IOException;
import java.util.*;

import javax.security.auth.login.LoginException;

public class Main {
    // Main bot builder, run this to start the bot
    public static void main(String[] args)
            throws LoginException, InterruptedException, IOException
    {
        Scanner s = new Scanner(System.in);
        System.out.println("Enter your bot's authorization token: ");
        String token = "";
        token = s.next();
        try {
            JDABuilder jda = JDABuilder.createDefault(token)
                    .addEventListeners(new CommandListener());
            jda.build();
            token = "quit";
        } catch (LoginException e) {
            System.out.println("Invalid login token!");
        }
    }
}
