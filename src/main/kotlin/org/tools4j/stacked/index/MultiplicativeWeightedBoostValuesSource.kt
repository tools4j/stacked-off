package org.tools4j.stacked.index

import org.apache.lucene.search.DoubleValuesSource
import org.apache.lucene.search.Explanation

class MultiplicativeWeightedBoostValuesSource(
    sourceBoostField: DoubleValuesSource,
    weight: Float,
    missingValueDefault: Double,
    leaveZeroValues: Boolean): BoostValuesSource(
        sourceBoostField,
        missingValueDefault,
        leaveZeroValues,
        {parentScore, fieldValue -> parentScore * fieldValue * weight},
        {parentExplanation, fieldBoostExplanation ->
           Explanation.match(
                    parentExplanation.value.toFloat() * fieldBoostExplanation.value.toFloat() * weight,
                    "product of:", fieldBoostExplanation, Explanation.match(weight, "weight"), parentExplanation)
        }
    )
