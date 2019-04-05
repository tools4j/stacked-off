package org.tools4j.stacked.index

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*

internal class UtilsKtTest {
    @Test
    fun toProgress() {
        assertThat(toProgress(100, 12)).isEqualTo("0.0 / 0.1 KB")
        assertThat(toProgress(2000, 100)).isEqualTo("0.1 / 2.0 KB")
        assertThat(toProgress(8000, 100)).isEqualTo("0.1 / 7.8 KB")
        assertThat(toProgress(5_000_000, 1234)).isEqualTo("1.2 / 4,882 KB")
        assertThat(toProgress(50_000_000, 1234_000)).isEqualTo("1.2 / 47 MB")
        assertThat(toProgress(50_000_000_000, 1234_000)).isEqualTo("1.2 / 47,683 MB")
    }

    @Test
    fun formatProgressDouble() {
        assertThat(formatProgressDouble(9.2)).isEqualTo("9.2")
        assertThat(formatProgressDouble(10.2)).isEqualTo("10")
        assertThat(formatProgressDouble(100.0)).isEqualTo("100")
    }
}