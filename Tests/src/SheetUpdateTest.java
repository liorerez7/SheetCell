import CoreParts.api.Engine;
import CoreParts.impl.InnerSystemComponents.EngineImpl;
import CoreParts.impl.UtilisUI.TerminalSheet;
import CoreParts.smallParts.CellLocation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SheetUpdateTest {

    private Engine engine;
    private TerminalSheet uiSheet;

    @BeforeEach
    public void setUp() throws Exception {
        engine = new EngineImpl();
        engine.readSheetCellFromXML("C:\\Users\\nivii\\programming\\CS degree\\year 2\\javaCourse\\sheetCell ex 1\\Engine\\src\\Utility\\STLSheetXmlExamples\\basic.xml");
        uiSheet = new TerminalSheet(); // Initialize your UI sheet implementation
    }

    @Test
    public void testUpdateSingleCell() throws Exception {
        engine.updateCell("5", 'A', "1");
        assertEquals(5.0, engine.getRequestedCell("A1").getEffectiveValue().getValue());
    }

    @Test
    public void testUpdateMultipleCells() throws Exception {
        engine.updateCell("5", 'A', "1");
        engine.updateCell("10", 'A', "2");
        engine.updateCell("{plus,{ref,A1},{ref,A2}}", 'A', "3");
        assertEquals(15.0, engine.getRequestedCell("A3").getEffectiveValue().getValue());
    }
    @Test
    public void testFunctionWithEmptyReference() throws Exception {
        engine.updateCell("{ref,A2}", 'B', "1");
        assertEquals("",engine.getRequestedCell("B1").getEffectiveValue().getValue());
    }
}




