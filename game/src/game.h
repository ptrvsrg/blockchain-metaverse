#ifndef _game_h_
#define _game_h_

#include "state.h"

/**
 * @brief глобальное положение игрока нужное для реализации нативных методов
*/
extern state_t state;

/**
 * @brief Функция, запускающая игру
 * 
 * @param state начальное состояние игрока.
*/
int run(state_t state);

#endif
