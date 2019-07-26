package tabletop.velocic.com.worldforgerpgtools.generatorcreation

import tabletop.velocic.com.worldforgerpgtools.appcommon.ProbabilityTableKey

enum class GeneratorTableTemplate(val tableData: ProbabilityTableKey)
{
    OneDFour(ProbabilityTableKey(dieSize = 4)),
    OneDSix(ProbabilityTableKey(dieSize = 6)),
    OneDEight(ProbabilityTableKey(dieSize = 8)),
    OneDTen(ProbabilityTableKey(dieSize = 10)),
    OneDTwelve(ProbabilityTableKey(dieSize = 12)),
    OneDTwenty(ProbabilityTableKey(dieSize = 20)),
    TwoDSix(ProbabilityTableKey(2, 6)),
    ThreeDSix(ProbabilityTableKey(3, 6)),
    OneDOneHundred(ProbabilityTableKey(dieSize = 100))
}
