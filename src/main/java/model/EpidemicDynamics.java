package model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * 伝染病のCAモデル
 *
 * @author tadaki
 */
public class EpidemicDynamics {

    private final int n;//システムサイズ
    private final int nPop;//個体の総数
    private final int initI;//状態Iの初期個体数
    private final double beta;//感染確率
    private final double gamma;//治癒確率
    private final Random random;

    private List<MacroState> sequence;//個体数の変化

    private final Individual[] individuals;//個体の配列

    /**
     * コンストラクタ
     *
     * @param n システムサイズはn x n
     * @param nPop 個体数
     * @param initI 状態Iの初期個体数
     * @param beta 感染確率
     * @param gamma 治癒確率
     */
    public EpidemicDynamics(int n, int nPop, int initI,
            double beta, double gamma, Random random) {
        this.n = n;
        this.nPop = nPop;
        this.initI = initI;
        this.beta = beta;
        this.gamma = gamma;
        individuals = new Individual[n * n];
        this.random = random;
    }

    /**
     * 初期化
     */
    public void initialize() {
        // 0からn*n-1の整数をでたらめに並べ替えた数列を配列として返す
        int randomIntegers[] = createRandomNumberArray(n * n, nPop);
        //randamIntegersを使って状態を初期化
        setRandomState(randomIntegers, initI, nPop);
        sequence = Collections.synchronizedList(new ArrayList<>());
        //初期の状態IとSの個体数を記録
        sequence.add(countMacroState());
    }

    /**
     * 0からn-1までの数字をでたらめに並べ、先頭のm個を返す
     *
     * @param n
     * @param m
     * @return
     */
    private int[] createRandomNumberArray(int n, int m) {
        var input = new ArrayList<Integer>();
        for (int i = 0; i < n; i++) {
            input.add(i);
        }
        Collections.shuffle(input, random);
        var output = new int[m];
        for (int i = 0; i < m; i++) {
            output[i] = input.get(i);
        }
        return output;
    }

    /**
     * でたらめな初期状態を生成
     *
     * @param randomIntegers セルのインデクスをでたらめに並べ替えた配列
     * @param initI 状態Iである個体数の初期値
     * @param nPop 個体数
     */
    private void setRandomState(int randomIntegers[],
            int initI, int nPop) {
        Individual.State[] states = createRandomState(randomIntegers,
                initI, nPop, individuals.length);
        for (int i = 0; i < states.length; i++) {
            if (states[i] != null) {
                individuals[i] = new Individual(states[i]);
            }
        }
    }

    /**
     * 0からn*n-1の整数をでたらめに並べ替えた数列を
     *
     * 入力配列として状態を初期化
     *
     * @param randomIntegers
     * @param initI 初期の状態Iの個体数
     * @param nPop 個体数の総数
     * @param nSite
     * @return
     */
    static public Individual.State[] createRandomState(
            int randomIntegers[], int initI, int nPop, int nSite) {
        Individual.State states[] = new Individual.State[nSite];
        //初期の状態IをinitI 個配置
        for (int i = 0; i < initI; i++) {
            int k = randomIntegers[i];
            states[k] = Individual.State.I;
        }
        //初期の状態SをnPop-initI個配置
        for (int i = initI; i < nPop; i++) {
            int k = randomIntegers[i];
            states[k] = Individual.State.S;
        }
        return states;
    }

    /**
     * 状態更新
     *
     * @return 状態Iの個体数
     */
    public int update() {
        //各個体について、次の状態を計算
        for (int y = 0; y < n; y++) {
            for (int x = 0; x < n; x++) {
                int k = y * n + x;
                //隣接個体
                List<Individual> neighbours = getNeighbours(x, y);
                if (individuals[k] != null) {
                    individuals[k].evalNextState(
                            neighbours, beta, gamma, random.nextDouble());
                }
            }
        }
        //各個体の状態更新
        for (Individual ind : individuals) {
            if (ind != null) {
                ind.update();
            }
        }
        //状態IとSの個体数を記録
        MacroState macroState = countMacroState();
        sequence.add(macroState);
        return macroState.I;//状態Iの個体数
    }

    /**
     * 隣接サイトの個体を求める
     *
     * @param x サイトのx座標
     * @param y サイトのy座標
     * @return 隣接個体のリスト
     */
    private List<Individual> getNeighbours(int x, int y) {
        int yu = (y - 1 + n) % n;//下の位置
        int yd = (y + 1) % n;//上の位置
        int xr = (x + 1) % n;//右の位置
        int xl = (x - 1 + n) % n;//左の位置
        int north = yu * n + x;
        int south = yd * n + x;
        int west = y * n + xl;
        int east = y * n + xr;
        Individual[] neighbours = new Individual[4];
        neighbours[0] = individuals[north];//上の個体
        neighbours[1] = individuals[west];//下の個体
        neighbours[2] = individuals[east];//右の個体
        neighbours[3] = individuals[west];//左の個体

        List<Individual> list = Collections.synchronizedList(new ArrayList<>());
        for (Individual neighbour : neighbours) {
            if (neighbour != null) {
                list.add(neighbour);
            }
        }
        return list;
    }

    /**
     * 状態IとSの個体数を数える
     *
     * @return 状態IとSの個体数
     */
    public MacroState countMacroState() {
        int countI = 0;
        int countS = 0;
        for (Individual ind : individuals) {
            if (ind != null) {
                Individual.State s = ind.getState();
                switch (s) {
                    case S ->
                        countS++;
                    case I ->
                        countI++;
                    default -> {
                    }
                }

            }
        }
        return new MacroState(countS, countI);
    }

    /**
     * 指定した位置の個体の状態を返す
     *
     * @param x
     * @param y
     * @return
     */
    public Individual.State getState(int x, int y) {
        int r = n * y + x;
        if (individuals[r] == null) {
            return null;
        }
        return individuals[r].getState();
    }

    /**
     * 個体数の時間変化
     *
     * @return
     */
    public List<MacroState> getSequence() {
        return sequence;
    }

    // setters and getters
    public int getN() {
        return n;
    }

    public int getnPop() {
        return nPop;
    }

    public int getInitI() {
        return initI;
    }

    public double getBeta() {
        return beta;
    }

    public double getGamma() {
        return gamma;
    }

}
