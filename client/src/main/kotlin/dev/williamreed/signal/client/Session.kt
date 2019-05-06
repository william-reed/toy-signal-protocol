package dev.williamreed.signal.client

import org.whispersystems.libsignal.SessionBuilder
import org.whispersystems.libsignal.SessionCipher
import org.whispersystems.libsignal.SignalProtocolAddress
import org.whispersystems.libsignal.protocol.PreKeySignalMessage
import org.whispersystems.libsignal.state.PreKeyBundle

/**
 * Session
 *
 * A session connecting two clients. Presumably the destAddress and destPreKey will be received over the wire
 */
class Session(sender: Client, destAddress: SignalProtocolAddress, destPreKey: PreKeyBundle) {
    private val cipher: SessionCipher

    init {
        // Instantiate a SessionBuilder for a remote recipientId + deviceId tuple.
        val sessionBuilder = SessionBuilder(sender.store, destAddress)
        // Build a session with a PreKey retrieved from the server.
        sessionBuilder.process(destPreKey)

        cipher = SessionCipher(sender.store, destAddress)
    }

    fun encrypt(msg: String) = PreKeySignalMessage(cipher.encrypt(msg.toByteArray()).serialize())
    fun decrypt(msg: PreKeySignalMessage) = String(cipher.decrypt(msg))
}
