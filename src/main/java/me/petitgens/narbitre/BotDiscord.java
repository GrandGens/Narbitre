package me.petitgens.narbitre;

import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.GatewayIntent;

import java.io.IOException;

public class BotDiscord extends ListenerAdapter {

    private static final String TOKEN = "MTE1NDQ1MDU2OTgwNTcwNTI0Ng.GRmAOG.2eORdpT7vieXi89TLQw00iu7Lh2aj_jKXU-PpI";
    private static final String EXPRESSIONS_CSV_PATH = "expressions.csv";
    private static final String PRATICANT_CSV_PATH = "praticants.csv";


    private final CompteurNez compteurNez;

    public static void main(String[] args){
        try{
            JDABuilder.createDefault(TOKEN)
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
    }

    public BotDiscord(){
        compteurNez = new CompteurNez(EXPRESSIONS_CSV_PATH, PRATICANT_CSV_PATH);
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if(event.getAuthor().isBot()){
            return;
        }

        Message message = event.getMessage();

        System.out.printf("%dh%d\n", message.getTimeCreated().getHour(), message.getTimeCreated().getMinute());

        if(message.getContentRaw().equals("nez")){
            ExpressionNasale expressionNasale = ExpressionNasale.fromMessage(message);
            try {
                compteurNez.ajouterExpression(expressionNasale, message.getAuthor());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        else if(message.getContentRaw().equals("!classement")){
            message.getChannel().sendMessage(compteurNez.classement()).queue();
            System.out.println(compteurNez.classement());
        }

        else if(message.getContentRaw().equals("!recompter")){
            try {
                compteurNez.recompter(message.getChannel());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
