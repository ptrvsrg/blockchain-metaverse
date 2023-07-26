#include "PlayerPositionHandler.h"
#include "game.h"

#include <stdio.h>

JNIEXPORT jobject JNICALL Java_ru_nsu_sberlab_gameintegration_PlayerPositionHandler_getPlayerPositionC
(JNIEnv* env, jclass) {
    jclass PlayerPosition = (*env)->FindClass(env, "ru/nsu/sberlab/gameintegration/data/PlayerPosition");
    jmethodID init = (*env)->GetMethodID(env, PlayerPosition, "<init>", "(FFFFF)V");
    jobject playerPosition = (*env)->NewObject(
        env, PlayerPosition, init,
        state.x, state.y, state.z,
        state.rx, state.ry
    );

    return playerPosition;
}
