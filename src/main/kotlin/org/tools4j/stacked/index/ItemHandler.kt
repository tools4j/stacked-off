package org.tools4j.stacked.index

interface ItemHandler<T> {
    fun handle(item: T)
    fun onFinish()
}