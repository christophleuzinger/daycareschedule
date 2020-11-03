package ch.westworks.daycareschedule;

import ch.westworks.daycareschedule.xlsx.XlsxInputReader;
import ch.westworks.daycareschedule.xlsx.XlsxOutputWriter;
import org.junit.jupiter.api.Test;

import java.io.File;

import static java.awt.Desktop.getDesktop;

public class XlsxTest {
    @Test
    public void test() throws Exception {
        final File file = new File(getClass().getResource("input.xlsx").toURI());
        final Input input = new XlsxInputReader().readInput(file);
        final Solution solution = new Solver(input).solve();
        final File output = File.createTempFile("solution", ".xslx");
        new XlsxOutputWriter().writeOutput(output, input, solution);
        getDesktop().open(output);
    }
}
