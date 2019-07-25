package tabletop.velocic.com.worldforgerpgtools.GeneratorCreation.ViewModels.NewGeneratorContents

import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import kotlinx.android.synthetic.main.partial_generator_contents_main_buttons.view.*
import kotlinx.android.synthetic.main.partial_generator_contents_merge_rows_buttons.view.*

class PrimaryFlowInteractions(
    private val parent: ViewGroup,
    val editDetailsClickHandler: () -> Unit = {},
    val mergeRowsClickHandler: (Int, Boolean) -> Unit = { _, _ -> },
    val splitMergedRowsClickHandler: () -> Unit = {}
)
{
    var rowIndex = 0

    init {
        parent.generator_contents_main_buttons_edit_details.setOnClickListener { editDetailsClickHandler() }
        parent.generator_contents_main_buttons_merge_range.setOnClickListener { mergeRowsClickHandler(rowIndex, false) }
        parent.generator_contents_main_buttons_split_range.setOnClickListener { splitMergedRowsClickHandler() }
    }

    fun show() {
        parent.visibility = View.VISIBLE
    }

    fun hide() {
        parent.visibility = View.INVISIBLE
    }
}

class MergeRowsFlowInteractions(
    private val parent: ViewGroup,
    val confirmMergeRowsClickHandler: (Int, Boolean) -> Unit = { _, _ -> }
)
{
    var rowIndex = 0

    init {
        parent.generator_contents_select_second_merge_target.setOnClickListener { confirmMergeRowsClickHandler(rowIndex, false) }
        parent.generator_contents_cancel_merge_rows.setOnClickListener { confirmMergeRowsClickHandler(rowIndex, true) }
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