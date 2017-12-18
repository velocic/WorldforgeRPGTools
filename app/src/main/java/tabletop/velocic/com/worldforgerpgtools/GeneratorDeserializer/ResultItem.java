package tabletop.velocic.com.worldforgerpgtools.GeneratorDeserializer;

import java.util.HashMap;
import java.util.Map;

public class ResultItem
{
    private int quantity;
    private String tableGeneratedFrom;
    private String name;
    private Map<String, String> detailData;

    public ResultItem(String tableGeneratedFrom, String name)
    {
        quantity = 1;
        this.tableGeneratedFrom = tableGeneratedFrom;
        this.name = name;
        detailData = new HashMap<String, String>();
    }

    public int getQuantity()
    {
        return quantity;
    }

    public void setQuantity(int quantity)
    {
        this.quantity = quantity;
    }

    public String getName()
    {
        return name;
    }

    public String getTableGeneratedFrom()
    {
        return tableGeneratedFrom;
    }

    public void addDetailDataField(String fieldName, String fieldContent)
    {
        detailData.put(fieldName, fieldContent);
    }

    public Map<String, String> getDetailData()
    {
        return detailData;
    }
}