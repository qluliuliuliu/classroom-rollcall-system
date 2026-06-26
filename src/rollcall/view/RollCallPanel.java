package rollcall.view;

import rollcall.model.RollCallRecord;
import rollcall.model.Student;
import rollcall.service.RollCallService;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class RollCallPanel extends JPanel {

    private final RollCallService service;
    private Student currentStudent;

    private JLabel nameLabel;
    private JLabel infoLabel;
    private JTextArea historyArea;
    private JButton callButton;
    private JButton correctButton;
    private JButton wrongButton;
    private JComboBox<String> courseCombo;

    public RollCallPanel(RollCallService service) {
        this.service = service;
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        top.add(new JLabel("课程:"));
        courseCombo = new JComboBox<>(new String[]{
                "默认课程", "Java程序设计", "数据结构", "操作系统"});
        courseCombo.setEditable(true);
        top.add(courseCombo);
        JButton setBtn = new JButton("设置");
        top.add(setBtn);
        JButton resetBtn = new JButton("重置本轮");
        top.add(resetBtn);
        add(top, BorderLayout.NORTH);

        JPanel center = new JPanel(new BorderLayout());
        JPanel result = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.insets = new Insets(5, 0, 5, 0);

        nameLabel = new JLabel("点击按钮开始点名", SwingConstants.CENTER);
        nameLabel.setFont(new Font("黑体", Font.BOLD, 48));
        nameLabel.setForeground(new Color(220, 50, 50));
        result.add(nameLabel, gbc);

        infoLabel = new JLabel(" ", SwingConstants.CENTER);
        infoLabel.setFont(new Font("宋体", Font.PLAIN, 16));
        result.add(infoLabel, gbc);

        center.add(result, BorderLayout.CENTER);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        callButton = new JButton("点 名");
        callButton.setFont(new Font("黑体", Font.BOLD, 24));
        callButton.setPreferredSize(new Dimension(160, 60));

        correctButton = new JButton("\u2713 答出");
        correctButton.setFont(new Font("黑体", Font.BOLD, 18));
        correctButton.setEnabled(false);

        wrongButton = new JButton("\u2717 未答出");
        wrongButton.setFont(new Font("黑体", Font.BOLD, 18));
        wrongButton.setEnabled(false);

        btnPanel.add(callButton); btnPanel.add(correctButton); btnPanel.add(wrongButton);
        center.add(btnPanel, BorderLayout.SOUTH);
        add(center, BorderLayout.CENTER);

        historyArea = new JTextArea();
        historyArea.setEditable(false);
        historyArea.setFont(new Font("宋体", Font.PLAIN, 12));
        JScrollPane sp = new JScrollPane(historyArea);
        sp.setPreferredSize(new Dimension(250, 0));
        sp.setBorder(BorderFactory.createTitledBorder("点名记录"));
        add(sp, BorderLayout.EAST);

        setBtn.addActionListener(e -> {
            service.setCourse((String) courseCombo.getSelectedItem());
            JOptionPane.showMessageDialog(this, "课程已设置");
        });
        resetBtn.addActionListener(e -> {
            JOptionPane.showMessageDialog(this, "本轮已重置");
        });
        callButton.addActionListener(e -> doCall());
        correctButton.addActionListener(e -> recordAnswer(true));
        wrongButton.addActionListener(e -> recordAnswer(false));

        refreshHistory();
    }

    private void doCall() {
        Student s = service.call();
        if (s == null) {
            JOptionPane.showMessageDialog(this, "学生列表为空，请先添加学生！");
            return;
        }
        currentStudent = s;
        nameLabel.setText(s.getName());
        infoLabel.setText(String.format("学号:%s | 班级:%s | 已点名%d次 答出%d次",
                s.getStudentNo(), s.getClassName(),
                s.getTotalCalled(), s.getTotalAnswered()));
        callButton.setEnabled(false);
        correctButton.setEnabled(true);
        wrongButton.setEnabled(true);
    }

    private void recordAnswer(boolean answered) {
        if (currentStudent == null) return;
        service.recordAnswer(currentStudent, answered);
        refreshHistory();

        callButton.setEnabled(true);
        correctButton.setEnabled(false);
        wrongButton.setEnabled(false);
    }

    private void refreshHistory() {
        List<RollCallRecord> records = service.getRecords();
        StringBuilder sb = new StringBuilder();
        int start = Math.max(0, records.size() - 20);
        for (int i = records.size() - 1; i >= start; i--) {
            RollCallRecord r = records.get(i);
            sb.append(String.format("[%s] %s %s\n",
                    r.getCallTime().substring(11, 19), r.getStudentName(),
                    r.isAnswered() ? "答出" : "未答出"));
        }
        historyArea.setText(sb.toString());
    }
}
