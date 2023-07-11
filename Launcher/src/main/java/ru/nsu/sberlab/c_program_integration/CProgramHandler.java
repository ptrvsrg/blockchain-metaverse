package ru.nsu.sberlab.c_program_integration;

/**
 * Программа на си вызывает методы из этого класса, для
 * записи изменений в блокчейн
 */
public class CProgramHandler {

    /**
     * Получает изменение блока и отправляет его на запись в блокчейн.
     *
     * @param p  значение параметра p блока
     * @param q  значение параметра q блока
     * @param x  значение параметра x блока
     * @param y  значение параметра y блока
     * @param z  значение параметра z блока
     * @param w  значение параметра w блока
     */
    public static void getBlockChange(int p, int q, int x, int y, int z, int w){
        Block block = new Block(p, q, x, y, z, w);
        //TODO отправить на запись блок
    }

    /**
     * Получает изменение состояния и отправляет его на запись в блокчейн.
     *
     * @param x   значение параметра x состояния
     * @param y   значение параметра y состояния
     * @param z   значение параметра z состояния
     * @param rx  значение поворота по оси х
     * @param rz  значение поворота по оси Y
     */
    public static void getStateChange(int x, int y, int z, int rx, int rz){
        State state =  new State(x, y, z, rx, rz);
        //TODO отправить на запись положение
    }
}
