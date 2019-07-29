package tabletop.velocic.com.worldforgerpgtools.generatorcreation

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
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
import tabletop.velocic.com.worldforgerpgtools.generatordeserializer.ResultItemDetail

class ResultItemDetailsFragment : Fragment()
{
    private var resultItemRow = 0
    private lateinit var detailsRecyclerView: RecyclerView
    private val resultItemDetails
        get() = (detailsRecyclerView.adapter as ResultItemDetailsAdapter).resultItemDetails

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater.inflate(R.layout.fragment_result_item_details, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        arguments?.let {
            populateViewContent(it)
            arguments = null
        } ?: savedInstanceState?.let {
            populateViewContent(it)
        } ?: throw IllegalStateException("Attempted to create ResultItemDetailsFragment with no arguments.")
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putParcelableArrayList(EXTRA_RESULT_ITEM_DETAILS, resultItemDetails)
        outState.putInt(ARG_ROW_INDEX, resultItemRow)

        super.onSaveInstanceState(outState)
    }

    private fun populateViewContent(viewData: Bundle) {
        if (!viewData.containsKey(ARG_ROW_INDEX)) {
            throw IllegalArgumentException("Missing row index argument required to associate" +
                    " result item detail data with the correct result item entry.")
        }

        val layoutInflater = LayoutInflater.from(activity) ?:
        throw IllegalStateException("Attempted to create a LayoutInflater from a null Activity instance")

        detailsRecyclerView = result_item_details_content
        detailsRecyclerView.layoutManager = LinearLayoutManager(activity)
        detailsRecyclerView.adapter = ResultItemDetailsAdapter(arrayListOf(), layoutInflater)

        if (viewData.containsKey(EXTRA_RESULT_ITEM_DETAILS)) {
            resultItemDetails.addAll(viewData.getParcelableArrayList(EXTRA_RESULT_ITEM_DETAILS))
        }

        val resultItemName = viewData.getString(ARG_RESULT_ITEM_NAME) ?: ""
        resultItemRow = viewData.getInt(ARG_ROW_INDEX)

        result_item_details_title.text = resources.getString(R.string.result_item_details_title).format(resultItemName)
        result_item_details_add_row.setOnClickListener {
            resultItemDetails.add(ResultItemDetail("", ""))
            (detailsRecyclerView.adapter as ResultItemDetailsAdapter).notifyDataSetChanged()
        }
        result_item_details_submit.setOnClickListener { sendResult(resultItemRow, resultItemDetails) }
    }

    private fun sendResult(resultItemRow: Int, resultItemDetails: ArrayList<ResultItemDetail>) {
        if (resultItemDetails.isEmpty()) {
            targetFragment?.onActivityResult(targetRequestCode, Activity.RESULT_CANCELED, Intent())
        }

        val intent = Intent().apply {
            putExtra(EXTRA_ROW_INDEX, resultItemRow)
            putParcelableArrayListExtra(EXTRA_RESULT_ITEM_DETAILS, resultItemDetails)
        }

        targetFragment?.onActivityResult(targetRequestCode, Activity.RESULT_OK, intent)
        fragmentManager?.popBackStack()
    }

    companion object {
        private const val ARG_ROW_INDEX = "row_index"
        private const val ARG_RESULT_ITEM_NAME = "result_item_name"
        const val EXTRA_ROW_INDEX = "tabletop.velocic.com.worldforgerpgtools.row_index"
        const val EXTRA_RESULT_ITEM_DETAILS = "tabletop.velocic.com.worldforgerpgtools.result_item_details"

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
    val resultItemDetails: ArrayList<ResultItemDetail>,
    private val layoutInflater: LayoutInflater
) : RecyclerView.Adapter<ResultItemDetailsViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ResultItemDetailsViewHolder {
        val view = layoutInflater.inflate(R.layout.list_item_result_item_details_entry, parent, false)
        return ResultItemDetailsViewHolder(
            view,
            this::deleteDetailItemEventHandler,
            this::changeDetailItemNameEventHandler,
            this::changeDetailItemContentEventHandler
        )
    }

    override fun getItemCount(): Int = resultItemDetails.size

    override fun onBindViewHolder(holder: ResultItemDetailsViewHolder, position: Int) =
        holder.bind(resultItemDetails[position], position)

    override fun getItemViewType(position: Int): Int = R.layout.list_item_result_item_details_entry

    private fun deleteDetailItemEventHandler(detailItemIndex: Int) {
        resultItemDetails.removeAt(detailItemIndex)
        notifyDataSetChanged()
    }

    private fun changeDetailItemNameEventHandler(rowIndex: Int, newName: String) {
        resultItemDetails[rowIndex].name = newName
    }

    private fun changeDetailItemContentEventHandler(rowIndex: Int, newContent: String) {
        resultItemDetails[rowIndex].content = newContent
    }
}

private class ResultItemDetailsViewHolder(
    val view: View,
    val deleteDetailItemEventHandler: (Int) -> Unit,
    changeDetailItemNameEventHandler: (Int, String) -> Unit,
    changeDetailItemContentEventHandler: (Int, String) -> Unit
) : RecyclerView.ViewHolder(view) {
    private val name = view.entry_name
    private val content = view.entry_content
    private var boundRowIndex = 0

    init {
        val badEditableMessage = "Attempted to update ResultItemDetails %s, but unexpectedly received" +
            " a null editable"
        name.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val newName = s?.toString()
                    ?: throw IllegalStateException(badEditableMessage.format("name"))
                changeDetailItemNameEventHandler(boundRowIndex, newName)
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
        content.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val newContent = s?.toString()
                    ?: throw IllegalStateException(badEditableMessage.format("content"))
                changeDetailItemContentEventHandler(boundRowIndex, newContent)
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
    }

    fun bind(dataModel: ResultItemDetail, rowIndex: Int) {
        boundRowIndex = rowIndex

        name.setText(dataModel.name, TextView.BufferType.EDITABLE)
        content.setText(dataModel.content, TextView.BufferType.EDITABLE)
        view.entry_delete.setOnClickListener { deleteDetailItemEventHandler(rowIndex) }
    }
}
