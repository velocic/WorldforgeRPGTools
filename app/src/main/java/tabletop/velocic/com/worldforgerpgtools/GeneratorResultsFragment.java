package tabletop.velocic.com.worldforgerpgtools;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import tabletop.velocic.com.worldforgerpgtools.GeneratorDeserializer.Generator;
import tabletop.velocic.com.worldforgerpgtools.GeneratorDeserializer.GeneratorCategory;
import tabletop.velocic.com.worldforgerpgtools.GeneratorDeserializer.GeneratorImporter;
import tabletop.velocic.com.worldforgerpgtools.GeneratorDeserializer.ResultItem;
import tabletop.velocic.com.worldforgerpgtools.GeneratorDeserializer.ResultRoller;

public class GeneratorResultsFragment extends android.support.v4.app.Fragment
{
    private static final String ARG_GENERATOR_PATH = "generator_path";
    private static final String ARG_NUMBER_OF_RESULTS_OVERRIDE = "number_of_results_override";
    private static final String INSTANCE_STATE_GENERATED_RESULT_SET = "generated_result_set";
    private final int IGNORE_NUMBER_OF_RESULTS_OVERRIDE = 0;

    private TextView resultTableName;
    private RecyclerView generatedItemList;
    private List<ResultItem> resultSet;
    private String generatorPath;

    public static GeneratorResultsFragment newInstance(String generatorPath, int numberOfResultsOverride)
    {
        GeneratorResultsFragment fragment = new GeneratorResultsFragment();

        Bundle args = new Bundle();
        args.putString(ARG_GENERATOR_PATH, generatorPath);
        args.putInt(ARG_NUMBER_OF_RESULTS_OVERRIDE, numberOfResultsOverride);
        fragment.setArguments(args);

        return fragment;
    }

    public static GeneratorResultsFragment newInstance(String generatorPath)
    {
        return newInstance(generatorPath, 0);
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

        resultTableName = view.findViewById(R.id.generated_results_table_name);
        generatedItemList = view.findViewById(R.id.generated_item_list);

        Bundle fragmentArguments = getArguments();

        ResultItem[] previousResults = (ResultItem[]) fragmentArguments.getParcelableArray(INSTANCE_STATE_GENERATED_RESULT_SET);
        generatorPath = fragmentArguments.getString(ARG_GENERATOR_PATH);
        int numberOfResultsOverride = fragmentArguments.getInt(ARG_NUMBER_OF_RESULTS_OVERRIDE);


        GeneratorImporter generatorImporter = GeneratorImporter.getInstance(view.getContext());
        GeneratorCategory rootCategory = generatorImporter.getRootGeneratorCategory();
        Generator generator = rootCategory.getGeneratorFromFullPath(generatorPath, rootCategory);

        int actualNumberOfResults = 1;

        if (numberOfResultsOverride == IGNORE_NUMBER_OF_RESULTS_OVERRIDE) {
            actualNumberOfResults = generator.getDefaultNumResultRolls();
        } else {
            actualNumberOfResults = numberOfResultsOverride;
        }

        if (previousResults == null) {
            ResultRoller roller = new ResultRoller(rootCategory);
            resultSet = roller.generateResultSet(generatorPath, actualNumberOfResults);
        } else {
            resultSet = new ArrayList<ResultItem>();
            for (ResultItem item : previousResults) {
                resultSet.add(item);
            }
        }

        resultTableName.setText(rootCategory.getGeneratorFromFullPath(generatorPath, rootCategory).getName());
        generatedItemList.setLayoutManager(new LinearLayoutManager(getActivity()));

        generatedItemList.setAdapter(
            new GeneratorResultsAdapter(resultSet)
        );

        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle outState)
    {
        saveState();
    }

    private void saveState()
    {
        ResultItem[] generatedResults = new ResultItem[resultSet.size()];

        for (int i = 0; i < resultSet.size(); ++i) {
            generatedResults[i] = resultSet.get(i);
        }

        Bundle args = getArguments();
        args.putString(ARG_GENERATOR_PATH, generatorPath);
        args.putParcelableArray(INSTANCE_STATE_GENERATED_RESULT_SET, generatedResults);
    }

    private class GeneratorResultsViewHolder extends RecyclerView.ViewHolder
        implements View.OnClickListener
    {
        private TextView resultQuantity;
        private TextView resultName;

        private ResultItem result;

        public GeneratorResultsViewHolder(LayoutInflater inflater, ViewGroup parent)
        {
            super(inflater.inflate(R.layout.list_item_generator_results, parent, false));
            itemView.setOnClickListener(this);

            resultQuantity = (TextView) itemView.findViewById(R.id.edit_text_generator_result_quantity);
            resultName = (TextView) itemView.findViewById(R.id.edit_text_generator_result_name);
        }

        public void bind(ResultItem result)
        {
            resultQuantity.setText(Integer.toString(result.getQuantity()));
            resultName.setText(result.getName());

            this.result = result;
        }

        @Override
        public void onClick(View v)
        {
            saveState();

            Fragment detailsFragment = GeneratorResultDetailsFragment.newInstance(result);
            getActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, detailsFragment)
                .addToBackStack(null)
                .commit();
        }
    }

    private class GeneratorResultsAdapter extends RecyclerView.Adapter<GeneratorResultsViewHolder>
    {
        private List<ResultItem> results;

        public GeneratorResultsAdapter(List<ResultItem> results)
        {
            this.results = results;
        }

        @Override
        public GeneratorResultsViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
        {
            LayoutInflater inflater = LayoutInflater.from(getActivity());

            return new GeneratorResultsViewHolder(inflater, parent);
        }

        @Override
        public void onBindViewHolder(GeneratorResultsViewHolder holder, int position)
        {
            ResultItem result = results.get(position);

            holder.bind(result);
        }

        @Override
        public int getItemCount()
        {
            return results.size();
        }

        @Override
        public int getItemViewType(int position)
        {
            return R.layout.list_item_generator_results;
        }
    }
}
