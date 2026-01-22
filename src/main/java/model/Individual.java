package model;

import java.util.List;

/**
 * 個体のクラス
 *
 * @author tadaki
 */
public class Individual {

    //個体の状態
    static public enum State {
        S, I, R;
    }
    private State state;//現在の状態
    private State nextState;//次の状態

    /**
     * 状態を指定して初期化
     *
     * @param state
     */
    public Individual(State state) {
        this.state = state;
    }

    /**
     * 現在のセルの状態から次の時刻のセルの状態を計算
     *
     * @param neighbours 隣接個体のリスト
     * @param beta 感染確率
     * @param gamma 治癒確率
     * @param r [0,1)の乱数に相当
     * @return
     */
    public State evalNextState(List<Individual> neighbours,
            double beta, double gamma, double r) {
        nextState = state;
        switch (state) {//注目している個体の状態毎に判断
            case S -> {
                //状態がSである場合
                boolean f = false;
                //周囲のセルに状態Iの個体が居るか
                for (Individual neighbour : neighbours) {
                    f = f | (neighbour.state == State.I);
                }
                if (f && (r < beta)) {
                    nextState = State.I;
                }//病気に罹る
            }
            case I -> {
                //状態がIである場合
                if (r < gamma) {
                    nextState = State.R;
                }//病気が治る
            }
            default -> {
            }
        }
        //注目している個体の状態毎に判断
        return nextState;
    }

    /**
     * 本当に状態を更新する
     *
     */
    public void update() {
        state = nextState;
    }

    public State getState() {
        return state;
    }

}
