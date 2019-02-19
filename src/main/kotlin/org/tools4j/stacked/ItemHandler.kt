package org.tools4j.stacked

interface ItemHandler<T> {
    fun handle(item: T)
    fun onFinish();
}