package tabletop.velocic.com.worldforgerpgtools

import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_generated_item_details.*
import kotlinx.android.synthetic.main.list_item_generated_item_details.view.*
import tabletop.velocic.com.worldforgerpgtools.GeneratorDeserializer.ResultItem

class GeneratorResultDetailsFragment : androidx.fragment.app.Fragment()
{
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_generated_item_details, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val detailItem = arguments?.getParcelable(ARG_RESULT_ITEM) as ResultItem?
            ?: throw IllegalStateException("Attempting to display detail information on a generated" +
                " result item, but was provided no item to display.")

        val validatedActivityInstance = activity
            ?: throw IllegalStateException("GeneratorResultDetailsFragment cannot" +
                " exist without a valid FragmentActivity instance.")

        generated_item_detail_table_name.text = detailItem.name
        generated_item_detail_list.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(validatedActivityInstance)
        generated_item_detail_list.adapter = ResultDetailsAdapter(validatedActivityInstance, detailItem)
    }

    companion object {
        const val ARG_RESULT_ITEM = "result_item"

        fun newInstance(resultItem: ResultItem) : GeneratorResultDetailsFragment {
            val fragment = GeneratorResultDetailsFragment()

            fragment.arguments = Bundle().apply {
                putParcelable(ARG_RESULT_ITEM, resultItem)
            }

            return fragment
        }
    }
}

private class ResultDetailsAdapter(
        private val activity: androidx.fragment.app.FragmentActivity,
        private val detailItem: ResultItem
) : androidx.recyclerview.widget.RecyclerView.Adapter<ResultDetailsViewHolder>()
{
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ResultDetailsViewHolder {
        val view = LayoutInflater.from(activity).inflate(R.layout.list_item_generated_item_details, parent, false)

        return ResultDetailsViewHolder(view)
    }

    override fun onBindViewHolder(holder: ResultDetailsViewHolder, position: Int) {
        val entry = detailItem.getDetailDataFieldByIndex(position)
            ?: throw IllegalStateException("Indexed into what should be a valid detail data field" +
                " entry, but retrieved no data.")

        holder.bind(entry.key, entry.value)
    }

    override fun getItemCount(): Int = detailItem.numDetailDataFields

    override fun getItemViewType(position: Int): Int = R.layout.list_item_generated_item_details
}
private class ResultDetailsViewHolder(
    view: View
) : androidx.recyclerview.widget.RecyclerView.ViewHolder(view)
{
    private val descriptionTitle = itemView.user_provided_description_title
    private val descriptionContent = itemView.user_provided_description_content

    fun bind(detailItemName: String, detailItemDescription: String)
    {
        descriptionTitle.text = detailItemName
        descriptionContent.text = detailItemDescription
    }
}
