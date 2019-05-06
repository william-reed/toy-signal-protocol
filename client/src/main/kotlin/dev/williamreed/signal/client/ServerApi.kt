package dev.williamreed.signal.client

import dev.williamreed.signal.common.SerializablePreKeyBundle
import dev.williamreed.signal.common.SerializablePreKeysBundle
import retrofit2.Call
import retrofit2.http.*

/**
 * Server API
 *
 * Connect to the server
 */
interface ServerApi {
    @POST("/register")
    fun register(@Body keysBundle: SerializablePreKeysBundle): Call<Unit>

    @GET("/deviceBundle")
    fun getDeviceBundle(@Query("deviceId") deviceId: Int): Call<SerializablePreKeyBundle>
}
