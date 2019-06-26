package org.tools4j.stacked.index

import org.apache.lucene.search.DoubleValuesSource
import org.apache.lucene.search.Explanation

class NonZeroWeightedBoostValuesSource(
    sourceBoostField: DoubleValuesSource,
    weight: Float,
    missingValueDefault: Double): BoostValuesSource(
        sourceBoostField,
        missingValueDefault,
        false,
        {parentScore, fieldValue -> parentScore * if(fieldValue == 0.0) 1.0f else weight},
        {parentExplanation, fieldBoostExplanation ->
            if(fieldBoostExplanation.value == 0.0f){
                parentExplanation
            } else {
                Explanation.match(
                    parentExplanation.value.toFloat() * fieldBoostExplanation.value.toFloat() * weight,
                    "product of:", Explanation.match(weight, "weight because of non-zero value of " + fieldBoostExplanation.description), parentExplanation
                )
            }
        }
    )
