package meanField;

/**
 * 平均場におけるSIRモデルの状態
 *
 * @author tadaki
 */
public class EpidemicState {

    private final double pS;
    private final double pI;
    private final double pR;
    private final int z;

    /**
     * コンストラクタ
     *
     * @param pS 状態Sの確率
     * @param pI 状態Iの確率
     * @param pR 状態Rの確率
     * @param z 隣接個体数
     */
    public EpidemicState(double pS, double pI, double pR, int z) {
        this.pS = pS;
        this.pI = pI;
        this.pR = pR;
        this.z = z;
    }

    /**
     * 状態更新
     *
     * @param beta 感染確率
     * @param gamma 治癒確率
     * @return 新しい状態
     */
    public EpidemicState update(double beta, double gamma) {
        double q = 1. - Math.pow(1 - pI, (double) z);
        double nS = pS - beta * q * pS;
        double nI = pI + beta * q * pS - gamma * pI;
        double nR = pR + gamma * pI;
        return new EpidemicState(nS, nI, nR, z);
    }

    //setters and getters
    public double getpS() {
        return pS;
    }

    public double getpI() {
        return pI;
    }

    public double getpR() {
        return pR;
    }

}
