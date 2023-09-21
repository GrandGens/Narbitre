package me.petitgens;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.GatewayIntent;

public class BotDiscord extends ListenerAdapter {

    private static final String TOKEN = "MTE1NDQ1MDU2OTgwNTcwNTI0Ng.GRmAOG.2eORdpT7vieXi89TLQw00iu7Lh2aj_jKXU-PpI";

    public static void main(String[] args){
        try{
            JDA bot = JDABuilder.createDefault(TOKEN)
                    .enableIntents(GatewayIntent.MESSAGE_CONTENT)
                    .setActivity(Activity.playing("with their nose"))
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

    public void direNez(MessageChannel messageChannel){
        System.out.println("nez");
        messageChannel.sendMessage("nez").queue();
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if(event.getAuthor().isBot()){
            return;
        }

        if(event.getMessage().getContentRaw().equals("nez")){
            direNez(event.getChannel());
        }
    }
}
