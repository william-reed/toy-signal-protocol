package dev.williamreed.signal

import org.whispersystems.libsignal.SessionCipher
import org.whispersystems.libsignal.SignalProtocolAddress
import org.whispersystems.libsignal.state.PreKeyBundle
import org.whispersystems.libsignal.state.SignalProtocolStore
import org.whispersystems.libsignal.state.impl.InMemorySignalProtocolStore
import org.whispersystems.libsignal.util.KeyHelper

/**
 * Client
 *
 * A client entity with its own keys
 */
class Client(name: String, deviceId: Int) {
    val store: SignalProtocolStore
    val address = SignalProtocolAddress(name, deviceId)
    val preKey: PreKeyBundle

    init {
        this.store = generateIdentity()
        // not sure about these ids
        val preKeyId = 0
        val signedPreKeyId = 0
        generatePreKeyIdentity(preKeyId, signedPreKeyId)

        val pubicPreKey = store.loadPreKey(preKeyId).keyPair.publicKey
        val signedPreKey = store.loadSignedPreKey(signedPreKeyId)
        val publicSignedPreKey = signedPreKey.keyPair.publicKey
        val identityKey = store.identityKeyPair.publicKey
        preKey = PreKeyBundle(
            store.localRegistrationId,
            deviceId,
            preKeyId,
            pubicPreKey,
            signedPreKeyId,
            publicSignedPreKey,
            signedPreKey.signature,
            identityKey
        )
    }

    /**
     * Generate the identity and create a store for it
     */
    private fun generateIdentity() =
        InMemorySignalProtocolStore(
            KeyHelper.generateIdentityKeyPair(),
            KeyHelper.generateRegistrationId(false)
        )

    /**
     * Generate and store pre key
     */
    private fun generatePreKeyIdentity(preKeyId: Int, signedPreKeyId: Int) {
        // TODO: store in circular queue and gen more when low
        val preKeys = KeyHelper.generatePreKeys(preKeyId, 100)
        val signedPreKey = KeyHelper.generateSignedPreKey(store.identityKeyPair, signedPreKeyId)

        // store pre keys
        preKeys.forEach {
            store.storePreKey(it.id, it)
        }
        // store signed keys
        store.storeSignedPreKey(signedPreKey.id, signedPreKey)
    }
}
