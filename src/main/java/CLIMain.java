import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.List;
import java.util.Random;
import meanField.EpidemicState;
import meanField.MeanField;
import model.EpidemicDynamics;
import model.Save2File;

/**
 *
 * @author tadaki
 */
public class CLIMain {

    /**
     * @param args the command line arguments
     * @throws java.io.IOException
     */
    public static void main(String[] args) throws IOException {
        int n = 256;//システムサイズはn*n
        double popRatio = 0.8;//セルに対する個体数の割合
        int nPop = (int) (popRatio * n * n);
        double initIRatio = 0.05;//I(0)
        int initI = (int) (initIRatio * n * n);
        double beta = 0.1;//感染確率
//        double gamma = 0.4;//治癒確率
        double gamma = 0.1;//治癒確率
        int tmax = n * n;//最大更新回数
        EpidemicDynamics dynamics
                = new EpidemicDynamics(n, nPop, initI, beta, gamma, new Random(48L));
        dynamics.initialize();
        MeanField sys = new MeanField(beta, gamma, initIRatio, popRatio);
        for (int t = 0; t < tmax; t++) {
            sys.update();
            if (dynamics.update() == 0) {//感染個体が居なくなると停止
                break;
            }
        }
        //結果をファイルへ
        Save2File.printSequence(dynamics);
        //状態の時間変化列の出力
        List<EpidemicState> list = sys.getStateSequence();
        String filename = "meanField.txt";
        try (PrintStream out = new PrintStream(new FileOutputStream(filename))) {
            out.println(MeanField.createHeader(sys));
            for (int t = 0; t < list.size(); t++) {
                out.println(t + " " + list.get(t).getpI() + " " + list.get(t).getpS());
            }
        }
    }

}
