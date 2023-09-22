package me.petitgens.narbitre;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

public abstract class ParserCsv {
    public static Collection<ExpressionNasale> lireExpressions(String chemin) throws IOException {
        ArrayList<ExpressionNasale> expressionNasales = new ArrayList<>();

        BufferedReader bufferedReader = Files.newBufferedReader(Path.of(chemin));

        String ligne;
        while((ligne = bufferedReader.readLine()) != null){
            expressionNasales.add(parseLigne(ligne));
        }

        bufferedReader.close();
        return expressionNasales;
    }

    public static HashMap<String, String> lireNomsPraticants(String chemin) throws IOException{
        HashMap<String, String> nomsPraticants = new HashMap<>();

        BufferedReader bufferedReader = Files.newBufferedReader(Path.of(chemin));

        String ligne;
        while((ligne = bufferedReader.readLine()) != null){
            String[] ligneSeparee = ligne.split(";");
            if(ligneSeparee.length != 2){
                throw new IOException("invalid number of fields in csv file");
            }
            nomsPraticants.put(ligneSeparee[0], ligneSeparee[1]);
        }

        return nomsPraticants;
    }

    public static void sauvegarderExpressions(String chemin, ArrayList<ExpressionNasale> expressionNasales) throws IOException {
        BufferedWriter bufferedWriter = Files.newBufferedWriter(Path.of(chemin), StandardOpenOption.CREATE);

        for (ExpressionNasale expressionNasale : expressionNasales){
            String ligne = String.format("%d/%d/%d;%d;%s\n", expressionNasale.date.jour, expressionNasale.date.mois,
                    expressionNasale.date.annee, expressionNasale.heure, expressionNasale.userId);
            bufferedWriter.write(ligne);
        }

        bufferedWriter.close();
    }

    public static void sauvegarderPraticants(String chemin, HashMap<String, String> praticants) throws IOException {
        BufferedWriter bufferedWriter = Files.newBufferedWriter(Path.of(chemin), StandardOpenOption.CREATE);

        for (String id : praticants.keySet()){
            bufferedWriter.write(String.format("%s;%s\n", id, praticants.get(id)));
        }

        bufferedWriter.close();
    }

    public static void ajouterExpression(String chemin, ExpressionNasale expressionNasale) throws IOException {
        BufferedWriter bufferedWriter = Files.newBufferedWriter(Path.of(chemin), StandardOpenOption.APPEND,
                StandardOpenOption.CREATE);

        String ligne = String.format("%d/%d/%d;%d;%s\n", expressionNasale.date.jour, expressionNasale.date.mois,
                expressionNasale.date.annee, expressionNasale.heure, expressionNasale.userId);
        bufferedWriter.write(ligne);

        bufferedWriter.close();
    }

    public static void ajouterPraticant(String chemin, String id, String nom) throws IOException {
        BufferedWriter bufferedWriter = Files.newBufferedWriter(Path.of(chemin), StandardOpenOption.APPEND,
                StandardOpenOption.CREATE);

        bufferedWriter.write(String.format("%s;%s\n", id, nom));

        bufferedWriter.close();
    }

    private static ExpressionNasale parseLigne(String ligne) throws IOException {
        String[] ligneSeparee = ligne.split(";");
        if(ligneSeparee.length != 3){
            throw new IOException("invalid number of fields in csv file");
        }
        Date date = parseDate(ligneSeparee[0]);
        int heure;
        try{
            heure = Integer.parseInt(ligneSeparee[1]);
        }
        catch (NumberFormatException e){
            throw new IOException("non-numeric value found in hour field");
        }

        String id = ligneSeparee[2];

        return new ExpressionNasale(date, heure, id);
    }

    private static Date parseDate(String date) throws IOException {
        String[] dateSeparee = date.split("/");
        try{
            if(dateSeparee.length != 3){
                throw new Exception();
            }

            int jour = Integer.parseInt(dateSeparee[0]);
            if(jour < 1 || jour > 31){
                throw new Exception();
            }

            int mois = Integer.parseInt(dateSeparee[1]);
            if(mois < 1 || mois > 12){
                throw new Exception();
            }

            int annee = Integer.parseInt(dateSeparee[2]);

            return new Date(jour, mois, annee);
        }
        catch (Exception e){
            throw new IOException("invalid date");
        }
    }
}
