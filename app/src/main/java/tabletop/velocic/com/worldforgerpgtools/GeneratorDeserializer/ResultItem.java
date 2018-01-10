package tabletop.velocic.com.worldforgerpgtools.GeneratorDeserializer;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Map;

public class ResultItem implements Parcelable
{
    private int quantity;
    private String tableGeneratedFrom;
    private String name;
    private Map<String, String> detailData;

    public ResultItem(String tableGeneratedFrom, String name, Map<String, String> detailData)
    {
        quantity = 1;
        this.tableGeneratedFrom = tableGeneratedFrom;
        this.name = name;
        this.detailData = detailData;
    }

    public ResultItem(Parcel in)
    {
        quantity = in.readInt();
        tableGeneratedFrom = in.readString();
        name = in.readString();

        int numMapEntries = in.readInt();

        for (int i = 0; i < numMapEntries; ++i) {
            String key = in.readString();
            String value = in.readString();

            detailData.put(key, value);
        }
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

    public Map.Entry<String, String> getDetailDataFieldByIndex(int index)
    {
        if (index >= detailData.size()) {
            return null;
        }

        int count = 0;
        Map.Entry<String, String> result = null;

        for (Map.Entry<String, String> entry : detailData.entrySet()) {
            if (count < index) {
                ++count;
                continue;
            }

            result = entry;
            break;
        }

        return result;
    }

    public int getNumDetailDataFields()
    {
        return detailData.size();
    }

    public Map<String, String> getDetailData()
    {
        return detailData;
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator()
    {
        public ResultItem createFromParcel(Parcel in)
        {
            return new ResultItem(in);
        }

        public ResultItem[] newArray(int size)
        {
            return new ResultItem[size];
        }
    };

    @Override
    public int describeContents()
    {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags)
    {
        parcel.writeInt(quantity);
        parcel.writeString(tableGeneratedFrom);
        parcel.writeString(name);
        parcel.writeInt(detailData.size());

        for (Map.Entry<String, String> entry : detailData.entrySet()) {
            parcel.writeString(entry.getKey());
            parcel.writeString(entry.getValue());
        }
    }
}