package me.petitgens.narbitre;

import net.dv8tion.jda.api.entities.User;

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
}
