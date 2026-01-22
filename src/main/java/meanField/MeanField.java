package meanField;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * SIRモデルの平均場
 *
 * @author tadaki
 */
public class MeanField {

    private EpidemicState state;//一つの時間での状態
    private final double beta;//感染確率
    private final double gamma;//治癒確率
    private final double iInit;//初期感染個体数
    private final double pop;//個体密度
    private final int z = 4;//隣接セル数
    private final List<EpidemicState> stateSequence;//状態の時間変化列
    static public String nl = System.getProperty("line.separator");

    /**
     * コンストラクタ
     *
     * @param beta 感染確率
     * @param gamma 治癒確率
     * @param iInit 感染個体の密度の初期値
     * @param pop 個体密度
     */
    public MeanField(double beta, double gamma, double iInit, double pop) {
        this.beta = beta;
        this.gamma = gamma;
        this.iInit = iInit;
        this.pop = pop;
        state = new EpidemicState(pop - iInit, iInit, 0., z);
        stateSequence = Collections.synchronizedList(new ArrayList<>());
        stateSequence.add(state);
    }

    /**
     * 状態更新
     *
     * @return
     */
    public EpidemicState update() {
        state = state.update(beta, gamma);
        stateSequence.add(state);
        return state;
    }

    /**
     * 状態の時間変化列
     *
     * @return
     */
    public List<EpidemicState> getStateSequence() {
        return stateSequence;
    }

    public double getBeta() {
        return beta;
    }

    public double getGamma() {
        return gamma;
    }

    public double getiInit() {
        return iInit;
    }

    public double getPop() {
        return pop;
    }

    /**
     * ファイルに書き込む際のヘッダ部生成
     *
     * @param sys 伝染病システム
     * @return
     */
    static public String createHeader(MeanField sys) {
        StringBuilder sb = new StringBuilder();
        sb.append("#Date:").append(new Date().toString()).append(nl);
        sb.append("#init I=").append(sys.getiInit()).append(nl);
        sb.append("#beta=").append(sys.getBeta()).append(nl);
        sb.append("#gamma=").append(sys.getGamma()).append(nl);
        sb.append("#").append(nl);
        sb.append("#t     I      S").append(nl);
        return sb.toString();
    }

    /**
     * @param args the command line arguments
     * @throws java.io.IOException
     */
    public static void main(String[] args) throws IOException {
        double population = 0.8;
        double iInit = 0.05;
        double beta = 0.1;
//        double gamma = 0.4;
        double gamma = 0.1;
        int tmax = 100;
        MeanField sys = new MeanField(beta, gamma, iInit, population);
        for (int t = 0; t < tmax; t++) {
            sys.update();
        }
        //状態の時間変化列の出力
        List<EpidemicState> list = sys.getStateSequence();
        String filename = "meanField-1.txt";
        try (PrintStream out = new PrintStream(new FileOutputStream(filename))) {
            for (int t = 0; t < list.size(); t++) {
                out.println(t + " " + list.get(t).getpI() + " " + list.get(t).getpS());
            }
        }
    }

}
