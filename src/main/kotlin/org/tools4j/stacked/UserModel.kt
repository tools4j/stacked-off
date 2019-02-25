package org.tools4j.stacked

import javax.xml.namespace.QName
import javax.xml.stream.events.StartElement

interface User {
    val id: String?
    val reputation: String?
    val displayName: String?
    val accountId: String?
}

data class UserImpl(
    override val id: String,
    override val reputation: String?,
    override val displayName: String?,
    override val accountId: String?) : User

class UserXmlRowHandler(delegate: ItemHandler<User>): XmlRowHandler<User>(delegate) {
    override fun getParentElementName(): String {
        return "users"
    }

    override fun handle(element: StartElement) {
        val user = UserImpl(
            element.getAttributeByName(QName.valueOf("Id"))!!.value,
            element.getAttributeByName(QName.valueOf("Reputation"))?.value,
            element.getAttributeByName(QName.valueOf("DisplayName"))?.value,
            element.getAttributeByName(QName.valueOf("AccountId"))?.value
        )
        delegate.handle(user)
    }
}