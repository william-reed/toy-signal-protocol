package dev.williamreed.signal

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonException
import spark.Spark.get
import spark.Spark.post

/**
 * Server
 *
 * Simulating the job of a server holding public key information from various clients
 */
object Server {
    // map of device ids and bundle
    private val devices = mutableMapOf<Int, SerializablePreKeysBundle>()
    private const val DEVICE_ID = "deviceId"

    init {
        setupWebServer()
    }

    /**
     * Register a device with the server
     *
     * @param bundleJson [SerializablePreKeysBundle] json version of the bundle
     */
    fun register(bundleJson: String) {
        val bundle = Json.parse(SerializablePreKeysBundle.serializer(), bundleJson)
        devices[bundle.deviceId] = bundle
    }

    /**
     * Get a bundle for the given device destination. Removes the pre key from the pool for the device
     *
     * @return json version of the bundle
     */
    fun getDeviceBundle(deviceId: Int): String {
        val preKeysBundle = devices[deviceId] ?: error("device id `$deviceId` does not exist.")
        val nextPreKey = preKeysBundle.publicPreKeys.first()

        // remove this key
        devices[deviceId] = preKeysBundle.copy(publicPreKeys = preKeysBundle.publicPreKeys.rest())

        return Json.stringify(
            SerializablePreKeyBundle.serializer(), SerializablePreKeyBundle(
                preKeysBundle.localRegistrationId,
                deviceId,
                nextPreKey,
                preKeysBundle.signedPublicPreKey,
                preKeysBundle.signedPreKeySignature,
                preKeysBundle.identityPublicKey
            )
        )
    }

    /**
     * Setup web server endpoints
     */
    private fun setupWebServer() {
        post("/register") { req, res ->
            try {
                register(req.body())
            } catch (e: JsonException) {
                res.status(400)
                e.message
            }
        }

        get("/deviceBundle") { req, res ->
            val deviceId = req.queryParams(DEVICE_ID)

            when {
                deviceId == null -> {
                    res.status(400)
                    "$DEVICE_ID not provided as a query parameter."
                }
                deviceId.toIntOrNull() == null -> {
                    res.status(400)
                    "$DEVICE_ID must be an integer."
                }
                else -> {
                    res.type("application/json")
                    try {
                        getDeviceBundle(deviceId.toInt())
                    } catch (e: IllegalStateException) {
                        res.status(400)
                        e.message
                    }
                }
            }
        }
    }

}
