package dev.williamreed.signal

import org.whispersystems.libsignal.SignalProtocolAddress
import org.whispersystems.libsignal.state.PreKeyBundle
import org.whispersystems.libsignal.state.PreKeyRecord
import org.whispersystems.libsignal.state.SignalProtocolStore
import org.whispersystems.libsignal.state.impl.InMemorySignalProtocolStore
import org.whispersystems.libsignal.util.KeyHelper
import java.util.*

/**
 * Client
 *
 * A client entity with its own keys
 */
class Client(name: String, private val deviceId: Int) {
    val store: SignalProtocolStore
    val address = SignalProtocolAddress(name, deviceId)
    private val preKeys = ArrayDeque<PreKeyRecord>()
    private var preKeyId = 0

    init {
        this.store = generateIdentity()
        generatePreKeyIdentity()
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
    private fun generatePreKeyIdentity() {
        val signedPreKey = KeyHelper.generateSignedPreKey(store.identityKeyPair, SIGNED_PRE_KEY_ID)
        generatePreKeys()

        // store signed keys
        store.storeSignedPreKey(signedPreKey.id, signedPreKey)
    }

    /**
     * Generate pre keys
     */
    private fun generatePreKeys() {
        val preKeys = KeyHelper.generatePreKeys(preKeyId, PRE_KEY_COUNT)
        // store pre keys
        preKeys.forEach {
            store.storePreKey(it.id, it)
            this.preKeys.push(it)
        }
        preKeyId += PRE_KEY_COUNT
    }

    /**
     * Get the next pre key bundle to use
     */
    fun nextPreKey(): PreKeyBundle {
        if (preKeys.isEmpty())
            generatePreKeys()

        val preKey = preKeys.pop()
        val signedPreKey = store.loadSignedPreKey(SIGNED_PRE_KEY_ID)

        return PreKeyBundle(
            store.localRegistrationId,
            deviceId,
            preKey.id,
            preKey.keyPair.publicKey,
            SIGNED_PRE_KEY_ID,
            signedPreKey.keyPair.publicKey,
            signedPreKey.signature,
            store.identityKeyPair.publicKey
        )
    }

    companion object {
        const val PRE_KEY_COUNT = 100
        // TODO: we only ever need one pre key right?
        const val SIGNED_PRE_KEY_ID = 123456
    }
}
