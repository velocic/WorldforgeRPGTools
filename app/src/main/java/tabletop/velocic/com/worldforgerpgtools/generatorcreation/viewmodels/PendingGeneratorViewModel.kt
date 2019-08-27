package tabletop.velocic.com.worldforgerpgtools.generatorcreation.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import tabletop.velocic.com.worldforgerpgtools.appcommon.ProbabilityTableKey
import tabletop.velocic.com.worldforgerpgtools.persistence.Generator

class PendingGeneratorViewModel : ViewModel()
{
    val newGenerator: MutableLiveData<Generator?> by lazy { MutableLiveData<Generator?>() }
    val tableData: MutableLiveData<ProbabilityTableKey?> by lazy { MutableLiveData<ProbabilityTableKey?>() }
}
