#include "StartTask.h"
#include "sync_queue.h"
#include "game.h"

JNIEXPORT void JNICALL Java_ru_nsu_sberlab_gameintegration_tasks_StartTask_start
(JNIEnv* env, jobject, jobject playerPosition) {
    queue_init(&in_blockchain_queue);
    queue_init(&out_blockchain_queue);
    state_t state;
    jclass PlayerPosition = (*env)->GetObjectClass(env, playerPosition);
    jmethodID getX = (*env)->GetMethodID(env, PlayerPosition, "getX", "()F");
    jmethodID getY = (*env)->GetMethodID(env, PlayerPosition, "getY", "()F");
    jmethodID getZ = (*env)->GetMethodID(env, PlayerPosition, "getZ", "()F");
    jmethodID getRx = (*env)->GetMethodID(env, PlayerPosition, "getRx", "()F");
    jmethodID getRy = (*env)->GetMethodID(env, PlayerPosition, "getRy", "()F");
    state.x = (float)(*env)->CallFloatMethod(env, playerPosition, getX);
    state.y = (float)(*env)->CallFloatMethod(env, playerPosition, getY);
    state.z = (float)(*env)->CallFloatMethod(env, playerPosition, getZ);
    state.rx = (float)(*env)->CallFloatMethod(env, playerPosition, getRx);
    state.ry = (float)(*env)->CallFloatMethod(env, playerPosition, getRy);
    run(state);
    queue_destroy(&in_blockchain_queue);
    queue_destroy(&out_blockchain_queue);
}
