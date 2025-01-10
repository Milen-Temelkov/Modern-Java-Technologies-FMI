package bg.sofia.uni.fmi.mjt.olympics.comparator;

import bg.sofia.uni.fmi.mjt.olympics.MJTOlympics;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.internal.verification.VerificationModeFactory.times;

public class NationMedalComparatorTest {

    private MJTOlympics olympicsMock = Mockito.mock(MJTOlympics.class);

    private NationMedalComparator comp = new NationMedalComparator(olympicsMock);

    @BeforeEach
    void setup() {
        when(olympicsMock.getTotalMedals("BG"))
            .thenReturn(3);
        when(olympicsMock.getTotalMedals("DE"))
            .thenReturn(5);
        when(olympicsMock.getTotalMedals("USA"))
            .thenReturn(5);
    }

    @Test
    void testCompareWithSameCountry() {
        assertEquals(0, comp.compare("BG", "BG"), "Comparing same nation should result in same number of medals");

        verify(olympicsMock, times(2)).getTotalMedals("BG");
    }

    @Test
    void testCompareWithDifferentCountriesDifferentMedals() {
        assertEquals(1, comp.compare("BG", "DE"), "Comparing country with less to a country with more medals should result in 1");

        verify(olympicsMock, times(1)).getTotalMedals("BG");
        verify(olympicsMock, times(1)).getTotalMedals("DE");
    }

    @Test
    void testCompareWithDifferentCountriesSameMedals() {
        assertEquals(1, comp.compare("USA", "DE"), "Comparing contries with same number of medals should order them alphabetically");

        verify(olympicsMock, times(1)).getTotalMedals("USA");
        verify(olympicsMock, times(1)).getTotalMedals("DE");
    }

}
