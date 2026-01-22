package model;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Date;
import java.util.List;

/**
 * 状態SとIの個体数の時間変化をファイルへ出力
 *
 * @author tadaki
 */
public class Save2File {

    private Save2File() {//インスタンスを作ることができない
    }
    //改行コード
    static public String nl = System.getProperty("line.separator");

    /**
     * 指定した状態にある個体数の変化をファイルへ出力
     *
     * @param sys 伝染病システム
     * @throws IOException
     */
    static public void printSequence(EpidemicDynamics sys)
            throws IOException {
        String filename = EpidemicDynamics.class.getSimpleName() + ".txt";
        printSequence(sys, filename);
    }

    /**
     * 指定した状態にある個体数の変化をファイルへ出力
     *
     * @param sys 伝染病システム
     * @param filename 出力先
     * @throws IOException
     */
    static public void printSequence(EpidemicDynamics sys,
            String filename) throws IOException {
        List<MacroState> list = sys.getSequence();

        //ヘッダー部を出力
        double size = sys.getN() * sys.getN();
        try (PrintStream out = new PrintStream(new FileOutputStream(filename))) {
            //ヘッダー部を出力
            String header = createHeader(sys);
            out.println(header);
            //時刻毎に出力
            for (int t = 0; t < list.size(); t++) {
                double pI = list.get(t).I / size;//状態Iの個体の割合
                double pS = list.get(t).S / size;//状態Sの個体の割合
                out.println(t+" "+pI+" "+pS);
            }
        }
    }

    /**
     * ファイルに書き込む際のヘッダ部生成
     *
     * @param sys 伝染病システム
     * @param s 状態の指定
     * @return
     */
    static private String createHeader(EpidemicDynamics sys) {
        StringBuilder sb = new StringBuilder();
        sb.append("#Date:").append(new Date().toString()).append(nl);
        sb.append("#").append(sys.getN()).append("x");
        sb.append(sys.getN()).append(nl);
        sb.append("#population=").append(sys.getnPop()).append(nl);
        sb.append("#init I=").append(sys.getInitI()).append(nl);
        double pInitI = (double) sys.getInitI() / sys.getN() / sys.getN();
        sb.append("#init_I/N/N=").append(pInitI).append(nl);
        sb.append("#beta=").append(sys.getBeta()).append(nl);
        sb.append("#gamma=").append(sys.getGamma()).append(nl);
        sb.append("#").append(nl);
        sb.append("#t     I      S").append(nl);
        return sb.toString();
    }
}
