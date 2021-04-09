package commands;

import carver.ModularCarver;
import energy.BackwardsEnergy;
import energy.ForwardsEnergy;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;
import pathfinder.DefaultPathfinder;
import pathfinder.ForwardsPathfinder;

import java.io.File;
import java.util.concurrent.ExecutionException;

public class CarveCommand {
    public void execute(MessageChannel channel, User author, Message message) {

        String content = message.getContentRaw();

        String[] splitContent = content.split(" ");
        String userCommand = splitContent[0];

        if (!isValidCommand(userCommand)) {
            sendSadSmoh(channel, "Invalid command!");
        }
        if (message.getAttachments().isEmpty()) {

        }
        Message.Attachment attachment = message.getAttachments().get(0);
        // Retrieve file from message

        try {
            if (!attachment.isImage()) {
                sendSadSmoh(channel, "smoh.... (I don't recognize this file format..)");
                return;
            }

            String path = "src/main/resources/images/download.png";
            attachment.downloadToFile(path).get();

            channel.sendMessage("SMOH!!! (begins chopping)")
                    .addFile(new File("src/main/resources/assets/small_chop.gif")).queue();
            channel.sendTyping().queue();

            ModularCarver carver = null;
            switch (userCommand) {
                case "!carve" -> carver = new ModularCarver(path, new BackwardsEnergy(), new DefaultPathfinder());
                case "!fcarve" -> carver = new ModularCarver(path, new ForwardsEnergy(), new ForwardsPathfinder());
                default -> {
                    sendSadSmoh(channel, "smoh.... (I don't recognize that command.. try !help)");
                    return;
                }
            }

            double xCut = 0;
            double yCut = 0;
            switch (splitContent.length) {
                case 1 -> xCut = 0.25;
                case 2 -> xCut = Double.parseDouble(splitContent[1]);
                case 3 -> {
                    xCut = Double.parseDouble(splitContent[1]);
                    yCut = Double.parseDouble(splitContent[2]);
                }
                default -> {
                    sendSadSmoh(channel, "smoh.. (too many arguments!)");
                    return;
                }
            }

            if (xCut >= attachment.getWidth() || yCut >= attachment.getHeight()) {
                sendSadSmoh(channel, "smoh.. (cut size cannot be larger than the image!)");
                return;
            }

            if (xCut > 1) { // How does this handle cut sizes of 1? Chop off 1 pixel?
                xCut /= attachment.getWidth();
            }
            if (yCut > 1) {
                yCut /= attachment.getHeight();
            }

            if (xCut >= 0 && yCut >= 0) {
                System.out.println("Smoo.. beginning ratio cut!");
                carver.carve(xCut, yCut);
            } else {
                sendSadSmoh(channel, "smoh.... (the cut numbers you gave dont make any sense..)");
                return;
            }
            channel.sendMessage("SMOHOHO (image completed!!!)")
                    .addFile(new File("src/main/resources/images/carved.PNG")).queue();
        } catch (InterruptedException e) {
            sendSadSmoh(channel, "smoh.... (something got interrupted!)");
        } catch (ExecutionException e) {
            sendSadSmoh(channel, "smoh.... (something happened... (Execution Exception!))");
        } catch (NumberFormatException e) {
            sendSadSmoh(channel, "smoh.... (please specify valid numbers (doubles)!!)");
        }
    }

    private boolean isValidCommand(String command) {
        return command.equals("!carve") || command.equals(("!fcarve"));
    }

    private void sendSadSmoh(MessageChannel channel, String msg) {
        channel.sendMessage(msg)
                .addFile(new File("src/main/resources/assets/smoh_apology.jpg")).queue();
    }
}
