package dev.williamreed.signal.client

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType
import retrofit2.Retrofit


fun main() {
    val contentType = MediaType.get("application/json")

    val retrofit = Retrofit.Builder()
        .baseUrl("http://localhost:4567")
        .addConverterFactory(Json.asConverterFactory(contentType))
        .build()

    val service = retrofit.create(ServerApi::class.java)


    val alice = Client("alice", 1234)
    val bob = Client("bob", 9876)

    // register with the server
    service.register(alice.nextPreKeysBundle()).execute().apply {
        if (!isSuccessful)
            println(errorBody())
    }
    service.register(bob.nextPreKeysBundle()).execute().apply {
        if (!isSuccessful)
            println(errorBody())
    }

    // create sessions with bundles obtained from the server
    val aliceToBob = Session(
        alice,
        bob.address,
        service.getDeviceBundle(bob.deviceId).execute().body()?.toPreKeyBundle() ?: error("idek")
    )
    val bobToAlice = Session(
        bob,
        alice.address,
        service.getDeviceBundle(alice.deviceId).execute().body()?.toPreKeyBundle() ?: error("idek")
    )

    // send a message with our obtained info
    // TODO: do this over the wire
    val msg = aliceToBob.encrypt("hi bob")
    println(String(msg.whisperMessage.body))
    println(bobToAlice.decrypt(msg))
}
