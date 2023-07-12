#include "JNI_BlockchainHandler.h"
#include "start_game.h"

JNIEXPORT void JNICALL Java_ru_nsu_sberlab_cprogramintegration_BlockchainHandler_sendBlockChangeC(
    JNIEnv* env, jclass,
    jint p, jint q,
    jint x, jint y, jint z,
    jint w
) {
    sync_queue_entry_t entry;
    entry.m_chunk_x = (int)p;
    entry.m_chunk_z = (int)q;
    entry.m_block_x = (int)x;
    entry.m_block_y = (int)y;
    entry.m_block_z = (int)z;
    entry.m_block_id = (int)w;
    enqueue(&out_blockchain_queue, entry);
}

JNIEXPORT void JNICALL Java_ru_nsu_sberlab_cprogramintegration_BlockchainHandler_sendStateChangeC(
    JNIEnv* env, jclass,
    jint x, jint y, jint z,
    jint rx, jint rz
) {
    //....
}