package tabletop.velocic.com.worldforgerpgtools.GeneratorDeserializer;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import tabletop.velocic.com.worldforgerpgtools.R;

public class GeneratorResultsFragment extends android.support.v4.app.Fragment
{
    private static final String ARG_GENERATOR_PATH = "generator_path";

    public static GeneratorResultsFragment newInstance(String generatorPath)
    {
        GeneratorResultsFragment fragment = new GeneratorResultsFragment();

        Bundle args = new Bundle();
        args.putString(ARG_GENERATOR_PATH, generatorPath);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_generator_results, container, false);

        String generatorPath = getArguments().getString(ARG_GENERATOR_PATH);

        GeneratorImporter generatorImporter = GeneratorImporter.getInstance(view.getContext());
        ResultRoller roller = new ResultRoller(generatorImporter.getRootGeneratorCategory());

        List<ResultItem> results = roller.generateResultSet(generatorPath, 5);

        return view;
    }
}
