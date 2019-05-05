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
class Client(val name: String, val deviceId: Int) {
    val store: SignalProtocolStore
    val address = SignalProtocolAddress(name, deviceId)
    private var preKeyId = 0
    private val signedPublicPreKey: Pair<Int, String>

    init {
        this.store = genIdentity()

        // only one of these will be used
        signedPublicPreKey = genSignedPreKey()
    }

    /**
     * Generate the identity and create a store for it
     */
    private fun genIdentity() =
        InMemorySignalProtocolStore(
            KeyHelper.generateIdentityKeyPair(),
            KeyHelper.generateRegistrationId(false)
        )

    /**
     * Generate and store signed pre key
     * @return a pair of key id and Base64 encoded string of the public key
     */
    private fun genSignedPreKey(): Pair<Int, String> {
        val signedPreKey = KeyHelper.generateSignedPreKey(store.identityKeyPair, SIGNED_PRE_KEY_ID)
        // store signed keys
        store.storeSignedPreKey(signedPreKey.id, signedPreKey)

        return signedPreKey.id to String(Base64.getEncoder().encode(signedPreKey.keyPair.publicKey.serialize()))
    }

    /**
     * Generate and store pre keys
     * @return a list of pair of key id and the Base64 encoded string of the public keys
     */
    private fun genPreKeys(): List<Pair<Int, String>> {
        val preKeys = KeyHelper.generatePreKeys(preKeyId, PRE_KEY_COUNT)
        // store pre keys
        preKeys.forEach {
            store.storePreKey(it.id, it)
        }
        preKeyId += PRE_KEY_COUNT

        return preKeys.map { it.id to String(Base64.getEncoder().encode(it.keyPair.publicKey.serialize())) }
    }

    /**
     * Get the next pre keys bundle. Generates a new batch of unsigned pre keys for each of these
     */
    fun nextPreKeysBundle() =
        SerializablePreKeysBundle(
            store.localRegistrationId,
            deviceId,
            genPreKeys(),
            signedPublicPreKey,
            String(Base64.getEncoder().encode(store.loadSignedPreKey(SIGNED_PRE_KEY_ID).signature)),
            String(Base64.getEncoder().encode(store.identityKeyPair.publicKey.serialize()))
        )


    companion object {
        const val PRE_KEY_COUNT = 100
        // TODO: we only ever need one signed pre key right?
        const val SIGNED_PRE_KEY_ID = 123456
    }
}
