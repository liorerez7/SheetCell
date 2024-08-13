package CoreParts;//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.

import CoreParts.impl.EngineImpl;
import CoreParts.smallParts.CellLocation;

public class Main {
    public static void main(String[] args) {

       EngineImpl engine = new EngineImpl();

       engine.updateCell("5", 'A', '1');
       engine.updateCell("8", 'A', '2');
       engine.updateCell("{PLUS, {REF, A1}, {REF, A2}}", 'A', '3');


       engine.updateCell("3", 'A', '1');
       engine.updateCell("{PLUS, 5, Moshe}", 'A', '2');
       System.out.print(engine.getCell(CellLocation.fromCellId('A', '3')).getEffectiveValue().evaluate());
//
//        CellImp cellImp = new CellImp(new Num(5), "5");
//
//        String value = "{MINUS, {PLUS,4,5}, {PLUS, 5, 8}}";
//
//        engine.updateCell(value, 'A', '1');



    }
}