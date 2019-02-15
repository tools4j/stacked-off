package org.tools4j.stacked

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.fail

class UserTestUtils {
    companion object {

        @JvmStatic
        fun assertHasAllUsers(users: List<User>) {
            assertHasUser3(users)
            assertHasUser24(users)
            assertHasUser63(users)
            assertHasUser77(users)
            assertHasUser80(users)
            assertHasUser262(users)
        }

        @JvmStatic
        fun assertHasUser3(users: List<User>) {
            var user = getUserWithId(users, "3")
            assertThat(user.id).isEqualTo("3")
            assertThat(user.reputation).isEqualTo("103")
            assertThat(user.displayName).isEqualTo("Jadasc")
            assertThat(user.accountId).isEqualTo("508203")
            }
  
        @JvmStatic
        fun assertHasUser24(users: List<User>) {
            var user = getUserWithId(users, "24")
            assertThat(user.id).isEqualTo("24")
            assertThat(user.reputation).isEqualTo("1241")
            assertThat(user.displayName).isEqualTo("Joshua Aslan Smith")
            assertThat(user.accountId).isEqualTo("1454870")
        }
  
        @JvmStatic
        fun assertHasUser63(users: List<User>) {
            var user = getUserWithId(users, "63")
            assertThat(user.id).isEqualTo("63")
            assertThat(user.reputation).isEqualTo("101")
            assertThat(user.displayName).isEqualTo("Doorknob")
            assertThat(user.accountId).isEqualTo("1266491")
        }
  
        @JvmStatic
        fun assertHasUser77(users: List<User>) {
            var user = getUserWithId(users, "77")
            assertThat(user.id).isEqualTo("77")
            assertThat(user.reputation).isEqualTo("101")
            assertThat(user.displayName).isEqualTo("mirabilos")
            assertThat(user.accountId).isEqualTo("2494278")
        }
  
        @JvmStatic
        fun assertHasUser80(users: List<User>) {
            var user = getUserWithId(users, "80")
            assertThat(user.id).isEqualTo("80")
            assertThat(user.reputation).isEqualTo("488")
            assertThat(user.displayName).isEqualTo("Sam Whited")
            assertThat(user.accountId).isEqualTo("141416")
        }
  
        @JvmStatic
        fun assertHasUser262(users: List<User>) {
            var user = getUserWithId(users, "262")
            assertThat(user.id).isEqualTo("262")
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