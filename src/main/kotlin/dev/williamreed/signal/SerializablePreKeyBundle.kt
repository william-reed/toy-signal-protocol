package dev.williamreed.signal

import kotlinx.serialization.Serializable
import org.whispersystems.libsignal.IdentityKey
import org.whispersystems.libsignal.state.PreKeyBundle
import java.util.*

/**
 * Serializable version of a key bundle to be sent to the server. Containing everything needed to reconstruct a pre key
 * bundle on another client
 *
 * @param localRegistrationId the client's registration ID. should not change throughout the life time. [org.whispersystems.libsignal.state.IdentityKeyStore.getLocalRegistrationId]
 * @param deviceId the device id
 * @param publicPreKeys list of pair of key id and Base64 encoded public key
 * @param signedPublicPreKey pair of key id and Base64 encoded signed public key
 * @param signedPreKeySignature Base64 encoded signed public key signature
 * @param identityPublicKey Base64 encoded identity public key
 */
@Serializable
data class SerializablePreKeysBundle(
    val localRegistrationId: Int,
    val deviceId: Int,
    val publicPreKeys: List<Pair<Int, String>>,
    val signedPublicPreKey: Pair<Int, String>,
    val signedPreKeySignature: String,
    val identityPublicKey: String
)

/**
 * Serializable version of a key bundle to be sent to the server. Containing everything needed to reconstruct a pre key
 * bundle on another client. Just like [SerializablePreKeysBundle] except this only holds one key
 *
 * @param localRegistrationId the client's registration ID. should not change throughout the life time. [org.whispersystems.libsignal.state.IdentityKeyStore.getLocalRegistrationId]
 * @param deviceId the device id
 * @param publicPreKey pair of key id and Base64 encoded public key
 * @param signedPublicPreKey pair of key id and Base64 encoded signed public key
 * @param signedPreKeySignature Base64 encoded signed public key signature
 * @param identityPublicKey Base64 encoded identity public key
 */
@Serializable
data class SerializablePreKeyBundle(
    val localRegistrationId: Int,
    val deviceId: Int,
    val publicPreKey: Pair<Int, String>,
    val signedPublicPreKey: Pair<Int, String>,
    val signedPreKeySignature: String,
    val identityPublicKey: String
) {
    fun toPreKeyBundle() {
        val decoder = Base64.getDecoder()

        val preKeyPublic = IdentityKey(decoder.decode(publicPreKey.second), 0).publicKey
        val signedPreKeyPublic = IdentityKey(decoder.decode(signedPublicPreKey.second), 0)

        PreKeyBundle(
            localRegistrationId,
            deviceId,
            publicPreKey.first,
            preKeyPublic,
            signedPublicPreKey.first,
            signedPreKeyPublic.publicKey,
            decoder.decode(signedPreKeySignature),
            IdentityKey(decoder.decode(identityPublicKey), 0)
        )
    }

    companion object {
        fun fromPreKeyBundle(bundle: PreKeyBundle) =
            SerializablePreKeyBundle(
                bundle.registrationId,
                bundle.deviceId,
                Pair(bundle.preKeyId, String(Base64.getEncoder().encode(bundle.preKey.serialize()))),
                Pair(bundle.signedPreKeyId, String(Base64.getEncoder().encode(bundle.signedPreKey.serialize()))),
                String(Base64.getEncoder().encode(bundle.signedPreKeySignature)),
                String(Base64.getEncoder().encode(bundle.identityKey.serialize()))
            )
    }
}
