package org.tools4j.stacked.index

import javax.xml.namespace.QName
import javax.xml.stream.events.StartElement

interface Site {
    val id: String
    val tinyName: String?
    val name: String?
    val longName: String?
    val url: String?
    val imageUrl: String?
    val iconUrl: String?
    val databaseName: String?
    val tagline: String?
    val tagCss: String?
    val totalQuestions: String?
    val totalAnswers: String?
    val totalUsers: String?
    val totalComments: String?
    val totalTags: String?
    val lastPost: String?
    val oDataEndpoint: String?
    val badgeIconUrl: String?
}

data class SiteImpl(
    override val id: String,
    override val tinyName: String?,
    override val name: String?,
    override val longName: String?,
    override val url: String?,
    override val imageUrl: String?,
    override val iconUrl: String?,
    override val databaseName: String?,
    override val tagline: String?,
    override val tagCss: String?,
    override val totalQuestions: String?,
    override val totalAnswers: String?,
    override val totalUsers: String?,
    override val totalComments: String?,
    override val totalTags: String?,
    override val lastPost: String?,
    override val oDataEndpoint: String?,
    override val badgeIconUrl: String?) : Site


class SiteXmlRowHandler(delegate: ItemHandler<Site>): XmlRowHandler<Site>(delegate) {
    override fun getParentElementName(): String {
        return "sites"
    }

    override fun handle(element: StartElement) {
        val site = SiteImpl(
            element.getAttributeByName(QName.valueOf("id"))!!.value,
            element.getAttributeByName(QName.valueOf("tinyName"))?.value,
            element.getAttributeByName(QName.valueOf("name"))?.value,
            element.getAttributeByName(QName.valueOf("longName"))?.value,
            element.getAttributeByName(QName.valueOf("url"))?.value,
            element.getAttributeByName(QName.valueOf("imageUrl"))?.value,
            element.getAttributeByName(QName.valueOf("iconUrl"))?.value,
            element.getAttributeByName(QName.valueOf("databaseName"))?.value,
            element.getAttributeByName(QName.valueOf("tagline"))?.value,
            element.getAttributeByName(QName.valueOf("tagCss"))?.value,
            element.getAttributeByName(QName.valueOf("totalQuestions"))?.value,
            element.getAttributeByName(QName.valueOf("totalAnswers"))?.value,
            element.getAttributeByName(QName.valueOf("totalUsers"))?.value,
            element.getAttributeByName(QName.valueOf("totalComments"))?.value,
            element.getAttributeByName(QName.valueOf("totalTags"))?.value,
            element.getAttributeByName(QName.valueOf("lastPost"))?.value,
            element.getAttributeByName(QName.valueOf("oDataEndpoint"))?.value,
            element.getAttributeByName(QName.valueOf("badgeIconUrl"))?.value
        )
        delegate.handle(site)
    }
}