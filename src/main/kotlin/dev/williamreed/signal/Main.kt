package dev.williamreed.signal

fun main() {
    val alice = Client("alice", 1234)
    val bob = Client("bob", 9876)

    val aliceToBob = Session(alice, bob.address, bob.preKey)
    val bobToAlice = Session(bob, alice.address, alice.preKey)

    val msg = aliceToBob.encrypt("hi bob")
    println(String(msg.whisperMessage.body))
    println(bobToAlice.decrypt(msg))
}
