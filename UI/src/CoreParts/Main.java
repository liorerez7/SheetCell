package CoreParts;

import CoreParts.api.Engine;
import CoreParts.api.controller.CommandManager;
import CoreParts.api.controller.InputHandler;
import CoreParts.impl.InnerSystemComponents.EngineImpl;
import CoreParts.impl.controller.commands.InputHandlerImpl;
import CoreParts.smallParts.CellLocation;
import CoreParts.impl.controller.CommandManagerImpl;
import CoreParts.smallParts.CellLocationFactory;

public class Main {

    public static void main(String[] args) {

        // Create instances of the implementations
        Engine engine = new EngineImpl();

//        CommandManager commandManager = new CommandManagerImpl(engine);
//        InputHandler inputHandler = new InputHandlerImpl(commandManager);
//        // Create MenuHandler with the Engine and CommandManager
//        MenuHandler menuHandler = new MenuHandler(engine, commandManager, inputHandler);
//        menuHandler.run();

        engine.updateCell("2", 'A', '1');
        engine.updateCell("10", 'A', '2');


//        engine.updateCell("{SUB, goodbyeAndHello, 7, 10}", 'A', '2');
//        System.out.println(engine.getRequestedCell("A2").getEffectiveValue().getValue());


        System.out.println(engine.getRequestedCell("A4").getEffectiveValue().getValue());








//
//        try{
//            engine.updateCell("2", 'A', '1');
//            engine.updateCell("{REF, A1}", 'A', '2');
//            engine.updateCell("{REF, A2}", 'A', '3');
//            engine.updateCell("{REF, A3}", 'A', '4');
//            engine.updateCell("{REF, A4}", 'A', '2');
//        }catch (Exception e){
//            System.out.println(e.getMessage());
//        }
//
//        EngineImpl TestEngine1 = new EngineImpl();
//        EngineImpl TestEngine2 = new EngineImpl();
//        EngineImpl TestEngine3 = new EngineImpl();
//
//        EngineTestOne(TestEngine1);
//        EngineTestTwo(TestEngine2);
//        EngineTestThree(TestEngine3);
    }


}