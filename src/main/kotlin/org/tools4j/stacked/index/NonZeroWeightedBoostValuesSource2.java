package org.tools4j.stacked.index;

import org.apache.lucene.index.LeafReaderContext;
import org.apache.lucene.search.DoubleValues;
import org.apache.lucene.search.DoubleValuesSource;
import org.apache.lucene.search.IndexSearcher;

import java.io.IOException;
import java.util.Objects;

public class NonZeroWeightedBoostValuesSource2 extends DoubleValuesSource{
    private final DoubleValuesSource sourceBoostField;
    private final float weight;

    public NonZeroWeightedBoostValuesSource2(DoubleValuesSource sourceBoostField, float weight) {
        this.sourceBoostField = sourceBoostField;
        this.weight = weight;
    }

    @Override
    public DoubleValues getValues(LeafReaderContext ctx, DoubleValues parentScore) throws IOException {
        long fieldValue =  (long) DoubleValues.withDefault(sourceBoostField.getValues(ctx, parentScore), 0).doubleValue();
        return new DoubleValues() {
            @Override
            public double doubleValue() throws IOException {
                float boost = fieldValue == 0.0 ? 1.0f : weight;
                return parentScore.doubleValue() * boost;
            }

            @Override
            public boolean advanceExact(int doc) throws IOException {
                return parentScore.advanceExact(doc);
            }
        };
    }

    @Override
    public boolean needsScores() {
        return true;
    }

    @Override
    public DoubleValuesSource rewrite(IndexSearcher reader) throws IOException {
        return new NonZeroWeightedBoostValuesSource2(sourceBoostField.rewrite(reader), weight);
    }

    @Override
    public boolean isCacheable(LeafReaderContext ctx) {
        return false;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NonZeroWeightedBoostValuesSource2 that = (NonZeroWeightedBoostValuesSource2) o;
        return weight == that.weight &&
                Objects.equals(sourceBoostField, that.sourceBoostField);
    }

    @Override
    public int hashCode() {
        return Objects.hash(sourceBoostField, weight);
    }

    @Override
    public String toString() {
        return "NonZeroWeightedBoostValuesSource2{" +
                "sourceBoostField=" + sourceBoostField +
                ", weight=" + weight +
                '}';
    }
}