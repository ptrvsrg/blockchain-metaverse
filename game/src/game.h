#ifndef _game_h_
#define _game_h_

#include "state.h"

/**
 * @brief глобальное положение игрока нужное для реализации нативных методов
*/
extern state_t state;

int run(state_t);

#endif
