#include "JNI_CDataExtractor.h"
#include "start_game.h"

JNIEXPORT jobject JNICALL Java_ru_nsu_sberlab_cprogramintegration_CDataExtractor_getBlockChangeC(
    JNIEnv* env,
    jclass
) {
    sync_queue_entry_t entry = dequeue(&in_blockchain_queue);
    jclass block_class = (*env)->FindClass(env, "ru/nsu/sberlab/cprogramintegration/Block");
    jmethodID block_constructor = (*env)->GetMethodID(env, block_class, "<init>", "(IIIIII)V");
    jobject block_inctance = (*env)->NewObject(env, block_class, block_constructor,
        entry.m_chunk_x, entry.m_chunk_z,
        entry.m_block_x, entry.m_block_y, entry.m_block_z,
        entry.m_block_id
    );

    return block_inctance;
}

JNIEXPORT jobject JNICALL Java_ru_nsu_sberlab_cprogramintegration_CDataExtractor_getStateChangeC(
    JNIEnv* env,
    jclass
) {
    //....
}
