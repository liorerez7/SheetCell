package Utility;
import GeneratedClasses.STLCell;
import GeneratedClasses.STLSheet;
import CoreParts.impl.SheetCellImp;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Unmarshaller;
import java.io.InputStream;


public class EngineUtilies {

    public static STLSheet deserializeFrom(InputStream in) throws JAXBException {
        JAXBContext jaxbContext = JAXBContext.newInstance(STLSheet.class);
        Unmarshaller u = jaxbContext.createUnmarshaller();
        return (STLSheet) u.unmarshal(in);
    }
}
