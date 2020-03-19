package fed

import kotlin.random.Random
import kotlin.random.nextInt

const val tokenLen = 32

val random by lazy { Random }

fun generateToken(): String {
    return (1..tokenLen).map { (random.nextInt('A'.toInt(), 'Z'.toInt()).toChar()) }.joinToString(separator = "")
}
