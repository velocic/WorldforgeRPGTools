package tabletop.velocic.com.worldforgerpgtools;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.List;

import tabletop.velocic.com.worldforgerpgtools.GeneratorDeserializer.GeneratorImporter;
import tabletop.velocic.com.worldforgerpgtools.GeneratorDeserializer.ResultItem;
import tabletop.velocic.com.worldforgerpgtools.GeneratorDeserializer.ResultRoller;

public class GeneratorSelectionFragment extends android.support.v4.app.Fragment
{
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_generators, container, false);


        GeneratorImporter generatorImporter = new GeneratorImporter();
        generatorImporter.importGenerators(getContext());

        ResultRoller roller = new ResultRoller(generatorImporter.getRootGeneratorCategory().getCategory("SwordsAndWizardry"));
        List<ResultItem> results = roller.generateResultSet("Items/Major Magic Items", 5);

        return view;
    }

    private class GeneratorSelectionAdapter extends BaseAdapter
    {
        @Override
        public int getCount()
        {
            return 0;
        }

        @Override
        public Object getItem(int i)
        {
            return null;
        }

        @Override
        public long getItemId(int i)
        {
            return 0;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup)
        {
            return null;
        }
    }
}
