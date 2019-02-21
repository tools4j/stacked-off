package org.tools4j.stacked

import org.assertj.core.api.Assertions.assertThat

fun assertIsQuestion1(question: Question){
    assertIsPost1(question)
    assertThat(question.childPosts).hasSize(1)
    assertHasPost3(question.childPosts)
}

fun assertIsQuestion2(question: Question){
    assertIsPost2(question)
    assertThat(question.childPosts).isEmpty()
}