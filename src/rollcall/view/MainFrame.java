package rollcall.view;

import rollcall.service.RollCallService;
import rollcall.service.StatisticsService;

import javax.swing.*;

public class MainFrame extends JFrame {

    private final RollCallService rollCallService = new RollCallService();
    private final StatisticsService statisticsService = new StatisticsService();

    public MainFrame() {
        setTitle("基于有状态的课堂点名系统 v1.0");
        setSize(900, 650);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("学生管理", new StudentPanel(rollCallService));
        tabbedPane.addTab("课堂点名", new RollCallPanel(rollCallService));
        tabbedPane.addTab("数据统计", new StatisticsPanel(statisticsService));

        add(tabbedPane);
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {}

        SwingUtilities.invokeLater(() -> {
            new MainFrame().setVisible(true);
        });
    }
}
