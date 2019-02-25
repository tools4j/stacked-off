package org.tools4j.stacked

import org.assertj.core.api.Assertions.assertThat

fun assertHasQuestion1(questions: Collection<Question>){
    val question = getQuestionById("1", questions)
    assertThat(question).isNotNull
    assertIsQuestion1(question!!)
}

fun assertHasQuestion2(questions: Collection<Question>){
    val question = getQuestionById("2", questions)
    assertThat(question).isNotNull
    assertIsQuestion2(question!!)
}

fun getQuestionById(id: String, questions: Collection<Question>): Question? {
    return questions.firstOrNull { it.id == id }
}

fun assertIsQuestion1(question: Question){
    assertIsPost1(question)
    assertThat(question.childPosts).hasSize(1)
    assertHasPost3(question.childPosts)
}

fun assertIsQuestion2(question: Question){
    assertIsPost2(question)
    assertThat(question.childPosts).isEmpty()
}