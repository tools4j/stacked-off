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
    override var id: String?,
    override var reputation: String?,
    override var displayName: String?,
    override var accountId: String?) : User

class UserXmlRowHandler(delegate: ItemHandler<User>): XmlRowHandler<User>(delegate) {
    override fun getParentElementName(): String {
        return "users"
    }

    override fun handle(element: StartElement) {
        val user = UserImpl(
            element.getAttributeByName(QName.valueOf("Id"))?.value,
            element.getAttributeByName(QName.valueOf("Reputation"))?.value,
            element.getAttributeByName(QName.valueOf("DisplayName"))?.value,
            element.getAttributeByName(QName.valueOf("AccountId"))?.value
        )
        delegate.handle(user)
    }
}