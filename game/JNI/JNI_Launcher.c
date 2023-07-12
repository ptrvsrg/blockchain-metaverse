#include "JNI_Launcher.h"
#include "start_game.h"

extern int main(void);

JNIEXPORT void JNICALL Java_ru_nsu_sberlab_launcher_Launcher_start(
    JNIEnv* env, jclass
) {
    start_game();
}
