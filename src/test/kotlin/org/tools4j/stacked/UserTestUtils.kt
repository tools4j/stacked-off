package org.tools4j.stacked

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.fail

class UserTestUtils {
    companion object {

        @JvmStatic
        fun assertHasAllUsers(users: List<User>) {
            assertHasUser1(users)
            assertHasUser2(users)
            assertHasUser3(users)
            assertHasUser4(users)
            assertHasUser5(users)
            assertHasUser6(users)
        }

        @JvmStatic
        fun assertHasUser1(users: List<User>) {
            assertUser1(getUserWithId(users, "1"))
        }

        @JvmStatic
        fun assertUser1(user: User) {
            assertThat(user.id).isEqualTo("1")
            assertThat(user.reputation).isEqualTo("103")
            assertThat(user.displayName).isEqualTo("Jadasc")
            assertThat(user.accountId).isEqualTo("508203")
        }

        @JvmStatic
        fun assertHasUser2(users: List<User>) {
            assertUser2(getUserWithId(users, "2"))
        }

        @JvmStatic
        fun assertUser2(user: User) {
            assertThat(user.id).isEqualTo("2")
            assertThat(user.reputation).isEqualTo("1241")
            assertThat(user.displayName).isEqualTo("Joshua Aslan Smith")
            assertThat(user.accountId).isEqualTo("1454870")
        }

        @JvmStatic
        fun assertHasUser3(users: List<User>) {
            assertUser3(getUserWithId(users, "3"))
        }

        @JvmStatic
        fun assertUser3(user: User) {
            assertThat(user.id).isEqualTo("3")
            assertThat(user.reputation).isEqualTo("101")
            assertThat(user.displayName).isEqualTo("Doorknob")
            assertThat(user.accountId).isEqualTo("1266491")
        }

        @JvmStatic
        fun assertHasUser4(users: List<User>) {
            assertUser4(getUserWithId(users, "4"))
        }

        @JvmStatic
        fun assertUser4(user: User) {
            assertThat(user.id).isEqualTo("4")
            assertThat(user.reputation).isEqualTo("101")
            assertThat(user.displayName).isEqualTo("mirabilos")
            assertThat(user.accountId).isEqualTo("2494278")
        }

        @JvmStatic
        fun assertHasUser5(users: List<User>) {
            assertUser5(getUserWithId(users, "5"))
        }

        @JvmStatic
        fun assertUser5(user: User) {
            assertThat(user.id).isEqualTo("5")
            assertThat(user.reputation).isEqualTo("488")
            assertThat(user.displayName).isEqualTo("Sam Whited")
            assertThat(user.accountId).isEqualTo("141416")
        }

        @JvmStatic
        fun assertHasUser6(users: List<User>) {
            assertUser6(getUserWithId(users, "6"))
        }

        @JvmStatic
        fun assertUser6(user: User) {
            assertThat(user.id).isEqualTo("6")
            assertThat(user.reputation).isEqualTo("6844")
            assertThat(user.displayName).isEqualTo("hoc_age")
            assertThat(user.accountId).isEqualTo("4366363")
        }

        private fun getUserWithId(users: List<User>, id: String): User {
            var user = users.find { it.id == id }
            if (user == null) {
                fail("Could not find post[$id]. Posts found: ${users.map { it.id }}")
            } else {
                return user!!
            }
        }
    }
}