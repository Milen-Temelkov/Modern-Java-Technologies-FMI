package bg.sofia.uni.fmi.mjt.olympics;

import bg.sofia.uni.fmi.mjt.olympics.competition.Competition;
import bg.sofia.uni.fmi.mjt.olympics.competition.CompetitionResultFetcher;
import bg.sofia.uni.fmi.mjt.olympics.competitor.Athlete;
import bg.sofia.uni.fmi.mjt.olympics.competitor.Competitor;
import bg.sofia.uni.fmi.mjt.olympics.competitor.Medal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.EnumMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class MJTOlympicsTest {

    private MJTOlympics olympics;
    private Competition testCompetition;
    private Competitor athlete1;
    private Competitor athlete2;
    private Competitor athlete3;

    private static CompetitionResultFetcher fetcherMock = Mockito.mock(CompetitionResultFetcher.class);


    @BeforeEach
    void setup() {

        Competitor athlete1 = new Athlete("123", "Asen", "BG");
        Competitor athlete2 = new Athlete("124", "Bob", "USA");
        Competitor athlete3 = new Athlete("125", "Gunter", "DE");

        TreeSet<Competitor> athletes = new TreeSet<>(new CompetitorNameComparator());
        athletes.add(athlete1);
        athletes.add(athlete2);
        athletes.add(athlete3);

        when(fetcherMock.getResult(any()))
            .thenReturn(athletes);


        Set<Competitor> competitors = new HashSet<>();
        competitors.add(athlete1);
        competitors.add(athlete2);
        competitors.add(athlete3);

        olympics = new MJTOlympics(competitors, fetcherMock);
        testCompetition = new Competition("testCompetition", "Unit testing", competitors);
    }


    @Test
    void testUpdateMedalStatisticsWithNullCompetition() {
        assertThrows(IllegalArgumentException.class, () -> olympics.updateMedalStatistics(null)
            , "Calling updateMedals should throw illegalArgument when called with null Competitors");
    }

    @Test
    void testUpdateMedalStatisticsWithUnregisteredCompetitor() {
        Competitor gosho = new Athlete("120", "Georgi", "BG");
//
        Competition competition = new Competition("50m freestyle", "Swimming", Set.of(gosho));

        assertThrows(IllegalArgumentException.class, () -> olympics.updateMedalStatistics(competition)
        , "Calling updateMedals should throw illegalArgument when there is unregistered competitor");
    }

    @Test
    void testUpdateMedalStatisticsWithValidCompetition() {
        olympics.updateMedalStatistics(testCompetition);

        Map<String, EnumMap<Medal, Integer>> medalTable = olympics.getNationsMedalTable();

        assertEquals(1, medalTable.get("BG").get(Medal.GOLD), "BG should have 1 medal after 1 competition");
        assertEquals(1, medalTable.get("USA").get(Medal.SILVER), "USA should have 1 medal after 1 competition");
        assertEquals(1, medalTable.get("DE").get(Medal.BRONZE), "DE should have 1 medal after 1 competition");
    }

    @Test
    void testGetNationsRankList() {
        olympics.updateMedalStatistics(testCompetition);

        assertIterableEquals(List.of("BG", "DE", "USA"), olympics.getNationsRankList(), "Nations should be ranked by number of medals");
    }



    @Test
    void testGetTotalMedalsWithValidNation() {
        olympics.updateMedalStatistics(testCompetition);

        assertEquals(1, olympics.getTotalMedals("BG"), "Bulgaria should have 1 medal after competition");
    }

    @Test
    void testGetTotalMedalsWithNullNation() {
        olympics.updateMedalStatistics(testCompetition);

        assertThrows(IllegalArgumentException.class, () -> olympics.getTotalMedals(null), "Get total medals should throw illegal argument when called with null");
    }

    @Test
    void testGetTotalMedalsWithUnregisteredNation() {
        olympics.updateMedalStatistics(testCompetition);

        assertThrows(IllegalArgumentException.class, () -> olympics.getTotalMedals("RU"), "Get total medals should throw illegal argument when called with unregistered nation");
    }

    @Test
    void testGetRegistered() {
        assertIterableEquals(testCompetition.competitors(), olympics.getRegisteredCompetitors(), "IDK");
    }
}
