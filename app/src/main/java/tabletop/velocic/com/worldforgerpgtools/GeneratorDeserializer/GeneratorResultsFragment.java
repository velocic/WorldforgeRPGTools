package tabletop.velocic.com.worldforgerpgtools.GeneratorDeserializer;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import tabletop.velocic.com.worldforgerpgtools.R;

public class GeneratorResultsFragment extends android.support.v4.app.Fragment
{
    private static final String ARG_GENERATOR_PATH = "generator_path";
    private TextView resultTableName;
    private RecyclerView generatedItemList;

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

        resultTableName = view.findViewById(R.id.generated_results_table_name);
        generatedItemList = view.findViewById(R.id.generated_item_list);

        String generatorPath = getArguments().getString(ARG_GENERATOR_PATH);

        GeneratorImporter generatorImporter = GeneratorImporter.getInstance(view.getContext());
        GeneratorCategory rootCategory = generatorImporter.getRootGeneratorCategory();
        ResultRoller roller = new ResultRoller(rootCategory);

        resultTableName.setText(rootCategory.getGeneratorFromFullPath(generatorPath, rootCategory).getName());

        generatedItemList.setLayoutManager(new LinearLayoutManager(getActivity()));
        generatedItemList.setAdapter(
            new GeneratorResultsAdapter(
                roller.generateResultSet(generatorPath,5)
            )
        );

        return view;
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
            //Open detail screen for the clicked item
            //This resultItem's getDetailData() function holds the content
            //for that screen
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
