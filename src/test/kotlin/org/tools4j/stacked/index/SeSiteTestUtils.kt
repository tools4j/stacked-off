package org.tools4j.stacked.index

import org.assertj.core.api.Assertions.assertThat

fun assertHasSeSite1(seSites: Collection<SeSite>) {
    val Site = getSeSiteById("1", seSites)
    assertThat(Site).isNotNull
    assertIsSeSite1(Site!!)
}

fun assertHasSeSite2(seSites: Collection<SeSite>) {
    val Site = getSeSiteById("2", seSites)
    assertThat(Site).isNotNull
    assertIsSeSite2(Site!!)
}

fun assertHasSeSite3(seSites: Collection<SeSite>) {
    val Site = getSeSiteById("3", seSites)
    assertThat(Site).isNotNull
    assertIsSeSite3(Site!!)
}

fun getSeSiteById(stackexchangeSiteId: String, seSites: Collection<SeSite>): SeSite? {
    return seSites.firstOrNull { it.seSiteId == stackexchangeSiteId }
}

fun assertIsSeSite1(seSite: SeSite) {
    assertThat(seSite.tinyName).isEqualTo("stackoverflow")
    assertThat(seSite.name).isEqualTo("StackOverflow")
    assertThat(seSite.longName).isEqualTo("Stack Overflow")
    assertThat(seSite.url).isEqualTo("https://stackoverflow.com")
    assertThat(seSite.urlDomain).isEqualTo("stackoverflow.com")
    assertThat(seSite.imageUrl).isEqualTo("https://cdn.sstatic.net/Sites/stackoverflow/img/logo.png")
    assertThat(seSite.iconUrl).isEqualTo("https://cdn.sstatic.net/Sites/stackoverflow/img/icon-16.png")
    assertThat(seSite.databaseName).isEqualTo("StackOverflow")
    assertThat(seSite.tagline).isEqualTo("Q&A for programmers")
    assertThat(seSite.tagCss).isEqualToIgnoringWhitespace(".post-tag{\nbackground-color:#E0EAF1;\nborder-bottom:1px solid #3E6D8E;\nborder-right:1px solid #7F9FB6;\ncolor:#3E6D8E;\nfont-size:90%;\nline-height:2.4;\nmargin:2px 2px 2px 0;\npadding:3px 4px;\ntext-decoration:none;\nwhite-space:nowrap;\n}\n.post-tag:hover {\nbackground-color:#3E6D8E;\nborder-bottom:1px solid #37607D;\nborder-right:1px solid #37607D;\ncolor:#E0EAF1;\ntext-decoration:none;}")
    assertThat(seSite.totalQuestions).isEqualTo("16845942")
    assertThat(seSite.totalAnswers).isEqualTo("25908201")
    assertThat(seSite.totalUsers).isEqualTo("9737247")
    assertThat(seSite.totalComments).isEqualTo("70206499")
    assertThat(seSite.totalTags).isEqualTo("53806")
    assertThat(seSite.lastPost).isEqualTo("2018-12-02T07:50:04.220")
    assertThat(seSite.oDataEndpoint).isEqualTo("https://odata.sqlazurelabs.com/OData.svc/v0.1/rp1uiewita/StackOverflow")
    assertThat(seSite.badgeIconUrl).isEqualTo("https://cdn.sstatic.net/Sites/stackoverflow/img/apple-touch-icon.png")
}

fun assertIsSeSite2(seSite: SeSite) {
    assertThat(seSite.seSiteId).isEqualTo("2")
    assertThat(seSite.tinyName).isEqualTo("beerme")
    assertThat(seSite.name).isEqualTo("Beer, Wine & Spirits Meta Stack Exchange")
    assertThat(seSite.longName).isEqualTo("Beer, Wine & Spirits Meta Stack Exchange")
    assertThat(seSite.url).isEqualTo("https://beer.meta.stackexchange.com")
    assertThat(seSite.imageUrl).isEqualTo("https://cdn.sstatic.net/Sites/alcoholmeta/img/logo.png")
    assertThat(seSite.iconUrl).isEqualTo("https://cdn.sstatic.net/Sites/alcoholmeta/img/favicon.ico")
    assertThat(seSite.databaseName).isEqualTo("StackExchange.Beer.Meta")
    assertThat(seSite.tagline).isEqualTo("Q&A for alcoholic beverage aficionados and those interested in beer, wine, or spirits")
    assertThat(seSite.tagCss).isEqualToIgnoringWhitespace(".post-tag{\nbackground-color:#E0EAF1;\nborder-bottom:1px solid #3E6D8E;\nborder-right:1px solid #7F9FB6;\ncolor:#3E6D8E;\nfont-size:90%;\nline-height:2.4;\nmargin:2px 2px 2px 0;\npadding:3px 4px;\ntext-decoration:none;\nwhite-space:nowrap;\n}\n.post-tag:hover {\nbackground-color:#3E6D8E;\nborder-bottom:1px solid #37607D;\nborder-right:1px solid #37607D;\ncolor:#E0EAF1;\ntext-decoration:none;}")
    assertThat(seSite.totalQuestions).isEqualTo("82")
    assertThat(seSite.totalAnswers).isEqualTo("128")
    assertThat(seSite.totalUsers).isEqualTo("421")
    assertThat(seSite.totalComments).isEqualTo("331")
    assertThat(seSite.totalTags).isEqualTo("74")
    assertThat(seSite.lastPost).isEqualTo("2018-12-02T03:19:44.280")
    assertThat(seSite.badgeIconUrl).isEqualTo("https://cdn.sstatic.net/Sites/alcoholmeta/img/apple-touch-icon.png")
}

fun assertIsSeSite3(seSite: SeSite) {
    assertThat(seSite.seSiteId).isEqualTo("3")
    assertThat(seSite.tinyName).isEqualTo("coffeeme")
    assertThat(seSite.name).isEqualTo("Coffee Meta")
    assertThat(seSite.longName).isEqualTo("Coffee Meta")
    assertThat(seSite.url).isEqualTo("https://coffee.meta.stackexchange.com")
    assertThat(seSite.imageUrl).isEqualTo("https://cdn.sstatic.net/Sites/coffeemeta/img/logo.png")
    assertThat(seSite.iconUrl).isEqualTo("https://cdn.sstatic.net/Sites/coffeemeta/img/favicon.ico")
    assertThat(seSite.databaseName).isEqualTo("StackExchange.Coffee.Meta")
    assertThat(seSite.tagline).isEqualTo("Q&A for people interested in all aspects of producing and consuming coffee")
    assertThat(seSite.totalAnswers).isEqualTo("143")
    assertThat(seSite.totalUsers).isEqualTo("363")
    assertThat(seSite.totalComments).isEqualTo("331")
    assertThat(seSite.totalTags).isEqualTo("70")
    assertThat(seSite.lastPost).isEqualTo("2018-12-02T03:12:12.927")
    assertThat(seSite.badgeIconUrl).isEqualTo("https://cdn.sstatic.net/Sites/coffeemeta/img/apple-touch-icon.png")
}
