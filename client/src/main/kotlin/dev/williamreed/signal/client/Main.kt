package dev.williamreed.signal.client

import dev.williamreed.signal.common.SerializablePreKeyBundle
import kotlinx.serialization.json.Json

fun main() {
    val alice = Client("alice", 1234)
    val bob = Client("bob", 9876)

    // TODO: turn into web requests
    // Server.register(Json.stringify(SerializablePreKeysBundle.serializer(), alice.nextPreKeysBundle()))
    // Server.register(Json.stringify(SerializablePreKeysBundle.serializer(), bob.nextPreKeysBundle()))

    // TODO: this can be created after requesting the information from the server
//    val aliceToBob = Session(
//        alice,
//        bob.address,
//        Json.parse(SerializablePreKeyBundle.serializer(), Server.getDeviceBundle(bob.deviceId)).toPreKeyBundle()
//    )
//    val bobToAlice = Session(
//        bob,
//        alice.address,
//        Json.parse(SerializablePreKeyBundle.serializer(), Server.getDeviceBundle(alice.deviceId)).toPreKeyBundle()
//    )

//    val msg = aliceToBob.encrypt("hi bob")
//    println(String(msg.whisperMessage.body))
//    println(bobToAlice.decrypt(msg))
}
