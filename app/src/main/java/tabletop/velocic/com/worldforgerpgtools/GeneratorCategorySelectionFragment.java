package tabletop.velocic.com.worldforgerpgtools;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import tabletop.velocic.com.worldforgerpgtools.GeneratorDeserializer.GeneratorCategory;
import tabletop.velocic.com.worldforgerpgtools.GeneratorDeserializer.GeneratorImporter;

public class GeneratorCategorySelectionFragment extends android.support.v4.app.Fragment
{
    String currentCategoryName = "";
    GeneratorCategory currentCategory = null;

    private static final String ARG_CATEGORY_PATH = "category_path";
    public static final String EXTRA_SELECTED_CATEGORY = "tabletop.velocic.com.worldforgergptools.selected_category";

    private Button selectButton;
    private GridView gridView;
    private TextView currentlySelectedCategoryText;
    private GeneratorCategorySelectionAdapter gridViewAdapter;

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
        View view = inflater.inflate(R.layout.fragment_generator_categories, container, false);

        Bundle fragmentArgs = getArguments();
        if (fragmentArgs != null) {
            currentCategoryName = fragmentArgs.getString(ARG_CATEGORY_PATH);
        }

        GeneratorImporter generatorImporter = GeneratorImporter.getInstance(view.getContext());
        GeneratorCategory rootCategory = generatorImporter.getRootGeneratorCategory();
        currentCategory = generatorImporter.getRootGeneratorCategory().getCategoryFromFullPath(currentCategoryName, rootCategory);

        selectButton = (Button) view.findViewById(R.id.button_select_category);
        selectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                onSelectButtonClicked();
            }
        });

        currentlySelectedCategoryText = (TextView) view.findViewById(R.id.textview_currently_selected_category);
        currentlySelectedCategoryText.setText(currentCategoryName);

        gridView = (GridView) view.findViewById(R.id.generator_selection);
        gridViewAdapter = new GeneratorCategorySelectionAdapter(currentCategory);
        gridView.setAdapter(gridViewAdapter);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View gridItemView, int i, long l)
            {
                onGridItemClicked(gridItemView);
            }
        });

        selectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                onSelectButtonClicked();
            }
        });

        return view;
    }

    private void onSelectButtonClicked()
    {
        /*
        TODO:
            - open modal dialog asking for your "generator name"
            - pass it the string from this fragment containing the full generator path
            - return from that dialog to the "create new generator" fragment, closing
                the possibly many instances of this fragment between the modal and
                that screen

            NOTE: fragmentManager popBackStack has an overload that takes an ID,
            and it will pop every single backstack instance between here and that ID
         */
        sendResult(Activity.RESULT_OK, currentlySelectedCategoryText.getText().toString());
    }

    private void sendResult(int resultCode, String selectedCategoryPath)
    {
        if (getTargetFragment() == null) {
            return;
        }

        Intent intent = new Intent();
        intent.putExtra(EXTRA_SELECTED_CATEGORY, selectedCategoryPath);

        getTargetFragment().onActivityResult(getTargetRequestCode(), resultCode, intent);

        getActivity().getSupportFragmentManager().popBackStack(
            GeneratorCreationFragment.BACK_STACK_GENERATOR_CREATION_FRAGMENT,
            FragmentManager.POP_BACK_STACK_INCLUSIVE
        );
    }

    private void onGridItemClicked(View view)
    {
        GeneratorCategoryViewHolder viewHolder = (GeneratorCategoryViewHolder) view.getTag();

        Fragment subCategoryFragment = newInstance(viewHolder.getCategory().getAssetPath());

        subCategoryFragment.setTargetFragment(getTargetFragment(), getTargetRequestCode());

        getActivity().getSupportFragmentManager().beginTransaction()
            .replace(R.id.fragment_container, subCategoryFragment)
            .addToBackStack(null)
            .commit();
    }

    private class GeneratorCategorySelectionAdapter extends BaseAdapter
    {
        private GeneratorCategory currentCategoryNode;

        public GeneratorCategorySelectionAdapter(GeneratorCategory currentCategoryNode)
        {
            this.currentCategoryNode = currentCategoryNode;
        }

        @Override
        public int getCount()
        {
            return currentCategoryNode.getNumChildCategories();
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
            GeneratorCategoryViewHolder viewHolder = null;

            View currentView = view;
            if (currentView == null) {
                LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
                currentView = layoutInflater.inflate(R.layout.grid_item_generators_and_categories, viewGroup, false);

                viewHolder = new GeneratorCategoryViewHolder(
                    currentView,
                    currentCategoryNode.getChildCategory(i)
                );

                currentView.setTag(viewHolder);

                return currentView;
            }

            viewHolder = (GeneratorCategoryViewHolder) currentView.getTag();
            viewHolder.bind(currentCategoryNode.getChildCategory(i));

            return currentView;
        }
    }

    private class GeneratorCategoryViewHolder
    {
        private ImageView categoryIcon;
        private TextView categoryText;
        private GeneratorCategory category = null;

        public GeneratorCategoryViewHolder(View view, GeneratorCategory category)
        {
            this.category = category;
            categoryIcon = (ImageView) view.findViewById(R.id.generators_and_categories_grid_item_icon);
            categoryText = (TextView) view.findViewById(R.id.generators_and_categories_grid_item_text);

            bind(category);
        }

        public void bind(GeneratorCategory category)
        {
            this.category = category;
            this.categoryIcon.setImageResource(R.drawable.ic_select_generator_category);
            this.categoryText.setText(category.getName());
        }

        public GeneratorCategory getCategory()
        {
            return category;
        }
    }
}