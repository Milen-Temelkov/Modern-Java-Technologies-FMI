package bg.sofia.uni.fmi.mjt.olympics;

import bg.sofia.uni.fmi.mjt.olympics.competitor.Competitor;

import java.util.Comparator;

public class CompetitorNameComparator implements Comparator<Competitor> {
    @Override
    public int compare(Competitor athlete1, Competitor athlete2) {
        return athlete1.getName().compareTo(athlete2.getName());
    }
}

