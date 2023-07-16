#include "JNI_Launcher.h"
#include "start_game.h"
#include "sync_queue.h"

JNIEXPORT void JNICALL Java_ru_nsu_sberlab_launcher_Launcher_start(
    JNIEnv* env, jclass
) {
    start_game();
}

JNIEXPORT void JNICALL Java_ru_nsu_sberlab_launcher_Launcher_init(JNIEnv* env, jclass) {
    queue_init(&in_blockchain_queue);
    queue_init(&out_blockchain_queue);
}

JNIEXPORT void JNICALL Java_ru_nsu_sberlab_launcher_Launcher_destroy(JNIEnv* env, jclass) {
    queue_destroy(&in_blockchain_queue);
    queue_destroy(&out_blockchain_queue);
}
