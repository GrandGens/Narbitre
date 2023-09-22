package me.petitgens.narbitre;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.requests.GatewayIntent;

import java.io.IOException;

public class TestCompte {
    private static final String TOKEN = "MTE1NDQ1MDU2OTgwNTcwNTI0Ng.GRmAOG.2eORdpT7vieXi89TLQw00iu7Lh2aj_jKXU-PpI";
    private static final String EXPRESSIONS_CSV_PATH = "expressions.csv";
    private static final String PRATICANT_CSV_PATH = "praticants.csv";

    public static void main(String args[]){
        JDA jda;
        try{
            jda = JDABuilder.createDefault(TOKEN)
                    .enableIntents(GatewayIntent.MESSAGE_CONTENT)
                    //.setActivity(Activity.playing("compter des nez"))
                    .addEventListeners(new BotDiscord())
                    .build();
        }
        catch (Exception e){
            System.out.println("Oups, ça marche pas !");
            System.out.println("Raison :");
            System.out.println(e.getMessage());
            return;
        }
        System.out.println("Connection OK");

        CompteurNez compteurNez = new CompteurNez(EXPRESSIONS_CSV_PATH, PRATICANT_CSV_PATH);
        Guild guild = jda.getGuildById("1049018739955335219");
        if (guild != null) {
            MessageChannel messageChannel = guild.getTextChannelById("1075331788869730325");
            try {
                compteurNez.recompter(messageChannel);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        else{
            System.out.println("Le serveur n'a pas été trouvé...");
        }

    }


}
