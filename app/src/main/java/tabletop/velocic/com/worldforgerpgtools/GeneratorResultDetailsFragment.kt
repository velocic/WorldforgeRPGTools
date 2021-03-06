package tabletop.velocic.com.worldforgerpgtools

import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import kotlinx.android.synthetic.main.fragment_generated_item_details.*
import kotlinx.android.synthetic.main.list_item_generated_item_details.view.*
import tabletop.velocic.com.worldforgerpgtools.persistence.ResultItem

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
        generated_item_detail_list.layoutManager = LinearLayoutManager(validatedActivityInstance)
        generated_item_detail_list.adapter = ResultDetailsAdapter(validatedActivityInstance, detailItem)
    }

    companion object {
        const val ARG_RESULT_ITEM = "result_item"

        fun newInstance(resultItem: ResultItem) : GeneratorResultDetailsFragment =
            GeneratorResultDetailsFragment().apply {
                arguments = bundleOf(
                    ARG_RESULT_ITEM to resultItem
                )
            }
    }
}

private class ResultDetailsAdapter(
        private val activity: FragmentActivity,
        private val detailItem: ResultItem
) : RecyclerView.Adapter<ResultDetailsViewHolder>()
{
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ResultDetailsViewHolder {
        val view = LayoutInflater.from(activity).inflate(R.layout.list_item_generated_item_details, parent, false)

        return ResultDetailsViewHolder(view)
    }

    override fun onBindViewHolder(holder: ResultDetailsViewHolder, position: Int) {
        val entry = detailItem.detailData[position]

        holder.bind(entry.name, entry.content)
    }

    override fun getItemCount(): Int = detailItem.detailData.size

    override fun getItemViewType(position: Int): Int = R.layout.list_item_generated_item_details
}
private class ResultDetailsViewHolder(
    view: View
) : RecyclerView.ViewHolder(view)
{
    private val descriptionTitle = itemView.user_provided_description_title
    private val descriptionContent = itemView.user_provided_description_content

    fun bind(detailItemName: String, detailItemDescription: String)
    {
        descriptionTitle.text = detailItemName
        descriptionContent.text = detailItemDescription
    }
}
