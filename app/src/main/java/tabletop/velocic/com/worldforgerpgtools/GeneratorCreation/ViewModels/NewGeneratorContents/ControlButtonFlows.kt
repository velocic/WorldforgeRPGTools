package tabletop.velocic.com.worldforgerpgtools.GeneratorCreation.ViewModels.NewGeneratorContents

import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import kotlinx.android.synthetic.main.partial_generator_contents_main_buttons.view.*
import kotlinx.android.synthetic.main.partial_generator_contents_merge_rows_buttons.view.*
import tabletop.velocic.com.worldforgerpgtools.GeneratorDeserializer.TableEntries

class PrimaryFlowInteractions(
    private val parent: ViewGroup,
    private val editDetailsClickHandler: () -> Unit = {},
    private val mergeRowsClickHandler: (Int, Boolean) -> Unit = { _, _ -> },
    private val splitMergedRowsClickHandler: (Int) -> Unit = {}
)
{
    private var rowData: TableEntries? = null
    private var rowIndex = 0

    init {
        parent.generator_contents_main_buttons_edit_details.setOnClickListener { editDetailsClickHandler() }
        parent.generator_contents_main_buttons_merge_range.setOnClickListener { mergeRowsClickHandler(rowIndex, false) }
        parent.generator_contents_main_buttons_split_range.setOnClickListener { splitMergedRowsClickHandler(rowIndex) }
    }

    fun bind(rowIndex: Int, rowData: TableEntries) {
        this.rowIndex = rowIndex
        this.rowData = rowData
    }

    fun show() {
        val isMergedRow = rowData?.diceRange?.first != rowData?.diceRange?.last
            ?: false

        if (isMergedRow) {
            parent.generator_contents_main_buttons_split_range.visibility = View.VISIBLE
        } else {
            parent.generator_contents_main_buttons_split_range.visibility = View.INVISIBLE
        }

        parent.visibility = View.VISIBLE
    }

    fun hide() {
        parent.visibility = View.INVISIBLE
    }
}

class MergeRowsFlowInteractions(
    private val parent: ViewGroup,
    private val confirmMergeRowsClickHandler: (Int, Boolean) -> Unit = { _, _ -> }
)
{
    private var rowIndex = 0

    init {
        parent.generator_contents_select_second_merge_target.setOnClickListener { confirmMergeRowsClickHandler(rowIndex, false) }
        parent.generator_contents_cancel_merge_rows.setOnClickListener { confirmMergeRowsClickHandler(rowIndex, true) }
    }

    fun bind(rowIndex: Int) {
        this.rowIndex = rowIndex
    }

    fun show(isInitialSelectedRowForMerge: Boolean) {
        parent.visibility = View.VISIBLE

        if (isInitialSelectedRowForMerge) {
            parent.generator_contents_cancel_merge_rows.visibility = View.VISIBLE
            parent.generator_contents_select_second_merge_target.visibility = View.INVISIBLE
            return
        }

        parent.generator_contents_cancel_merge_rows.visibility = View.INVISIBLE
        parent.generator_contents_select_second_merge_target.visibility = View.VISIBLE
    }

    fun hide() {
        parent.visibility = View.INVISIBLE
        parent.generator_contents_cancel_merge_rows.visibility = View.GONE
        parent.generator_contents_select_second_merge_target.visibility = View.INVISIBLE
    }
}