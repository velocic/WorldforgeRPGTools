package tabletop.velocic.com.worldforgerpgtools.GeneratorCreation.ViewModels.NewGeneratorContents

import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import kotlinx.android.synthetic.main.partial_generator_contents_main_buttons.view.*

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
        parent.visibility = View.GONE
    }
}

class MergeRowsFlowInteractions(
    private val cancelButton: ImageView,
    val cancelMergeRowsClickHandler: () -> Unit = {}
)
{
    var rowIndex = 0

    init {
        cancelButton.setOnClickListener { cancelMergeRowsClickHandler() }
    }

    fun show() {
        cancelButton.visibility = View.VISIBLE
    }

    fun hide() {
        cancelButton.visibility = View.GONE
    }
}