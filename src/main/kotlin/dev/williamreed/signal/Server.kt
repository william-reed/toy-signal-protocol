package dev.williamreed.signal

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
     * @param bundle [SerializablePreKeysBundle] of keys and other info
     */
    fun register(bundle: SerializablePreKeysBundle) {
        devices[bundle.deviceId] = bundle
    }

    /**
     * Get a bundle for the given device destination. Removes the pre key from the pool for the device
     */
    fun getDeviceBundle(deviceId: Int): SerializablePreKeyBundle {
        val preKeysBundle = devices[deviceId] ?: error("device id `$deviceId` does not exist.")
        val nextPreKey = preKeysBundle.publicPreKeys.first()

        // remove this key
        devices[deviceId] = preKeysBundle.copy(publicPreKeys = preKeysBundle.publicPreKeys.rest())

        return SerializablePreKeyBundle(
            preKeysBundle.localRegistrationId,
            deviceId,
            nextPreKey,
            preKeysBundle.signedPublicPreKey,
            preKeysBundle.signedPreKeySignature,
            preKeysBundle.identityPublicKey
        )
    }
}
