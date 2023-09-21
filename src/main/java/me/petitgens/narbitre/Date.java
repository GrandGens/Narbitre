package me.petitgens.narbitre;

import java.util.Objects;

public class Date {
    public final int jour;
    public final int mois;
    public final int annee;

    public Date(int jour, int mois, int annee) {
        this.jour = jour;
        this.mois = mois;
        this.annee = annee;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Date date = (Date) o;
        return jour == date.jour && mois == date.mois && annee == date.annee;
    }

    @Override
    public int hashCode() {
        return Objects.hash(jour, mois, annee);
    }
}
