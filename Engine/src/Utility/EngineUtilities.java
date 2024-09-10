package Utility;
import CoreParts.impl.DtoComponents.DtoSheetCell;
import GeneratedClassesEx2.STLSheet;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Unmarshaller;
import java.io.InputStream;


public class EngineUtilities {


    public static STLSheet deserializeFrom(InputStream in) throws JAXBException {
        JAXBContext jaxbContext = JAXBContext.newInstance(STLSheet.class);
        Unmarshaller u = jaxbContext.createUnmarshaller();
        return (STLSheet) u.unmarshal(in);
    }

    public static DtoSheetCell sortSheetCell(String range, String args, DtoSheetCell dtoSheetCell){

        return SheetCellSorter.sortSheetCell(range, args, dtoSheetCell);
    }


}
