package org.tools4j.stacked.index

import org.apache.lucene.index.LeafReaderContext
import org.apache.lucene.search.DoubleValues
import org.apache.lucene.search.DoubleValuesSource
import org.apache.lucene.search.Explanation
import org.apache.lucene.search.IndexSearcher
import java.io.IOException
import java.util.*


open class BoostValuesSource(
    val sourceBoostField: DoubleValuesSource,
    val missingValueDefault: Double,
    val leaveZeroValues: Boolean,
    val boostingCalcGivenParentScoreAndFieldValue: (Double, Double) -> Double,
    val explanationEnricherGivenParentExplanationAndBoostExplanation: (Explanation, Explanation) -> Explanation): DoubleValuesSource() {

    @Throws(IOException::class)
    override fun getValues(ctx: LeafReaderContext, scores: DoubleValues): DoubleValues {
        val `in` = DoubleValues.withDefault(sourceBoostField.getValues(ctx, scores), missingValueDefault)
        return object : DoubleValues() {
            @Throws(IOException::class)
            override fun doubleValue(): Double {
                return boostingCalcGivenParentScoreAndFieldValue(scores.doubleValue(), `in`.doubleValue())
            }

            @Throws(IOException::class)
            override fun advanceExact(doc: Int): Boolean {
                return `in`.advanceExact(doc)
            }
        }
    }

    override fun needsScores(): Boolean {
        return true
    }

    @Throws(IOException::class)
    override fun rewrite(reader: IndexSearcher): DoubleValuesSource {
        return BoostValuesSource(
            sourceBoostField.rewrite(reader),
            missingValueDefault,
            leaveZeroValues,
            boostingCalcGivenParentScoreAndFieldValue,
            explanationEnricherGivenParentExplanationAndBoostExplanation)
    }

    override fun equals(o: Any?): Boolean {
        if (this === o) return true
        if (o == null || javaClass != o.javaClass) return false
        val that = o as BoostValuesSource?
        return sourceBoostField == that!!.sourceBoostField
    }

    @Throws(IOException::class)
    override fun explain(ctx: LeafReaderContext, docId: Int, scoreExplanation: Explanation): Explanation {
        if (scoreExplanation.isMatch == false) {
            return scoreExplanation
        }
        val boostExpl = sourceBoostField.explain(ctx, docId, scoreExplanation)
        return if (!boostExpl.isMatch || (boostExpl.value == 0.0f && leaveZeroValues)) {
            scoreExplanation
        } else {
            explanationEnricherGivenParentExplanationAndBoostExplanation(scoreExplanation, boostExpl)
        }
    }

    override fun hashCode(): Int {
        return Objects.hash(sourceBoostField)
    }

    override fun toString(): String {
        return "sourceBoostField($sourceBoostField)"
    }

    override fun isCacheable(ctx: LeafReaderContext): Boolean {
        return false;
    }
}