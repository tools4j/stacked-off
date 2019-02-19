package org.tools4j.stacked

class ToListHandler<T>(val list: MutableList<T>): ItemHandler<T>{
    override fun handle(item: T) {
        list.add(item)
    }

    override fun onFinish() {
        //do nothing
    }
}