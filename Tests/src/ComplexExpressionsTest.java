import CoreParts.api.Engine;
import CoreParts.impl.InnerSystemComponents.EngineImpl;
import CoreParts.impl.UtilisUI.TerminalSheet;
import Utility.Exception.CellCantBeEvaluated;
import Utility.Exception.CycleDetectedException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ComplexExpressionsTest {

    private Engine engine;
    private TerminalSheet uiSheet;

    @BeforeEach
    public void setUp() throws Exception {
        engine = new EngineImpl();
        String liorPath = "C:\\Users\\Lior\\Desktop\\Lior\\שנה 2\\java\\projects\\matalot\\SheetCelll\\Engine\\src\\Utility\\STLSheetXmlExamples\\Empty.xml";

        engine.readSheetCellFromXML(liorPath);
        uiSheet = new TerminalSheet(); // Initialize your UI sheet implementation
    }

    @Test
    public void testUpdateSingleCell() throws Exception {
        System.out.println("Starting test: testUpdateSingleCell");
        engine.updateCell("5", 'A', "1");
        System.out.println("Updated A1 with 5");
        assertEquals(5.0, engine.getRequestedCell("A1").getEffectiveValue().getValue());
        uiSheet.display(engine.getSheetCell());
    }

    @Test
    public void basicPlusFunction() throws Exception {
        System.out.println("Starting test: testUpdateMultipleCells");
        engine.updateCell("5", 'A', "1");
        System.out.println("Updated A1 with 5");
        engine.updateCell("10", 'A', "2");
        System.out.println("Updated A2 with 10");
        engine.updateCell("{plus,{ref,A1},{ref,A2}}", 'A', "3");
        System.out.println("Updated A3 with formula {plus,{ref,A1},{ref,A2}}");
        assertEquals(15.0, engine.getRequestedCell("A3").getEffectiveValue().getValue());
        uiSheet.display(engine.getSheetCell());
    }
    @Test
    public void testFunctionWithEmptyReference() throws Exception {
        System.out.println("Starting test: testFunctionWithEmptyReference");
        engine.updateCell("{ref,A1}", 'B', "1");
        System.out.println("Updated B1 with formula {ref,A1}");
        assertEquals("", engine.getRequestedCell("B1").getEffectiveValue().getValue());
        uiSheet.display(engine.getSheetCell());

        engine.updateCell("5", 'A', "1");
        System.out.println("Updated A1 with 5");

        engine.updateCell("{plus,{ref,A1},{ref,B1}}", 'C', "1");
        System.out.println("Updated C1 with formula {plus,{ref,A1},{ref,B1}}");
        assertEquals(10.0, engine.getRequestedCell("C1").getEffectiveValue().getValue());
        uiSheet.display(engine.getSheetCell());
    }

    @Test
    public void testCircularDependency() throws Exception {
        System.out.println("Starting test: testCircularDependency");
        engine.updateCell("10", 'B', "1"); // Set B1 to 10
        System.out.println("Initial setup: B1 = 10");
        try {
            engine.updateCell("{plus,{ref,B1},3}", 'A', "1");
            System.out.println("Updated A1: {plus,{ref,B1},3}");

            engine.updateCell("{plus,{ref,A1},{ref,B1}}", 'C', "1");
            System.out.println("Updated C1: {plus,{ref,A1},{ref,B1}}");

            engine.updateCell("{plus,{ref,B1},{ref,C1}}", 'B', "1");
            System.out.println("Updated B1: {plus,{ref,B1},{ref,C1}}");

            fail("Expected CycleDetectedException to be thrown");
        } catch (CycleDetectedException e) {
            System.out.println("Caught expected CycleDetectedException:");
            System.out.println(e.getMessage());

            System.out.println("Sheet state after exception:");
            uiSheet.display(engine.getSheetCell());
        }
    }

    @Test
    public void plusNumAndString() throws Exception {
        System.out.println("Starting test: plusNumAndString");
        engine.updateCell("hello", 'B', "1");
        System.out.println("Updated B1 with 'hello'");

        engine.updateCell("{plus,{ref,B1},3}", 'A', "1");
        System.out.println("Updated A1 with formula {plus,{ref,B1},3}");
        assertEquals(Double.NaN, engine.getRequestedCell("A1").getEffectiveValue().getValue());
        uiSheet.display(engine.getSheetCell());
        engine.updateCell("10", 'B', "1");
        System.out.println("Updated B1 with 10");
        uiSheet.display(engine.getSheetCell());
        assertEquals(13.0, engine.getRequestedCell("A1").getEffectiveValue().getValue());
    }
    @Test
    public void moreComplexPlusAndString() throws Exception {
        engine.updateCell("10", 'B', "1");
        System.out.println("Updated B1 with 10");
        engine.updateCell("World", 'B', "2");
        System.out.println("Updated B2 with 'World'");
        engine.updateCell("{CONCAT,{ref,B1},{ref,B2}}", 'A', "1");
        System.out.println("Updated A1 with formula {CONCAT,{ref,B1},{ref,B2}}");
        assertEquals("UNDEFINED", engine.getRequestedCell("A1").getEffectiveValue().getValue());
        uiSheet.display(engine.getSheetCell());
        engine.updateCell("hello ", 'B', "1");
        System.out.println("Updated B1 with 'hello'");
        uiSheet.display(engine.getSheetCell());
        assertEquals("hello World", engine.getRequestedCell("A1").getEffectiveValue().getValue());
    }
    @Test
    public void concatRefToRawNumber() throws Exception {
        engine.updateCell("hello", 'D', "2");
        System.out.println("Updated B1 with 'hello'");
        try {
            System.out.println("trying to Update A1 with formula {CONCAT,{REF,D2},2}");
            engine.updateCell("{CONCAT,{REF,D2},2}", 'A', "1");
            fail("Expected CellCantBeEvaluated to be thrown");
        }catch (CellCantBeEvaluated e) {
            System.out.println("Caught expected CellCantBeEvaluatedException:");
            System.out.println(e.getMessage());
        }
    }
    @Test
    public void ConcatRefToEmptyCell() throws Exception {

        engine.updateCell("{Concat,{ref,a1},{ref,a3}}", 'A', "2");
        System.out.println("Updated A2 with two empty cells a1 and a3");
        assertEquals("UNDEFINED", engine.getRequestedCell("A2").getEffectiveValue().getValue());
        uiSheet.display(engine.getSheetCell());
        engine.updateCell("hello", 'A', "1");
        System.out.println("Updated A1 with 'hello'");
        engine.updateCell("world", 'A', "3");
        System.out.println("Updated A3 with 'world'");
        assertEquals("hello world", engine.getRequestedCell("A2").getEffectiveValue().getValue());
        uiSheet.display(engine.getSheetCell());

    }

    @Test
    public void PlusRefToEmptyCell() throws Exception {
        engine.updateCell("{plus,{ref,a1},{ref,a3}}", 'A', "2");
        System.out.println("Updated A2 with two empty cells a1 and a3");
        assertEquals(Double.NaN, engine.getRequestedCell("A2").getEffectiveValue().getValue());
        uiSheet.display(engine.getSheetCell());
        engine.updateCell("{plus,{ref,a2},{ref,b2}}", 'B', "1");
        System.out.println("Updated B1 with formula {plus,{ref,a2},{ref,b2}}");
        engine.updateCell("5", 'A', "1");
        System.out.println("Updated A1 with 5");
        engine.updateCell("10", 'A', "3");
        System.out.println("Updated A3 with 10");
        assertEquals(15.0, engine.getRequestedCell("A2").getEffectiveValue().getValue());
        engine.updateCell("10", 'B', "2");
        System.out.println("Updated B2 with 10");
        assertEquals(25.0, engine.getRequestedCell("B1").getEffectiveValue().getValue());
        uiSheet.display(engine.getSheetCell());


    }
}