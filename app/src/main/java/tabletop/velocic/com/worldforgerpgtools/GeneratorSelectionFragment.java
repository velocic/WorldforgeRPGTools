package tabletop.velocic.com.worldforgerpgtools;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import tabletop.velocic.com.worldforgerpgtools.GeneratorDeserializer.Generator;
import tabletop.velocic.com.worldforgerpgtools.GeneratorDeserializer.GeneratorCategory;
import tabletop.velocic.com.worldforgerpgtools.GeneratorDeserializer.GeneratorImporter;
import tabletop.velocic.com.worldforgerpgtools.GeneratorDeserializer.GeneratorResultsFragment;

public class GeneratorSelectionFragment extends android.support.v4.app.Fragment
{
    String currentCategoryName = "";
    GeneratorCategory currentCategory = null;

    private static final String ARG_CATEGORY_PATH = "category_path";

    private GridView gridView;
    private GeneratorSelectionAdapter gridViewAdapter;

    public static GeneratorSelectionFragment newInstance(String categoryPath)
    {
        GeneratorSelectionFragment fragment = new GeneratorSelectionFragment();

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

        gridView = (GridView) view.findViewById(R.id.generator_selection);
        gridViewAdapter = new GeneratorSelectionAdapter(view.getContext(), currentCategory);
        gridView.setAdapter(gridViewAdapter);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View gridItemView, int i, long l)
            {
                onGridItemClicked(gridItemView);
            }
        });

        return view;
    }

    private void onGridItemClicked(View view)
    {
        GeneratorOrCategoryViewHolder viewHolder = (GeneratorOrCategoryViewHolder) view.getTag();

        if (viewHolder.getCategory() == null) {
            Fragment generatedResultsFragment = GeneratorResultsFragment.newInstance(
                currentCategory.getGeneratorFullPath(viewHolder.getGenerator())
            );
            getActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, generatedResultsFragment)
                .addToBackStack(null)
                .commit();

            return;
        }

        Fragment subCategoryFragment = newInstance(viewHolder.getCategory().getAssetPath());
        getActivity().getSupportFragmentManager().beginTransaction()
            .replace(R.id.fragment_container, subCategoryFragment)
            .addToBackStack(null)
            .commit();
    }

    private class GeneratorSelectionAdapter extends BaseAdapter
    {
        private Context context;
        private GeneratorCategory currentCategoryNode;

        public GeneratorSelectionAdapter(Context context, GeneratorCategory currentCategoryNode)
        {
            this.context = context;
            this.currentCategoryNode = currentCategoryNode;
        }

        @Override
        public int getCount()
        {
            int numCategories = currentCategory.getNumChildCategories();
            int numGenerators = currentCategory.getNumGenerators();

            return numCategories + numGenerators;
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
            GeneratorOrCategoryViewHolder viewHolder = null;
            boolean isCategoryIndex = isCategoryIndex(i);

            View currentView = view;
            if (currentView == null) {
                LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
                currentView = layoutInflater.inflate(R.layout.grid_item_generators_and_categories, viewGroup, false);

                if (isCategoryIndex) {
                    viewHolder = new GeneratorOrCategoryViewHolder(
                        currentView,
                        currentCategoryNode.getChildCategory(i)
                    );
                } else {
                    viewHolder = new GeneratorOrCategoryViewHolder(
                        currentView,
                        currentCategoryNode.getGenerator(i)
                    );
                }

                currentView.setTag(viewHolder);

                return currentView;
            }

            viewHolder = (GeneratorOrCategoryViewHolder) currentView.getTag();
            if (isCategoryIndex) {
                viewHolder.bind(currentCategoryNode.getChildCategory(i));
            } else {
                viewHolder.bind(currentCategoryNode.getGenerator(i));
            }

            return currentView;
        }

        private boolean isCategoryIndex(int index)
        {
            if (index < currentCategoryNode.getNumChildCategories()) {
                return true;
            }

            return false;
        }
    }

    private class GeneratorOrCategoryViewHolder
    {
        private ImageView generatorOrCategoryIcon;
        private TextView generatorOrCategoryText;

        private GeneratorCategory category = null;
        private Generator generator = null;

        public GeneratorOrCategoryViewHolder(View view, Generator generator)
        {
            generatorOrCategoryIcon = (ImageView) view.findViewById(R.id.generators_and_categories_grid_item_icon);
            generatorOrCategoryText = (TextView) view.findViewById(R.id.generators_and_categories_grid_item_text);
            this.generator = generator;

            setViewFromGeneratorOrCategory();
        }

        public GeneratorOrCategoryViewHolder(View view, GeneratorCategory category)
        {
            generatorOrCategoryIcon = (ImageView) view.findViewById(R.id.generators_and_categories_grid_item_icon);
            generatorOrCategoryText = (TextView) view.findViewById(R.id.generators_and_categories_grid_item_text);
            this.category = category;

            setViewFromGeneratorOrCategory();
        }

        public void bind(Generator generator)
        {
            this.generator = generator;
            this.category = null;

            setViewFromGeneratorOrCategory();
        }

        public void bind(GeneratorCategory category)
        {
            this.category = category;
            this.generator = null;

            setViewFromGeneratorOrCategory();
        }

        public Generator getGenerator() {return generator;}
        public GeneratorCategory getCategory() {return category;}

        private void setViewFromGeneratorOrCategory()
        {
            if (generator != null) {
                this.generatorOrCategoryIcon.setImageResource(R.drawable.ic_generate_results);
                this.generatorOrCategoryText.setText(generator.getName());
                return;
            }

            this.generatorOrCategoryIcon.setImageResource(R.drawable.ic_select_generator_category);
            this.generatorOrCategoryText.setText(category.getName());
            return;
        }
    }
}
