package dev.williamreed.signal

import kotlinx.serialization.json.Json

/**
 * Server
 *
 * Simulating the job of a server holding public key information from various clients
 */
class Server {
    // map of device ids and bundle
    private val devices = mutableMapOf<Int, SerializablePreKeysBundle>()

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

        return Json.stringify(SerializablePreKeyBundle.serializer(), SerializablePreKeyBundle(
            preKeysBundle.localRegistrationId,
            deviceId,
            nextPreKey,
            preKeysBundle.signedPublicPreKey,
            preKeysBundle.signedPreKeySignature,
            preKeysBundle.identityPublicKey
        ))
    }
}
