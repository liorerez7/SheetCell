package CoreParts.TestCases;

import CoreParts.api.Engine;
import CoreParts.api.controller.CommandManager;
import CoreParts.api.controller.InputHandler;
import CoreParts.impl.InnerSystemComponents.EngineImpl;
import CoreParts.impl.controller.commands.InputHandlerImpl;
import CoreParts.smallParts.CellLocation;
import CoreParts.impl.controller.CommandManagerImpl;
import CoreParts.smallParts.CellLocationFactory;
import CoreParts.impl.InnerSystemComponents.EngineImpl;

public class TestAbs {
    public static void main(String[] args) {

        EngineImpl TestEngine1 = new EngineImpl();
        TestEngine1.updateCell("{ABS, -10}", 'A', '1');
        TestEngine1.updateCell("{REF, A1}", 'A', '2');

        System.out.println(TestEngine1.getRequestedCell("A1").getEffectiveValue().getValue());
        System.out.println(TestEngine1.getRequestedCell("A2").getEffectiveValue().getValue());

        TestEngine1.updateCell("{DIVIDE, 10, 0}", 'A', '3');
        System.out.println(TestEngine1.getRequestedCell("A3").getEffectiveValue().getValue());
    }
}
