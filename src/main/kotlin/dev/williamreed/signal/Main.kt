package dev.williamreed.signal

import kotlinx.serialization.json.Json

fun main() {
    val alice = Client("alice", 1234)
    val bob = Client("bob", 9876)

    // server simulation to demo that bob and alice don't need to talk to each other
    Server.register(Json.stringify(SerializablePreKeysBundle.serializer(), alice.nextPreKeysBundle()))
    Server.register(Json.stringify(SerializablePreKeysBundle.serializer(), bob.nextPreKeysBundle()))

    val aliceToBob = Session(
        alice,
        bob.address,
        Json.parse(SerializablePreKeyBundle.serializer(), Server.getDeviceBundle(bob.deviceId)).toPreKeyBundle()
    )
    val bobToAlice = Session(
        bob,
        alice.address,
        Json.parse(SerializablePreKeyBundle.serializer(), Server.getDeviceBundle(alice.deviceId)).toPreKeyBundle()
    )

    val msg = aliceToBob.encrypt("hi bob")
    println(String(msg.whisperMessage.body))
    println(bobToAlice.decrypt(msg))
}
