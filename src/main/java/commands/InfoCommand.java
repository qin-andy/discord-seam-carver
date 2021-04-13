package commands;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;

import java.awt.*;
import java.io.File;

public class InfoCommand extends Command {

    public InfoCommand() {
        name = "info";
    }
    // Sends an informative help command decorated with discsord embed
    public void execute(MessageChannel channel, User author, Message message) {
        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle("Smohbot Info", "https://github.com/qin-andy/Smohbot");
        eb.setColor(Color.blue);
        String desc =
                "SMOHHH!!! (My name is Smohbot!)\n"
                        + "I am equipped with a chopping snout for content aware image scaling,"
                        + " using seam carving!"
                        + " Use a ``!carve`` with an image attachment to get started!\n ";
        eb.addField("\n:cat: SMOHHHH! (HELLO!) :cat:", desc, false);

        desc =
            "``!carve x y``: Default seam carving command. x and y can be in decimal percentages or pixels\n\n"
            + "``!fcarve x y``: Experimental carving command. Takes longer, but may reduce distortions.\n\n"
            + "``!info``: information command. The command you're using right now!";

        eb.addField(":heart: Command List: :heart:", desc, false);

        eb.setThumbnail("attachment://smoh_help.jpg");
        eb.setFooter("Visit https://github.com/qin-andy/Smohbot for details",
                "attachment://smoh_help.jpg");
        channel.sendMessage(eb.build()).addFile(new File("src/main/resources/assets/smoh_help.jpg")).queue();
    }
}
