import static org.junit.jupiter.api.Assertions.assertEquals;
import operative.Cabine;
import org.junit.jupiter.api.Test;

public class CabineTests {

    @Test
    void initialisation() {
        Cabine cabine = new Cabine(0.0);
        assertEquals(0.0, cabine.getPosition(), "la cabine doit avoir pour position 0.0.");
    }

    @Test
    void monter() {
        Cabine cabine = new Cabine(0.0);
        cabine.monter(1.0);
        assertEquals(1.0, cabine.getPosition(), "la cabine doit s'être déplacée vers le haut.");
    }

    @Test
    void descendre() {
        Cabine cabine = new Cabine(1.0);
        cabine.descendre(1.0);
        assertEquals(0.0, cabine.getPosition(), "la cabine doit s'être déplacée vers le bas.");
    }

}
