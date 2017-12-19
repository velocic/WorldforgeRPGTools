package tabletop.velocic.com.worldforgerpgtools.GeneratorDeserializer;

import java.util.ArrayList;
import java.util.List;

public class ResultRoller
{
    private GeneratorCategory rootGeneratorCategory;

    public ResultRoller(GeneratorCategory rootGeneratorCategory)
    {
        this.rootGeneratorCategory = rootGeneratorCategory;
    }

    public List<ResultItem> generateResultSet(String fullyQualifiedGeneratorPath)
    {
        List<ResultItem> resultSet = new ArrayList<ResultItem>();

        Generator randomTable = rootGeneratorCategory.getGeneratorFromFullPath(fullyQualifiedGeneratorPath, rootGeneratorCategory);

        return null;
    }
}
