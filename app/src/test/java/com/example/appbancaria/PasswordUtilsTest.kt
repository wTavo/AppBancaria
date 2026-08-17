package com.example.appbancaria

import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class PasswordUtilsTest {
    @Test
    fun `hash no guarda la contraseña original`() {
        val hash = PasswordUtils.hash("secreto123")

        assertNotEquals("secreto123", hash)
        assertTrue(PasswordUtils.matches("secreto123", hash))
        assertFalse(PasswordUtils.matches("otra123", hash))
    }
}
