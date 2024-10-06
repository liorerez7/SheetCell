import CoreParts.api.Engine;
import CoreParts.impl.DtoComponents.DtoSheetCell;
import CoreParts.impl.InnerSystemComponents.EngineImpl;
import com.google.gson.Gson;


public class testEngineDependency {
    public static void main(String[] args) {
        System.out.println("This is a test for the engine dependency");
        Engine engine = new EngineImpl();
        DtoSheetCell dtoSheetCell = engine.getSheetCell();
        Gson gson = new Gson();
        String a = gson.toJson(dtoSheetCell);
        System.out.println(a);
    }
}
