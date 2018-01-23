package tabletop.velocic.com.worldforgerpgtools;

import android.os.Bundle;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import tabletop.velocic.com.worldforgerpgtools.GeneratorDeserializer.GeneratorCategory;
import tabletop.velocic.com.worldforgerpgtools.GeneratorDeserializer.GeneratorImporter;

public class GeneratorCategorySelectionFragment extends android.support.v4.app.Fragment
{
    String currentCategoryName = "";
    GeneratorCategory currentCategory = null;

    private static final String ARG_CATEGORY_PATH = "category_path";

    private GridView gridView;

    public static GeneratorCategorySelectionFragment newInstance(String categoryPath)
    {
        GeneratorCategorySelectionFragment fragment = new GeneratorCategorySelectionFragment();

        Bundle args = new Bundle();
        args.putString(ARG_CATEGORY_PATH, categoryPath);
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
        View view = inflater.inflate(R.layout.fragment_generators, container, false);

        Bundle fragmentArgs = getArguments();
        if (fragmentArgs != null) {
            currentCategoryName = fragmentArgs.getString(ARG_CATEGORY_PATH);
        }

        GeneratorImporter generatorImporter = GeneratorImporter.getInstance(view.getContext());
        GeneratorCategory rootCategory = generatorImporter.getRootGeneratorCategory();
        currentCategory = generatorImporter.getRootGeneratorCategory().getCategoryFromFullPath(currentCategoryName, rootCategory);

        return view;
    }
}