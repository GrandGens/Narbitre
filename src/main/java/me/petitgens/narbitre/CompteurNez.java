package me.petitgens.narbitre;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageHistory;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;

import java.io.IOException;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class CompteurNez {
    private final ArrayList<ExpressionNasale> expressionNasales;

    private HashMap<String, String> praticants;
    private final String cheminCsvExpressions;
    private final String cheminCsvPraticans;

    public CompteurNez(String cheminCsvExpressions, String cheminCsvPraticans) {
        expressionNasales = new ArrayList<>();

        this.cheminCsvExpressions = cheminCsvExpressions;
        this.cheminCsvPraticans = cheminCsvPraticans;
        try {
            expressionNasales.addAll(ParserCsv.lireExpressions(cheminCsvExpressions));
            praticants = ParserCsv.lireNomsPraticants(cheminCsvPraticans);
        } catch (IOException ignored) {
            expressionNasales.clear();
            praticants = new HashMap<>();
        }
    }

    public void ajouterExpression(ExpressionNasale expressionNasale, User utilisateur) throws IOException {
        if(expressionNasales.contains(expressionNasale)){
            return;
        }

        expressionNasales.add(expressionNasale);
        ParserCsv.ajouterExpression(cheminCsvExpressions, expressionNasale);

        if(! praticants.containsKey(utilisateur.getId())){
            praticants.put(utilisateur.getId(), utilisateur.getName());
            ParserCsv.ajouterPraticant(cheminCsvPraticans, utilisateur.getId(), utilisateur.getName());
        }
    }

    public String classement(){
        if(expressionNasales.isEmpty()){
            return "Aucune expression nasale n'a encore été enregistrée.";
        }

        StringBuilder output = new StringBuilder("Classement des praticants par nombres d'expression nasales :\n");

        Object[] idsPraticants = praticants.keySet().toArray();

        int[] comptes = compteExpressions(idsPraticants);

        triClassement(idsPraticants, comptes);

        for(int i = 0; i < idsPraticants.length; i++){
            output.append(String.format("\n%d: %s avec %d expressions\n", i + 1,
                    praticants.get((String) idsPraticants[i]), comptes[i]));
        }

        return output.toString();
    }

    public int[] compteExpressions(Object[] ids){
        int[] output = new int[ids.length];
        for(ExpressionNasale expressionNasale : expressionNasales){
            for(int i = 0; i < ids.length; i++){
                if(expressionNasale.userId.equals(ids[i])){
                    output[i]++;
                    break;
                }
            }
        }
        return output;
    }

    public void triClassement(Object[] ids, int[] comptes){
        for(int i = 0; i < ids.length; i++) {
            int compteCourant = comptes[i];
            Object idCourant = ids[i];
            int indiceGaucheCourant = i - 1;

            while (indiceGaucheCourant >= 0 && comptes[indiceGaucheCourant] < compteCourant) {
                comptes[indiceGaucheCourant + 1] = comptes[indiceGaucheCourant];
                ids[indiceGaucheCourant + 1] = ids[indiceGaucheCourant];
                indiceGaucheCourant--;
            }

            comptes[indiceGaucheCourant + 1] = compteCourant;
            ids[indiceGaucheCourant + 1] = idCourant;
        }
    }

    public void recompter(MessageChannel messageChannel) throws IOException {
        final int MESSAGE_AMOUNT = 100;

        expressionNasales.clear();
        praticants.clear();

        System.out.println("Début du comptage...");

        MessageHistory messageHistory = messageChannel.getHistory();

        int previousHistorySize;

        do{
            previousHistorySize = messageHistory.size();
            messageHistory.retrievePast(MESSAGE_AMOUNT).complete();
        } while(messageHistory.size() > previousHistorySize);

        do {
            previousHistorySize = messageHistory.size();
            messageHistory.retrieveFuture(MESSAGE_AMOUNT).complete();
        } while (messageHistory.size() > previousHistorySize);

        List<Message> messages = messageHistory.getRetrievedHistory();

        for(Message message : messages){
            System.out.println(message.getContentRaw());
            OffsetDateTime dateTime = message.getTimeCreated();
            Locale.setDefault(Locale.FRANCE);
            ZonedDateTime localTime = dateTime.atZoneSameInstant(ZoneId.systemDefault());
            System.out.printf("%d/%d/%d %dh%d:%d\n", localTime.getDayOfMonth(), localTime.getMonthValue(),
                    localTime.getYear(), localTime.getHour(), localTime.getMinute(), localTime.getSecond());
            if(localTime.getHour() == localTime.getMinute()){
                ajouterExpression(ExpressionNasale.fromMessage(message), message.getAuthor());
            }
        }

        System.out.println("Fin du comptage (" + messages.size() + " messages)");

        ParserCsv.sauvegarderExpressions(cheminCsvExpressions, expressionNasales);
        ParserCsv.sauvegarderPraticants(cheminCsvPraticans, praticants);
    }
}
