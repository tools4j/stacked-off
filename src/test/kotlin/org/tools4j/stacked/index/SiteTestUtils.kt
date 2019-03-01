package org.tools4j.stacked.index

import org.assertj.core.api.Assertions.assertThat

fun assertHasSite1(Sites: Collection<Site>) {
    val Site = getSiteById("1", Sites)
    assertThat(Site).isNotNull
    assertIsSite1(Site!!)
}

fun assertHasSite2(Sites: Collection<Site>) {
    val Site = getSiteById("2", Sites)
    assertThat(Site).isNotNull
    assertIsSite2(Site!!)
}

fun assertHasSite3(Sites: Collection<Site>) {
    val Site = getSiteById("3", Sites)
    assertThat(Site).isNotNull
    assertIsSite3(Site!!)
}

fun getSiteById(stackexchangeSiteId: String, Sites: Collection<Site>): Site? {
    return Sites.firstOrNull { it.stackexchangeSiteId == stackexchangeSiteId }
}

fun assertIsSite1(site: Site) {
    assertThat(site.tinyName).isEqualTo("stackoverflow")
    assertThat(site.name).isEqualTo("StackOverflow")
    assertThat(site.longName).isEqualTo("Stack Overflow")
    assertThat(site.url).isEqualTo("https://stackoverflow.com")
    assertThat(site.imageUrl).isEqualTo("https://cdn.sstatic.net/Sites/stackoverflow/img/logo.png")
    assertThat(site.iconUrl).isEqualTo("https://cdn.sstatic.net/Sites/stackoverflow/img/icon-16.png")
    assertThat(site.databaseName).isEqualTo("StackOverflow")
    assertThat(site.tagline).isEqualTo("Q&A for programmers")
    assertThat(site.tagCss).isEqualToIgnoringWhitespace(".post-tag{\nbackground-color:#E0EAF1;\nborder-bottom:1px solid #3E6D8E;\nborder-right:1px solid #7F9FB6;\ncolor:#3E6D8E;\nfont-size:90%;\nline-height:2.4;\nmargin:2px 2px 2px 0;\npadding:3px 4px;\ntext-decoration:none;\nwhite-space:nowrap;\n}\n.post-tag:hover {\nbackground-color:#3E6D8E;\nborder-bottom:1px solid #37607D;\nborder-right:1px solid #37607D;\ncolor:#E0EAF1;\ntext-decoration:none;}")
    assertThat(site.totalQuestions).isEqualTo("16845942")
    assertThat(site.totalAnswers).isEqualTo("25908201")
    assertThat(site.totalUsers).isEqualTo("9737247")
    assertThat(site.totalComments).isEqualTo("70206499")
    assertThat(site.totalTags).isEqualTo("53806")
    assertThat(site.lastPost).isEqualTo("2018-12-02T07:50:04.220")
    assertThat(site.oDataEndpoint).isEqualTo("https://odata.sqlazurelabs.com/OData.svc/v0.1/rp1uiewita/StackOverflow")
    assertThat(site.badgeIconUrl).isEqualTo("https://cdn.sstatic.net/Sites/stackoverflow/img/apple-touch-icon.png")
}

fun assertIsSite2(site: Site) {
    assertThat(site.tinyName).isEqualTo("superuser")
    assertThat(site.name).isEqualTo("SuperUser")
    assertThat(site.longName).isEqualTo("Super User")
    assertThat(site.url).isEqualTo("https://superuser.com")
    assertThat(site.imageUrl).isEqualTo("https://cdn.sstatic.net/Sites/superuser/img/logo.png")
    assertThat(site.iconUrl).isEqualTo("https://cdn.sstatic.net/Sites/superuser/img/icon-16.png")
    assertThat(site.databaseName).isEqualTo("SuperUser")
    assertThat(site.tagline).isEqualTo("Q&A for computer enthusiasts and power users")
    assertThat(site.tagCss).isEqualToIgnoringWhitespace(".post-tag {\n-moz-border-radius:7px 7px 7px 7px;\nbackground-color:#FFFFFF;\nborder:2px solid #14A7C6;\ncolor:#1087A4;\nfont-size:90%;\nline-height:2.4;\nmargin:2px 2px 2px 0;\npadding:3px 5px;\ntext-decoration:none;\nwhite-space:nowrap;\n}\n.post-tag:visited {\ncolor:#1087A4;\n}\n.post-tag:hover {\nbackground-color:#14A7C6;\nborder:2px solid #14A7C6;\ncolor:#F3F1D9;\ntext-decoration:none;\n}")
    assertThat(site.totalQuestions).isEqualTo("390593")
    assertThat(site.totalAnswers).isEqualTo("578335")
    assertThat(site.totalUsers).isEqualTo("695350")
    assertThat(site.totalComments).isEqualTo("1413644")
    assertThat(site.totalTags).isEqualTo("5319")
    assertThat(site.lastPost).isEqualTo("2018-12-02T04:50:39.060")
    assertThat(site.oDataEndpoint).isEqualTo("https://odata.sqlazurelabs.com/OData.svc/v0.1/rp1uiewita/SuperUser")
    assertThat(site.badgeIconUrl).isEqualTo("https://cdn.sstatic.net/Sites/superuser/img/apple-touch-icon.png")
}

fun assertIsSite3(site: Site) {
    assertThat(site.tinyName).isEqualTo("serverfault")
    assertThat(site.name).isEqualTo("ServerFault")
    assertThat(site.longName).isEqualTo("Server Fault")
    assertThat(site.url).isEqualTo("https://serverfault.com")
    assertThat(site.imageUrl).isEqualTo("https://cdn.sstatic.net/Sites/serverfault/img/logo.png")
    assertThat(site.iconUrl).isEqualTo("https://cdn.sstatic.net/Sites/serverfault/img/icon-16.png")
    assertThat(site.databaseName).isEqualTo("ServerFault")
    assertThat(site.tagline).isEqualTo("Q&A for system administrators and IT professionals")
    assertThat(site.tagCss).isEqualToIgnoringWhitespace(".post-tag {\nbackground-color:#F3F1D9;\nborder:1px solid #C5B849;\ncolor:#444444;\nfont-size:90%;\nline-height:2.4;\nmargin:2px 2px 2px 0;\npadding:3px 4px;\ntext-decoration:none;\nwhite-space:nowrap;\n}\n.post-tag:visited {\ncolor:#444444;\n}\n.post-tag:hover {\nbackground-color:#444444;\nborder:1px solid #444444;\ncolor:#F3F1D9;\ntext-decoration:none\n}")
    assertThat(site.totalQuestions).isEqualTo("268276")
    assertThat(site.totalAnswers).isEqualTo("441825")
    assertThat(site.totalUsers).isEqualTo("390834")
    assertThat(site.totalComments).isEqualTo("884510")
    assertThat(site.totalTags).isEqualTo("3643")
    assertThat(site.lastPost).isEqualTo("2018-12-02T04:46:30.700")
    assertThat(site.oDataEndpoint).isEqualTo("https://odata.sqlazurelabs.com/OData.svc/v0.1/rp1uiewita/ServerFault")
    assertThat(site.badgeIconUrl).isEqualTo("https://cdn.sstatic.net/Sites/serverfault/img/apple-touch-icon.png")
}
