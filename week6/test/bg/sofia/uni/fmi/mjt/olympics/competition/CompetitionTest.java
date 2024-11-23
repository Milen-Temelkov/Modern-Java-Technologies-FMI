package bg.sofia.uni.fmi.mjt.olympics.competition;

import bg.sofia.uni.fmi.mjt.olympics.competitor.Athlete;
import bg.sofia.uni.fmi.mjt.olympics.competitor.Competitor;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class CompetitionTest {

    @Test
    void testValidCompetitionCreation() {
        Set<Competitor> competitors = new HashSet<>();
        competitors.add(new Athlete("123", "Peter", "BG"));
        competitors.add(new Athlete("124", "Ivan", "BG"));

        Competition competition = new Competition("100m Sprint", "Athletics", competitors);

        assertEquals("100m Sprint", competition.name());
        assertEquals("Athletics", competition.discipline());
        assertEquals(competitors, competition.competitors());
    }

    @Test
    void testCompetitionCreationWithNullName() {
        Set<Competitor> competitors = Set.of(new Athlete("123", "Peter", "BG"));
        assertThrows(IllegalArgumentException.class, () -> new Competition(null, "Athletics", competitors), "Cannot create competition with null name");
    }

    @Test
    void testCompetitionCreationWithBlankName() {
        Set<Competitor> competitors = Set.of(new Athlete("123", "Peter", "BG"));
        assertThrows(IllegalArgumentException.class, () -> new Competition("  ", "Athletics", competitors), "Cannot create competition with blank name");
    }

    @Test
    void testCompetitionCreationWithNullDiscipline() {
        Set<Competitor> competitors = Set.of(new Athlete("123", "Peter", "BG"));
        assertThrows(IllegalArgumentException.class, () -> new Competition("100m Sprint", null, competitors), "Cannot create competition with null discipline");
    }

    @Test
    void testCompetitionCreationWithBlankDiscipline() {
        Set<Competitor> competitors = Set.of(new Athlete("123", "Peter", "BG"));
        assertThrows(IllegalArgumentException.class, () -> new Competition("100m Sprint", "  ", competitors), "Cannot create competition with blank discipline");
    }

    @Test
    void testCompetitionCreationWithNullAthletes() {
        assertThrows(IllegalArgumentException.class, () -> new Competition("100m Sprint", "Athletics", null), "Cannot create competition with null competitors");
    }

    @Test
    void testCompetitionCreationWithEmptyAthletes() {
        assertThrows(IllegalArgumentException.class, () -> new Competition("100m Sprint", "Athletics", Collections.emptySet()), "Cannot create competition with empty competitors");
    }

    @Test
    void testUnmodifiableAthletesSet() {
        Set<Competitor> competitors = new HashSet<>();
        competitors.add(new Athlete("123", "Peter", "BG"));

        Competition competition = new Competition("100m Sprint", "Athletics", competitors);

        Set<Competitor> AthletesFromCompetition = competition.competitors();
        assertThrows(UnsupportedOperationException.class, () -> AthletesFromCompetition.add(new Athlete("123", "Peter", "BG")), "Competitors should be unmodifiable collection");
    }

    @Test
    void testEquals() {
        Set<Competitor> Athletes1 = Set.of(new Athlete("123", "Peter", "BG"));
        Set<Competitor> Athletes2 = Set.of(new Athlete("124", "Ivan", "BG"));

        Competition competition1 = new Competition("100m Sprint", "Athletics", Athletes1);
        Competition competition2 = new Competition("100m Sprint", "Athletics", Athletes2);

        assertEquals(competition1, competition2, "The competitions should be equal if they have the same name and discipline");

    }

    @Test
    void testHashCode() {
        Set<Competitor> Athletes1 = Set.of(new Athlete("123", "Peter", "BG"));
        Set<Competitor> Athletes2 = Set.of(new Athlete("124", "Ivan", "BG"));

        Competition competition1 = new Competition("100m Sprint", "Athletics", Athletes1);
        Competition competition2 = new Competition("100m Sprint", "Athletics", Athletes2);

        assertEquals(competition1.hashCode(), competition2.hashCode(), "The competitions should have the same hashCode if they have the same name and discipline ");
    }
}

