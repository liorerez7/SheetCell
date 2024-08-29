import CoreParts.api.Engine;
import CoreParts.impl.InnerSystemComponents.EngineImpl;
import CoreParts.impl.UtilisUI.TerminalSheet;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class BasicFuncionTest {
    private Engine engine;
    private TerminalSheet uiSheet;

    @BeforeEach
    public void setUp() throws Exception {
        engine = new EngineImpl();
        engine.readSheetCellFromXML("C:\\Users\\nivii\\programming\\CS degree\\year 2\\javaCourse\\sheetCell ex 1\\Engine\\src\\Utility\\STLSheetXmlExamples\\Empty.xml");
        uiSheet = new TerminalSheet(); // Initialize your UI sheet implementation
    }
    @Test
    public void PLUS() throws Exception {
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
    public void MINUS() throws Exception {
        engine.updateCell("5", 'A', "1");
        System.out.println("Updated A1 with 5");
        engine.updateCell("10", 'A', "2");
        System.out.println("Updated A2 with 10");
        engine.updateCell("{minus,{ref,A2},{ref,A1}}", 'A', "3");
        System.out.println("Updated A3 with formula {minus,{ref,A2},{ref,A1}}");
        assertEquals(5.0, engine.getRequestedCell("A3").getEffectiveValue().getValue());
        uiSheet.display(engine.getSheetCell());
    }
    @Test
    public void TIMES() throws Exception {
        engine.updateCell("5", 'A', "1");
        System.out.println("Updated A1 with 5");
        engine.updateCell("10", 'A', "2");
        System.out.println("Updated A2 with 10");
        engine.updateCell("{times,{ref,A1},{ref,A2}}", 'A', "3");
        System.out.println("Updated A3 with formula {times,{ref,A1},{ref,A2}}");
        assertEquals(50.0, engine.getRequestedCell("A3").getEffectiveValue().getValue());
        uiSheet.display(engine.getSheetCell());
    }
    @Test
    public void DIVIDE() throws Exception {
        engine.updateCell("5", 'A', "1");
        System.out.println("Updated A1 with 5");
        engine.updateCell("10", 'A', "2");
        System.out.println("Updated A2 with 10");
        engine.updateCell("{divide,{ref,A2},{ref,A1}}", 'A', "3");
        System.out.println("Updated A3 with formula {divide,{ref,A2},{ref,A1}}");
        assertEquals(2.0, engine.getRequestedCell("A3").getEffectiveValue().getValue());
        uiSheet.display(engine.getSheetCell());
        try {
            engine.updateCell("{divide,10,0}", 'A', "3");
            System.out.println("Updated A3 with formula {divide,10,0}");
            uiSheet.display(engine.getSheetCell());
            assertEquals(Double.NaN, engine.getRequestedCell("A3").getEffectiveValue().getValue());
        }catch (IllegalArgumentException e){
            System.out.println(e.getMessage());
        }
    }

    @Test
    public void MOD() throws Exception {
        engine.updateCell("5", 'A', "1");
        System.out.println("Updated A1 with 5");
        engine.updateCell("10", 'A', "2");
        System.out.println("Updated A2 with 10");
        engine.updateCell("{mod,{ref,A2},{ref,A1}}", 'A', "3");
        System.out.println("Updated A3 with formula {mod,{ref,A2},{ref,A1}}");
        assertEquals(0.0, engine.getRequestedCell("A3").getEffectiveValue().getValue());
        uiSheet.display(engine.getSheetCell());
    }

}
