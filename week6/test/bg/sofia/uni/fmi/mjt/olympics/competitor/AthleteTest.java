package bg.sofia.uni.fmi.mjt.olympics.competitor;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class AthleteTest {

    private Athlete athlete;

    @BeforeEach
    void setUp() {
        athlete = new Athlete("Ident", "Milen", "Bulgaria");
    }

    @Test
    void testAddMedal() {
        athlete.addMedal(Medal.GOLD);

        assertEquals(1, athlete.getMedals().size(), "Medals size should be 2 after adding 1 medal");
    }

    @Test
    void testAddTwoMedalsFromSameType() {
        athlete.addMedal(Medal.GOLD);
        athlete.addMedal(Medal.GOLD);

        assertEquals(2, athlete.getMedals().size(), "Medals size should be 1 after adding 1 medal");
    }

    @Test
    void testAddMedalWithNull() {
        assertThrows(IllegalArgumentException.class, () -> athlete.addMedal(null), "Add medals should throw illegal argument when given null");
    }

}
