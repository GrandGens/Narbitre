package me.petitgens.narbitre;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Objects;

public class ExpressionNasale {
    public final Date date;
    public final int heure;

    public final String userId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ExpressionNasale that = (ExpressionNasale) o;
        return heure == that.heure && Objects.equals(date, that.date) && Objects.equals(userId, that.userId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(date, heure, userId);
    }

    public ExpressionNasale(Date date, int heure, String userId) {
        this.date = date;
        this.heure = heure;
        this.userId = userId;
    }

    public static ExpressionNasale fromMessage(Message message){
        ZonedDateTime localTime = message.getTimeCreated().atZoneSameInstant(ZoneId.systemDefault());
        Date date = new Date(localTime.getDayOfMonth(), localTime.getMonthValue(), localTime.getYear());
        int heure = message.getTimeCreated().getMinute();
        User praticant = message.getAuthor();

        return new ExpressionNasale(date, heure, praticant.getId());
    }
}
