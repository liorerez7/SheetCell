package CoreParts;//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.

import CoreParts.impl.EngineImpl;
import CoreParts.smallParts.CellLocation;

public class Main {
    public static void main(String[] args) {

        EngineImpl engine = new EngineImpl();


        engine.updateCell("4", 'A', '1');
        engine.updateCell("7", 'A', '2');
        engine.updateCell("3", 'A', '3');
        engine.updateCell("{PLUS, {REF, A1}, {MINUS, {REF, A2}, {REF, A3}}}", 'B', '1');

        System.out.print(engine.getCell(CellLocation.fromCellId('B', '1')).getEffectiveValue().evaluate()+"\n");

        engine.updateCell("10", 'A', '2');

        System.out.print(engine.getCell(CellLocation.fromCellId('B', '1')).getEffectiveValue().evaluate()+"\n");


    }
}