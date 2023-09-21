package me.petitgens.narbitre;

import net.dv8tion.jda.api.entities.User;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

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

    public void ajouterExpression(Date date, int heure, User utilisateur) throws IOException {
        ExpressionNasale expressionNasale = new ExpressionNasale(date, heure, utilisateur.getId());

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
}
