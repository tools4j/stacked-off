package org.tools4j.stacked

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonRootName

interface User {
    val id: String?
    val reputation: String?
    val displayName: String?
    val accountId: String?
}

@JsonRootName("users")
data class Users(
    @set:JsonProperty("row")
    var users: List<UserImpl>? = null){

    companion object {
        @JvmStatic
        fun fromXmlOnClasspath(onClasspath: String): Users {
            return XmlDoc.parseAs(onClasspath)
        }
    }
}

@JsonRootName("row")
data class UserImpl(
    @set:JsonProperty("Id")
    override var id: String?,

    @set:JsonProperty("Reputation")
    override var reputation: String?,

    @set:JsonProperty("DisplayName")
    override var displayName: String?,

    @set:JsonProperty("AccountId")
    override var accountId: String?) : User