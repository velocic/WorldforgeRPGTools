package tabletop.velocic.com.worldforgerpgtools.generatorcreation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.fragment_result_item_details.*
import kotlinx.android.synthetic.main.list_item_result_item_details_entry.view.*
import tabletop.velocic.com.worldforgerpgtools.R

class ResultItemDetailsFragment : Fragment()
{
    private val resultItemDetails = mutableListOf<ResultItemDetail>()
    private lateinit var detailsRecyclerView: RecyclerView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater.inflate(R.layout.fragment_result_item_details, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val resultItemName = arguments?.getString(ARG_RESULT_ITEM_NAME) ?: ""
        val resultRowIndex = arguments?.getInt(ARG_ROW_INDEX) ?:
            throw IllegalArgumentException("Missing row index argument required to associate" +
                " result item detail data with the correct result item entry.")

        val layoutInflater = LayoutInflater.from(activity) ?:
            throw IllegalStateException("Attempted to create a LayoutInflater from a null Activity instance")

        result_item_details_title.text = resources.getString(R.string.result_item_details_title).format(resultItemName)

        detailsRecyclerView = result_item_details_content
        detailsRecyclerView.layoutManager = LinearLayoutManager(activity)
        detailsRecyclerView.adapter = ResultItemDetailsAdapter(resultItemDetails, layoutInflater)

        result_item_details_add_row.setOnClickListener {
            resultItemDetails.add(ResultItemDetail("", ""))
            (detailsRecyclerView.adapter as ResultItemDetailsAdapter).notifyDataSetChanged()
        }
    }

    companion object {
        private const val ARG_ROW_INDEX = "row_index"
        private const val ARG_RESULT_ITEM_NAME = "result_item_name"

        fun newInstance(rowIndex: Int, resultItemName: String) : Fragment {
            return ResultItemDetailsFragment().apply {
                arguments = bundleOf(
                    ARG_ROW_INDEX to rowIndex,
                    ARG_RESULT_ITEM_NAME to resultItemName
                )
            }
        }
    }
}

private class ResultItemDetailsAdapter(
    private val resultItemDetails: MutableList<ResultItemDetail>,
    private val layoutInflater: LayoutInflater
) : RecyclerView.Adapter<ResultItemDetailsViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ResultItemDetailsViewHolder {
        val view = layoutInflater.inflate(R.layout.list_item_result_item_details_entry, parent, false)
        return ResultItemDetailsViewHolder(view, this::deleteDetailItemEventHandler)
    }

    override fun getItemCount(): Int = resultItemDetails.size

    override fun onBindViewHolder(holder: ResultItemDetailsViewHolder, position: Int) =
        holder.bind(position, resultItemDetails[position].name, resultItemDetails[position].content)

    override fun getItemViewType(position: Int): Int = R.layout.list_item_result_item_details_entry

    private fun deleteDetailItemEventHandler(detailItemIndex: Int) {
        resultItemDetails.removeAt(detailItemIndex)
        notifyDataSetChanged()
    }
}

private class ResultItemDetailsViewHolder(
    val view: View,
    val deleteDetailItemEventHandler: (Int) -> Unit
) : RecyclerView.ViewHolder(view) {
    private val name = view.entry_name
    private val content = view.entry_content

    fun bind(rowIndex: Int, detailName: String, detailContent: String) {
        name.setText(detailName, TextView.BufferType.EDITABLE)
        content.setText(detailContent, TextView.BufferType.EDITABLE)
        view.entry_delete.setOnClickListener { deleteDetailItemEventHandler(rowIndex) }
    }
}

data class ResultItemDetail(val name: String, val content: String)
