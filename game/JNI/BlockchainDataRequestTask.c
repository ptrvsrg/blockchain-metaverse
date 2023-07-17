#include "BlockchainDataRequestTask.h"
#include "sync_queue.h"

JNIEXPORT void JNICALL Java_ru_nsu_sberlab_gameintegration_tasks_BlockchainDataRequestTask_sendBlockChangeC
(JNIEnv* env, jclass, jobject block) {
    jclass Block = (*env)->GetObjectClass(env, block);
    jmethodID getP = (*env)->GetMethodID(env, Block, "getP", "()I");
    jmethodID getQ = (*env)->GetMethodID(env, Block, "getQ", "()I");
    jmethodID getX = (*env)->GetMethodID(env, Block, "getX", "()I");
    jmethodID getY = (*env)->GetMethodID(env, Block, "getY", "()I");
    jmethodID getZ = (*env)->GetMethodID(env, Block, "getZ", "()I");
    jmethodID getW = (*env)->GetMethodID(env, Block, "getW", "()I");
    sync_queue_entry_t entry;
    entry.m_chunk_x = (int)(*env)->CallIntMethod(env, block, getP);
    entry.m_chunk_z = (int)(*env)->CallIntMethod(env, block, getQ);
    entry.m_block_x = (int)(*env)->CallIntMethod(env, block, getX);
    entry.m_block_y = (int)(*env)->CallIntMethod(env, block, getY);
    entry.m_block_z = (int)(*env)->CallIntMethod(env, block, getZ);
    entry.m_block_id = (int)(*env)->CallIntMethod(env, block, getW);
    enqueue(&out_blockchain_queue, entry);
}
