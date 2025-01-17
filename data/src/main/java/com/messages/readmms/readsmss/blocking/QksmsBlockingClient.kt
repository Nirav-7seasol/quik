
package com.messages.readmms.readsmss.blocking

import com.messages.readmms.readsmss.repository.BlockingRepository
import io.reactivex.Completable
import io.reactivex.Single
import javax.inject.Inject

class QksmsBlockingClient @Inject constructor(
    private val blockingRepo: BlockingRepository
) : BlockingClient {

    override fun isAvailable(): Boolean = true

    override fun getClientCapability() = BlockingClient.Capability.BLOCK_WITHOUT_PERMISSION

    override fun shouldBlock(address: String): Single<BlockingClient.Action> = isBlacklisted(address)

    override fun isBlacklisted(address: String): Single<BlockingClient.Action> = Single.fromCallable {
        when (blockingRepo.isBlocked(address)) {
            true -> BlockingClient.Action.Block()
            false -> BlockingClient.Action.Unblock
        }
    }

    override fun block(addresses: List<String>): Completable = Completable.fromCallable {
        blockingRepo.blockNumber(*addresses.toTypedArray())
    }

    override fun unblock(addresses: List<String>): Completable = Completable.fromCallable {
        blockingRepo.unblockNumbers(*addresses.toTypedArray())
    }

    override fun openSettings() = Unit // TODO: Do this here once we implement AndroidX navigation

}
