package activity;

import junit.framework.TestCase;
import org.junit.Test;

import static activity.Triangulo.ehTrianguloValido;
import static org.junit.Assert.assertEquals;

public class TrianguloTest extends TestCase {

    public void testMain() {
    }




    @Test
    public void testEhTrianguloValido() {

        assertTrue(Triangulo.ehTrianguloValido(3, 4, 5));
        assertTrue(Triangulo.ehTrianguloValido(5, 5, 5));
        assertTrue(Triangulo.ehTrianguloValido(2, 2, 3));


        assertFalse(Triangulo.ehTrianguloValido(1, 1, 3));
        assertFalse(Triangulo.ehTrianguloValido(0, 4, 5));
        assertFalse(Triangulo.ehTrianguloValido(10, 1, 1));
    }

}