package commands;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;

import java.awt.*;
import java.io.File;

public class InfoCommand extends Command {
    public void execute(MessageChannel channel, User author, Message message) {
        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle("Smohbot Info", "https://github.com/qin-andy/Smohbot");
        eb.setColor(Color.red);
        String desc =
                "SMOHHH!!! (Hi!!!! My name is Smohbot!)\n"
                        + "Use !carve with an image attachment to apply content aware image scaling!\n"
                        + "Adjust the cut size by added x and y ratios, i.e. 'carve 0.3 0.3'!\n"
                        + "(Experimental) Try !fcarve to reduce artifacts! (Might take longer)\n";
        eb.addField("SMOHHHHHHHHHHH! (HELLO!)", desc, true);

        channel.sendMessage(eb.build()).addFile(new File("src/main/resources/assets/smoh_help.jpg")).queue();
    }
}
