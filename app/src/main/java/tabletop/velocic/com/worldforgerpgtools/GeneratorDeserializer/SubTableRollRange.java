package tabletop.velocic.com.worldforgerpgtools.GeneratorDeserializer;

public class SubTableRollRange
{
    private int minRoll;
    private int maxRoll;

    SubTableRollRange(int minRoll, int maxRoll)
    {
        this.minRoll = minRoll;
        this.maxRoll = maxRoll;
    }

    public int getMinRoll()
    {
        return minRoll;
    }

    public int getMaxRoll()
    {
        return maxRoll;
    }
}
