package tabletop.velocic.com.worldforgerpgtools;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Map;

import tabletop.velocic.com.worldforgerpgtools.GeneratorDeserializer.ResultItem;

public class GeneratorResultDetailsFragment extends android.support.v4.app.Fragment
{
    private static final String ARG_RESULT_ITEM = "result_item";

    private TextView detailTableName;
    private RecyclerView detailList;

    public static GeneratorResultDetailsFragment newInstance(ResultItem resultItem)
    {
        GeneratorResultDetailsFragment fragment = new GeneratorResultDetailsFragment();

        Bundle args = new Bundle();
        args.putParcelable(ARG_RESULT_ITEM, resultItem);
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
        View view = inflater.inflate(R.layout.fragment_generated_item_details, container, false);

        detailTableName = view.findViewById(R.id.generated_item_detail_table_name);
        detailList = view.findViewById(R.id.generated_item_detail_list);

        ResultItem detailItem = getArguments().getParcelable(ARG_RESULT_ITEM);
        detailTableName.setText(detailItem.getName());

        detailList.setLayoutManager(new LinearLayoutManager(getActivity()));
        detailList.setAdapter(
            new ResultDetailsAdapter(
                detailItem
            )
        );

        return view;
    }

    private class ResultDetailsViewHolder extends RecyclerView.ViewHolder
    {
        private String detailItemName;
        private String detailItemDescription;
        private TextView descriptionTitle;
        private TextView descriptionContent;

        public ResultDetailsViewHolder(LayoutInflater inflater, ViewGroup parent)
        {
            super(inflater.inflate(R.layout.list_item_generated_item_details, parent, false));

            descriptionTitle = itemView.findViewById(R.id.user_provided_description_title);
            descriptionContent = itemView.findViewById(R.id.user_provided_description_content);
        }

        public void bind(String detailItemName, String detailItemDescription)
        {
            this.detailItemName = detailItemName;
            this.detailItemDescription = detailItemDescription;

            descriptionTitle.setText(this.detailItemName);
            descriptionContent.setText(this.detailItemDescription);
        }
    }

    private class ResultDetailsAdapter extends RecyclerView.Adapter<ResultDetailsViewHolder>
    {
        private ResultItem detailItem;

        public ResultDetailsAdapter(ResultItem detailItem)
        {
            this.detailItem = detailItem;
        }

        @Override
        public ResultDetailsViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
        {
            LayoutInflater inflater = LayoutInflater.from(getActivity());

            return new ResultDetailsViewHolder(inflater, parent);
        }

        @Override
        public void onBindViewHolder(ResultDetailsViewHolder holder, int position)
        {
            Map.Entry<String, String> entry = detailItem.getDetailDataFieldByIndex(position);

            holder.bind(entry.getKey(), entry.getValue());
        }

        @Override
        public int getItemCount()
        {
            return detailItem.getNumDetailDataFields();
        }

        @Override
        public int getItemViewType(int position)
        {
            return R.layout.list_item_generated_item_details;
        }
    }
}
